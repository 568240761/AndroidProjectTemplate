# AndroidProjectTemplate
新建Android项目时，需要重新配置，这样会比较太慢；而且有些工具类可以重复使用，粘来粘去又很麻烦；所以就根据以往的项目经验再加上现在很流行的Jetpack整理出了这个快速新建Android项目模板。

项目基本配置：
```groovy
compileSdkVersion 29
buildToolsVersion "29.0.3"

minSdkVersion 21
targetSdkVersion 29
```

在项目中Jetpack使用的模块如下：
- 数据绑定库
- 处理生命周期
- LiveData
- ViewModel

其他模块的可以根据自身需求进行添加。

项目使用的是Android推荐应用架构，架构图如下：
![架构图](img/architecture.png)

注意，该项目并不涉及SQLite,如果需要请自行添加；网络请求库直接使用的okhttp，并没有用retrofit。
## 模块介绍
为啥要分成模块呢？

因为以前在外包公司呆过，看到过很多杂乱无章的项目，代码粘来粘去那是常事，因此也常碰到一些稀奇古怪的Bug。慢慢地就意识到项目如果按功能分模块，不仅维护、复用方便，工作效率也会提高，最关键的稀奇古怪的Bug少了，头发也掉得少了。
### app
### module_basic
### module_util
### module_network
### module_image
## 适配问题
### Android 8.0
#### Activity的透明主题崩溃问题该异常为：
```
Caused by: java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
```
大概意思就是说透明的主题设置了界面方向(android:screenOrientation="")，我们可以去掉屏幕方向或者改为全屏。
```xml
<style name="Translucent_Fullscreen" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowFullscreen">true</item>
</style>
```