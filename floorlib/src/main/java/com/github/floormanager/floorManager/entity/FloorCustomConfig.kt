package com.github.floormanager.floorManager.entity

import androidx.annotation.LayoutRes

/**
 * 楼层视图配置类
 * 包含楼层布局ID、分组信息和排序配置
 * 
 * @param floorLayoutId 楼层渲染时使用的layout文件的id
 * @param floorGroupIndex 楼层的组id，用于adapter渲染时处理分组的背景间距圆角等
 * @param floorGroupSortIndex 楼层组内的升序排序值，默认为0，如果组内楼层的排序值均为默认值则渲染顺序为放入楼层list中的先后顺序
 */
class FloorCustomConfig(

    @LayoutRes val floorLayoutId: Int,
    val floorGroupIndex: Int,
    val floorGroupSortIndex: Int = 0

)