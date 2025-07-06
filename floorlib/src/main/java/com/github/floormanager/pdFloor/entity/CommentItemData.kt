package com.github.floormanager.pdFloor.entity
/**
 * 单条评价数据类
 * 包含单条商品评价的详细信息
 */
data class CommentItemData(
    val userName: String?,
    val userIcon: String?,
    val commentPoint: Int?,
    val time: String?,
    val tagList: ArrayList<String?>?
)