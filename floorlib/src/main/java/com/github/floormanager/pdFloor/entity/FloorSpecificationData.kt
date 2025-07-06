package com.github.floormanager.pdFloor.entity

/**
 * 规格楼层数据类
 * 包含商品规格参数列表信息
 * 
 * @param specList 商品规格列表
 */
class FloorSpecificationData(val specList: ArrayList<SpecItemData>?) {

    /**
     * 规格项数据类
     * 包含单个规格项的标题和内容信息
     * 
     * @param title 规格标题
     * @param content 规格内容
     */
    class SpecItemData(
        val title: String?,
        val content: String?
    )

}