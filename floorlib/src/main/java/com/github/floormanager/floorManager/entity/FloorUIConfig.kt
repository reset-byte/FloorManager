package com.github.floormanager.floorManager.entity

import android.graphics.Color

/**
 * 楼层UI配置类
 * 包含楼层渲染时的UI相关属性配置，如背景、间距、圆角等
 */

class FloorUIConfig {

    //暂未使用
    var useDefaultSetting: Boolean = true

    //是否需要adapter在渲染时为楼层添加背景，默认为true，个别楼层存在不需要单独设置背景的情况
    var needSetBackground: Boolean = true

    //背景图url，为使用网络下发的背景图而预留，目前暂无真实业务场景使用
    var backgroundImgUrl: String = ""

    //背景图资源id
    var backgroundImgId: Int = 0

    //背景颜色
    var backgroundColor: Int = Color.WHITE

    //楼层上间距
    var marginTop: Float = 0F

    //楼层下间距
    var marginBottom: Float = 0F

    //楼层左边距
    var marginLeft: Float = 0F

    //楼层右边距
    var marginRight: Float = 0F

    //楼层上圆角半径
    var cornerTopRadius: Float = 0F

    //楼层下圆角半径
    var cornerBottomRadius: Float = 0F

    //圆角类型（都有、只有上、只有下、都没有这四种）
    var cornerType: CornerType = CornerType.NONE

    enum class CornerType {
        TOP, BOTTOM, ALL, NONE
    }

}