# AndroidProjectTemplate
快速搭建Android项目
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