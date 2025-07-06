package com.github.floormanager.pdFloor.entity
/**
 * 售后楼层数据类
 * 包含售后服务信息和包装配送相关数据
 */
data class FloorAfterSaleData(
    val afterSaleContent: String?,
    val packageContent: String?
)