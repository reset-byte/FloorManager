package com.github.floormanager.floorManager.entity

/**
 * 楼层实体类
 * 用于传入adapter的楼层渲染数据，包含楼层类型、UI配置和业务数据
 * 
 * @param floorType 楼层的唯一标识
 *
 *
 */

class FloorEntity(val floorType: String) {

    //为埋点预留的Entity，因为floorType并不一定能直接用来做上报，暂未使用
    var floorAttrEntity: FloorAttrEntity? = null

    //楼层渲染时的UI相关配置
    var floorUIConfig: FloorUIConfig = FloorUIConfig()

    //楼层的高度
    var floorHeight: Int = 0

    //楼层的业务数据
    var floorData: Any? = null

}