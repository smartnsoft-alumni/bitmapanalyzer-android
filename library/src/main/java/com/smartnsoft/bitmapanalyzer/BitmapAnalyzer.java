// The MIT License (MIT)
//
// Copyright (c) 2017 Smart&Soft
//
// Permission is hereby granted, free of charge, to any person obtaining a copy
// of this software and associated documentation files (the "Software"), to deal
// in the Software without restriction, including without limitation the rights
// to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
// copies of the Software, and to permit persons to whom the Software is
// furnished to do so, subject to the following conditions:
//
// The above copyright notice and this permission notice shall be included in all
// copies or substantial portions of the Software.
//
// THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
// IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
// FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
// AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
// LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
// OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
// SOFTWARE.

package com.smartnsoft.bitmapanalyzer;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;

import com.smartnsoft.droid4me.download.DownloadInstructions.BitmapableBitmap;
import com.smartnsoft.droid4me.download.DownloadSpecs.SizedImageSpecs;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Align;
import android.graphics.Paint.FontMetrics;
import android.graphics.Paint.Style;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;

/**
 * @author Jocelyn Girard
 * @since 2016.03.29
 */
public final class BitmapAnalyzer
{

  public final static class BitmapDownloadAnalyticsSizes
  {

    public final List<Long> bitmapPixelSizes = new ArrayList<>();

    public final List<Long> viewPixelSizes = new ArrayList<>();

    public void addPixels(long bitmapPixelSize, long viewPixelSize)
    {
      bitmapPixelSizes.add(bitmapPixelSize);
      viewPixelSizes.add(viewPixelSize);
    }

    public float computeRatio()
    {
      long viewPixelSize = 0;
      for (long size : viewPixelSizes)
      {
        viewPixelSize += size;
      }
      final float viewPixelSizeRatio = viewPixelSize / viewPixelSizes.size();

      long bitmapPixelSize = 0;
      for (long size : bitmapPixelSizes)
      {
        bitmapPixelSize += size;
      }
      final float bitmapPixelSizeRatio = bitmapPixelSize / bitmapPixelSizes.size();

      return bitmapPixelSizeRatio / viewPixelSizeRatio;
    }
  }

  public static class BitmapDownloadAnalyticsImageSpecs
      extends SizedImageSpecs
  {

    private final static class ColorRGB
    {

      public final float percent;

      public final int r;

      public final int g;

      public final int b;

      private ColorRGB(float percent, int r, int g, int b)
      {
        this.percent = percent;
        this.r = r;
        this.g = g;
        this.b = b;
      }
    }

    private final DecimalFormat decimalFormat;

    final ColorRGB[] percentColors = new ColorRGB[] { new ColorRGB(0f, 0xff, 0, 0xff), new ColorRGB(.8f, 0xff, 0, 0), new ColorRGB(1f, 0, 0xff, 0),
        new ColorRGB(1.2f, 0, 0xff, 0xff), new ColorRGB(2f, 0, 0, 0xff), new ColorRGB(10f, 0, 0, 0xff) };

    public BitmapDownloadAnalyticsImageSpecs(int imageResourceId, int width, int height)
    {
      super(imageResourceId, width, height);
      decimalFormat = new DecimalFormat("#.####");
    }

    public InputStream downloadInputStream(BitmapableBitmap bitmapableBitmap, String bitmapUid, Object imageSpecs, String url)
    {
      if (BitmapAnalyzer.isEnabled == true && bitmapableBitmap.getBitmap() != null)
      {
        final Bitmap bitmap = bitmapableBitmap.getBitmap();
        final int bitmapPixelSize = bitmap.getWidth() * bitmap.getHeight();
        final int viewPixelSize = width * height;
        // if (log.isDebugEnabled() == true)
        // {
        // log.debug("BitmapAnalytics: We save the pixels used between the bitmap image size (" + bitmapPixelSize + "px) and the view size (" +
        // viewPixelSize + "px)");
        // }
        BitmapAnalyzer.bitmapDownloadAnalyticsSizes.addPixels(bitmapPixelSize, viewPixelSize);
        // if (log.isDebugEnabled() == true)
        // {
        // log.debug("BitmapAnalytics: The current ratio between the bitmaps pixels and the views pixels is " +
        // BitmapAnalyzer.bitmapDownloadAnalyticsSizes.computeRatio());
        // }
      }

      final ByteArrayOutputStream stream = new ByteArrayOutputStream();
      bitmapableBitmap.getBitmap().compress(Bitmap.CompressFormat.PNG, 100, stream);
      final byte[] byteArray = stream.toByteArray();
      return new ByteArrayInputStream(byteArray);
    }

    public boolean onBindBitmap(boolean downloaded, View imageView, Bitmap bitmap, String imageUid, Object imageSpecs)
    {
      final float matchingPercent;
      if (BitmapAnalyzer.isEnabled == true)
      {
        if (width == bitmap.getWidth() && height == bitmap.getHeight())
        {
          matchingPercent = 1f;
        }
        else
        {
          final float r1 = (width * 1f) / height;
          final float r2 = (bitmap.getWidth() * 1f) / bitmap.getHeight();
          // matchingPercent = (r1 + r2 + (r1 / r2) + (width / bitmap.getWidth() * 1f) + (height / bitmap.getHeight() * 1f)) / 5f;
          matchingPercent = ((r1 / r2) + (width / bitmap.getWidth() * 1f) + (height / bitmap.getHeight() * 1f)) / 3f;
        }
        // if (log.isDebugEnabled() == true)
        // {
        // log.debug("BitmapAnalytics: The image view size is " + width + "x" + height + " and the bitmap size is " + bitmap.getWidth() + "x" +
        // bitmap.getHeight() + " the matching percent is " + matchingPercent);
        // }
        final Bitmap customBitmap = writeTextOnDrawable(imageView.getContext(), bitmap, matchingPercent, 14);
        ((ImageView) imageView).setImageBitmap(customBitmap);
        // ((ImageView) imageView).setColorFilter(computeColorFromPercent(matchingPercent));
        return true;
      }
      else
      {
        return false;
      }
    }

    public Bitmap writeTextOnDrawable(Context context, Bitmap bitmap, float matchingPercent, int textSize)
    {
      float scale = context.getResources().getDisplayMetrics().density;
      final Bitmap newBitmap = bitmap.copy(Config.ARGB_8888, true);
      final Canvas canvas = new Canvas(newBitmap);
      final FontMetrics fontMetrics = new FontMetrics();
      final Paint paint = new Paint();
      final Rect textRect = new Rect();

      // if (log.isDebugEnabled() == true)
      // {
      // log.debug("BitmapAnalytics: We draw text on bitmap, the used canvas is hardware accelerated ? " + canvas.isHardwareAccelerated());
      // }

      paint.setStyle(Style.FILL);
      paint.setTypeface(Typeface.DEFAULT_BOLD);
      paint.setTextAlign(Align.CENTER);
      paint.getFontMetrics(fontMetrics);
      paint.setAntiAlias(true);
      paint.setTextSize(textSize * scale);
      final String text = decimalFormat.format(matchingPercent).trim();
      paint.getTextBounds(text, 0, text.length(), textRect);

      final int xPosition = (canvas.getWidth() / 2) - 2; // -2 is for regulating the x position offset
      final int yPosition = (canvas.getHeight() / 2) - (textRect.height() / 2);

      final float margin = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, context.getResources().getDisplayMetrics());

      paint.setColor(computeColorFromPercent(matchingPercent));
      canvas.drawRect(xPosition - (textRect.width() / 2f) - margin, yPosition - textRect.height() - margin, xPosition + paint.measureText(text) + margin,
          yPosition + margin, paint);

      paint.setColor(Color.WHITE);
      canvas.drawText(text, xPosition, yPosition, paint);

      return newBitmap;
    }

    private final int computeColorFromPercent(float percent)
    {
      int index = 0;
      for (; index < percentColors.length; index++)
      {
        if (percent <= percentColors[index].percent)
        {
          break;
        }
      }
      final ColorRGB lowerColor = percentColors[index - 1];
      final ColorRGB upperColor = percentColors[index];
      final float range = upperColor.percent - lowerColor.percent;
      final float rangePercent = (percent - lowerColor.percent) / range;
      final float lowerPercent = 1 - rangePercent;
      final float upperPercent = rangePercent;

      return Color.argb(255, // Transparency
          (int) Math.floor(lowerColor.r * lowerPercent + upperColor.r * upperPercent), // R
          (int) Math.floor(lowerColor.g * lowerPercent + upperColor.g * upperPercent), // G
          (int) Math.floor(lowerColor.b * lowerPercent + upperColor.b * upperPercent)); // B
    }

  }

  public final static BitmapDownloadAnalyticsSizes bitmapDownloadAnalyticsSizes = new BitmapDownloadAnalyticsSizes();

  public static boolean isEnabled = false;

  public static void setIsEnabled(boolean isEnabled)
  {
    BitmapAnalyzer.isEnabled = isEnabled;
  }

}
