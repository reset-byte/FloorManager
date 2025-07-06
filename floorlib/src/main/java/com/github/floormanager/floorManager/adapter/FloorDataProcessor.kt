package com.github.floormanager.floorManager.adapter

import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.entity.FloorUIConfig
import com.github.floormanager.floorManager.entity.ModuleConfig
import com.github.floormanager.floorManager.manager.FloorManagerProxy

/**
 * 楼层数据预处理器
 * 负责楼层数据的过滤、分组、排序和UI配置
 */
class FloorDataProcessor(private val moduleName: String) {

    /**
     * 处理楼层数据列表
     * @param originalData 原始楼层数据列表
     * @return 处理后的楼层数据列表
     */
    fun processFloorData(originalData: List<FloorEntity>): List<FloorEntity> {
        val floorManager = FloorManagerProxy.getInstances(moduleName)
        
        // 1. 过滤未注册的楼层
        val filteredData = filterRegisteredFloors(originalData, floorManager)
        
        // 2. 按组进行分组
        val groupedData = groupFloorsByIndex(filteredData, floorManager)
        
        // 3. 应用默认UI配置
        val configuredData = applyDefaultUIConfig(groupedData, floorManager.moduleConfig)
        
        // 4. 排序并合并
        return sortAndMergeFloors(configuredData)
    }

    private fun filterRegisteredFloors(
        data: List<FloorEntity>,
        floorManager: com.github.floormanager.floorManager.manager.FloorManager
    ): List<FloorEntity> {
        return data.filter { floorManager.isFloorHasBeenRegistered(it.floorType) }
    }

    private fun groupFloorsByIndex(
        data: List<FloorEntity>,
        floorManager: com.github.floormanager.floorManager.manager.FloorManager
    ): Map<Int, MutableList<FloorEntity>> {
        val groupMap = mutableMapOf<Int, MutableList<FloorEntity>>()
        
        data.forEach { floorEntity ->
            val groupIndex = floorManager.getFloorConfigById(floorEntity.floorType)
                ?.floorGroupIndex ?: 0
            
            groupMap.getOrPut(groupIndex) { mutableListOf() }.add(floorEntity)
        }
        
        return groupMap
    }

    private fun applyDefaultUIConfig(
        groupedData: Map<Int, MutableList<FloorEntity>>,
        moduleConfig: ModuleConfig?
    ): Map<Int, MutableList<FloorEntity>> {
        if (moduleConfig == null) return groupedData
        
        val minGroupIndex = groupedData.keys.minOrNull() ?: 0
        val maxGroupIndex = groupedData.keys.maxOrNull() ?: 0
        
        groupedData.forEach { (groupIndex, floorList) ->
            floorList.forEachIndexed { index, floorEntity ->
                if (floorEntity.floorUIConfig.useDefaultSetting) {
                    configureFloorUI(
                        floorEntity,
                        moduleConfig,
                        groupIndex,
                        index,
                        floorList.size,
                        minGroupIndex,
                        maxGroupIndex
                    )
                }
            }
        }
        
        return groupedData
    }

    private fun configureFloorUI(
        floorEntity: FloorEntity,
        moduleConfig: ModuleConfig,
        groupIndex: Int,
        indexInGroup: Int,
        groupSize: Int,
        minGroupIndex: Int,
        maxGroupIndex: Int
    ) {
        with(floorEntity.floorUIConfig) {
            backgroundColor = moduleConfig.floorBackgroundColor
            cornerTopRadius = moduleConfig.floorCornerTopRadius
            cornerBottomRadius = moduleConfig.floorCornerBottomRadius
            
            // 设置间距
            if (groupIndex > minGroupIndex && indexInGroup == 0) {
                marginTop = moduleConfig.floorGroupTopMargin
            }
            if (indexInGroup == groupSize - 1) {
                marginBottom = moduleConfig.floorGroupBottomMargin
            }
            
            marginLeft = moduleConfig.floorGroupHorizontalMargin
            marginRight = moduleConfig.floorGroupHorizontalMargin
            
            // 设置圆角类型
            cornerType = determineCornerType(groupIndex, indexInGroup, groupSize, minGroupIndex)
        }
    }

    private fun determineCornerType(
        groupIndex: Int,
        indexInGroup: Int,
        groupSize: Int,
        minGroupIndex: Int
    ): FloorUIConfig.CornerType {
        return when {
            groupIndex == minGroupIndex -> {
                if (indexInGroup < groupSize - 1) FloorUIConfig.CornerType.NONE
                else FloorUIConfig.CornerType.BOTTOM
            }
            groupSize == 1 -> FloorUIConfig.CornerType.ALL
            indexInGroup == 0 -> FloorUIConfig.CornerType.TOP
            indexInGroup == groupSize - 1 -> FloorUIConfig.CornerType.BOTTOM
            else -> FloorUIConfig.CornerType.NONE
        }
    }

    private fun sortAndMergeFloors(groupedData: Map<Int, MutableList<FloorEntity>>): List<FloorEntity> {
        val result = mutableListOf<FloorEntity>()
        val floorManager = FloorManagerProxy.getInstances(moduleName)
        
        // 对每个组内的楼层进行排序
        groupedData.forEach { (_, floorList) ->
            floorList.sortBy { floorEntity ->
                floorManager.getFloorConfigById(floorEntity.floorType)
                    ?.floorGroupSortIndex ?: 0
            }
        }
        
        // 按组索引顺序合并
        groupedData.keys.sorted().forEach { groupIndex ->
            groupedData[groupIndex]?.let { result.addAll(it) }
        }
        
        return result
    }
} 