# AndroidProjectTemplate
新建Android项目时，需要重新配置，这样会比较太慢；而且有些工具类可以重复使用，粘来粘去又很麻烦；所以就根据以往的项目经验再加上现在很流行的[Jetpack](https://developer.android.google.cn/jetpack)整理出了这个快速新建Android项目模板。

项目基本配置：
```groovy
compileSdkVersion 29
buildToolsVersion "29.0.3"

minSdkVersion 21
targetSdkVersion 29
```

在项目中[Jetpack](https://developer.android.google.cn/jetpack)使用的模块如下：
- [数据绑定库](https://developer.android.google.cn/topic/libraries/data-binding)
- [处理生命周期](https://developer.android.google.cn/topic/libraries/architecture/lifecycle)
- [LiveData](https://developer.android.google.cn/topic/libraries/architecture/livedata)
- [ViewModel](https://developer.android.google.cn/topic/libraries/architecture/viewmodel)

其他模块的可以根据自身需求进行添加。

项目使用的是Android推荐应用架构，架构图如下：

![架构图](img/architecture.png)

注意，项目并不涉及SQLite,如果需要请自行添加；网络请求库直接使用的[okhttp](https://github.com/square/okhttp)，并没有用[retrofit](https://github.com/square/retrofit)。
## 模块介绍
为啥要分成模块呢？

因为以前在外包公司呆过，看到过杂乱无章的项目，代码粘来粘去那是常事，因此也常碰到一些稀奇古怪的Bug。慢慢地就意识到项目如果按功能分模块，不仅维护、复用方便，工作效率也会提高，最关键的稀奇古怪的Bug少了。
### app
开发模块；不同的项目，UI风格不同，业务不同，所以大多数得重新编写代码，把开发工作都集中在这个模块里，其他模块复用。

UI相关的代码写在AppActivity/AppFragment的子类以及xml布局文件中里，业务相关的代码写在AppViewModel的子类里，数据相关的代码写在AppRepository的子类里。
### module_basic
基本复用模块，不包含任何业务逻辑代码。

类的简单介绍：
- BasicActivity，Activity的封装类；
- BasicFragment，Fragment的封装类；
- ActivityManager，Activity的管理类；
- ObservableManager，数据变化通知管理类；
- PermissionManager，动态权限管理类；
- CloseProcessHandler，避免应用崩溃后重启的类。
### module_util
工具复用模块，不包含任何业务逻辑代码。

类的简单介绍：
- Log.kt，打印日志相关方法的Kotlin文件；
- AppInfoUtil，获取应用信息的工具类；
- DateUtil，时间相关的工具类；
- DeviceUtil，关于手机设备方面的工具类；
- FileProviderUtil，FileProvider类相关的工具类
- GsonUtil，gson相关的工具类；
- JavaReflectUtil，反射工具类；
- MessageDigestUtil，消息摘要相关的工具类
- ProcessUtil，进程方面的工具类；
- ResourcesUtil，Resources类相关的工具类
### module_network
网络复用模块，不包含任何业务逻辑代码。

使用了kotlin协程和网络请求库[okhttp](https://github.com/square/okhttp)，功能如下：
- 支持get、head、post、delete、put、patch网络请求方法；
- 支持Content-Type为表单、json、multipart/form-data以及上传文件时自定义Content-Type；
- 支持显示上传、下载文件的进度。
### module_image
图像复用模块，不包含任何业务逻辑代码。

## 适配问题
### Android 8.0
#### Activity的透明主题崩溃问题该异常为：
```
Caused by: java.lang.IllegalStateException: Only fullscreen opaque activities can request orientation
```
大概意思就是说透明的主题设置了界面方向(android:screenOrientation)，我们可以去掉屏幕方向或者改为全屏。
```xml
<style name="Translucent_Fullscreen" parent="Theme.AppCompat.Light.NoActionBar">
        <item name="android:windowBackground">@android:color/transparent</item>
        <item name="android:windowIsTranslucent">true</item>
        <item name="android:windowFullscreen">true</item>
</style>
```