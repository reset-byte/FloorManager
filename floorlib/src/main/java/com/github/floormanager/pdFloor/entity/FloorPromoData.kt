package com.github.floormanager.pdFloor.entity

/**
 * 促销楼层数据类
 * 包含促销活动信息和优惠券相关数据
 */

class FloorPromoData(val couponList: List<String>?, val promoList: List<com.github.floormanager.pdFloor.entity.FloorPromoData.PromoItemData>?) {

    /**
     * 促销项数据类
     * 包含促销活动的标题和内容信息
     * 
     * @param title 促销角标
     * @param content 促销内容
     */

    class PromoItemData(val title: String?, val content: String?)

}