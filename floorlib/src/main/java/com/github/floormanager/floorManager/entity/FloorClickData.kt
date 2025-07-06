package com.github.floormanager.floorManager.entity

/**
 * 楼层点击事件数据类
 * 包含楼层点击事件的相关信息，用于事件传递和处理
 * 
 * @param floorType 楼层唯一标识
 * @param clickPosition 点击位置唯一标识（楼层内唯一，一般定义在楼层实现类的内部）
 * @param data 点击事件传递的业务数据
 */
class FloorClickData(
    val floorType: String,
    val clickPosition: String? = null,
    val data: Any? = null
) {

}