package com.github.floormanager.floorManager.entity

import android.graphics.Color

/**
 * 模块配置类
 * 在注册模块时传入的配置参数，用于设置楼层通用的背景色、间距、圆角等属性
 * 
 * @param floorBackgroundColor 楼层通用背景色
 * @param floorGroupTopMargin 楼层组通用上间距
 * @param floorGroupBottomMargin 楼层组通用下间距
 * @param floorGroupHorizontalMargin 楼层组通用左右边距
 * @param floorCornerTopRadius 楼层组通用上圆角半径
 * @param floorCornerBottomRadius 楼层组通用下圆角半径
 *
 * 以上属性的用处是在adapter进行楼层渲染时，对未特殊指定属性的楼层按注册时传入的分组进行通用背景色、间距、圆角的设置
 *
 */

class ModuleConfig(

    val floorBackgroundColor: Int = Color.WHITE,

    var floorGroupTopMargin: Float = 0F,

    var floorGroupBottomMargin: Float = 0F,

    var floorGroupHorizontalMargin: Float = 0F,

    var floorCornerTopRadius: Float = 0F,

    var floorCornerBottomRadius: Float = 0F


)