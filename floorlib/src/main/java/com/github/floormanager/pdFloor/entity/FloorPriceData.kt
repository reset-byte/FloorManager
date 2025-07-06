package com.github.floormanager.pdFloor.entity

import java.math.BigDecimal

/**
 * 价格楼层数据类
 * 包含商品价格信息和收藏状态
 * 
 * @param salePrice 商品售价
 * @param price 商品原价
 * @param isSlash 是否需要显示砍后价标志
 * @param isLike 商品是否收藏
 */

class FloorPriceData(
    val salePrice: BigDecimal?,
    val price: BigDecimal?,
    val isSlash: Boolean?,
    var isLike: Boolean?
)