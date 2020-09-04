package com.ly.android.ui

import com.ly.module.basic.BasicFragment

/**
 * Created by Lan Yang on 2020/9/4
 *
 * 为了不让业务逻辑代码写入[BasicFragment]中，所以又新建了一个抽象类；
 * 这样做的目的就是让[BasicFragment]可以重复使用，因为不同的项目业务不同的。
 */
abstract class AppFragment:BasicFragment()