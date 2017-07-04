# Bitmap Analyzer

Small component that analyze efficiency between ImageViews and it's contents.

This version of the library has no additional dependencies.

## Usage

### From the Smart&Soft Nexus

/!\ Library releases are **NOT** available on Maven Central /!\

**Gradle**

Because the library is available only on the Smart&Soft Nexus, you have to add the following lines to your `build.gradle` project file :

```groovy
allprojects
{
  repositories
  {
    maven
    {
      credentials
      {
        username nexusUsername
        password nexusPassword
      }
      
      url nexusRepositoryUrl
    }
  }
}
```

Then, you can add the following line into the `dependencies` bloc of your `build.gradle` module file :

```groovy
compile ("com.smartnsoft:bitmapanalyzer:1.0")
```

**Maven**

```xml
<dependency>
  <groupId>com.smartnsoft</groupId>
  <artifactId>bitmapanalyzer</artifactId>
  <version>1.0</version>
</dependency>
```

## Author

The Android Team @Smart&Soft, software agency [http://www.smartnsoft.com](http://www.smartnsoft.com)

## License

layoutinflator is available under the MIT license. See the LICENSE file for more info.
