package com.github.floormanager.pdFloor.entity

/**
 * 评价楼层数据类
 * 包含商品评价信息和统计数据
 *
 * @param count 评价总数
 * @param applauseRate 好评率（百分数）
 * @param itemData 第一条评价数据
 *
 */

data class FloorCommentData(val count: Int?, val applauseRate: Int?, val itemData: CommentItemData?)