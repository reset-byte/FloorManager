package com.github.floormanager.floorManager.entity

import com.github.floormanager.floorManager.floor.OptimizedBaseFloor

/**
 * 楼层配置数据类
 * 包含楼层实现类和视图配置信息
 * 
 * @param floorClass 楼层实现类的class，用于adapter初始化楼层
 * @param floorCustomConfig 楼层的视图配置
 */
data class FloorConfig(

    val floorClass: Class<out OptimizedBaseFloor?>,
    val floorCustomConfig: FloorCustomConfig

)