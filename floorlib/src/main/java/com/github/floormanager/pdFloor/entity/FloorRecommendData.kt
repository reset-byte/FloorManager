package com.github.floormanager.pdFloor.entity

import java.math.BigDecimal

/**
 * 推荐楼层数据类
 * 包含推荐商品列表信息
 * 
 * @param productList 推荐商品列表
 */
class FloorRecommendData(val productList: ArrayList<RecommendItemData>?) {

    /**
     * 推荐商品项数据类
     * 包含单个推荐商品的基本信息
     * 
     * @param skuId 商品skuId
     * @param title 商品名称
     * @param salePrice 商品售价
     * @param originalPrice 商品原价
     * @param imgUrl 商品主图url
     * @param hasStock 是否有库存
     */
    class RecommendItemData(
        val skuId:String?,
        val title: String?,
        val salePrice: BigDecimal?,
        val originalPrice: BigDecimal?,
        val imgUrl: String?,
        val hasStock: Boolean
    )

}