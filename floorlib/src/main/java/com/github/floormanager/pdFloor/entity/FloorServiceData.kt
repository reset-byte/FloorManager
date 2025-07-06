package com.github.floormanager.pdFloor.entity

/**
 * 服务楼层数据类
 * 包含商品相关服务列表信息
 * 
 * @param serviceList 服务列表
 */

class FloorServiceData(
    val serviceList: ArrayList<ServiceItemData>
) {

    /**
     * 服务项数据类
     * 包含单个服务项的内容信息
     * 
     * @param content 服务内容
     */

    class ServiceItemData(val content: String?)

}