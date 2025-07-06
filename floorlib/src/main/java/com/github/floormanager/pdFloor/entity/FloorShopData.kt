package com.github.floormanager.pdFloor.entity

/**
 * 店铺楼层数据类
 * 包含店铺基本信息和交互状态
 * 
 * @param shopName 店铺名
 * @param shopIconUrl 店铺图标url
 * @param showChat 是否展示客服按钮
 * @param isFollow 是否关注
 */

class FloorShopData(val shopName: String?, val shopIconUrl: String?, val showChat: Boolean,var isFollow:Boolean)