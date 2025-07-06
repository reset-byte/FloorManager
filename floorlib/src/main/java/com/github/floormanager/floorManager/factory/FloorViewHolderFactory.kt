package com.github.floormanager.floorManager.factory

import android.content.Context
import android.view.LayoutInflater
import android.widget.FrameLayout
import androidx.lifecycle.LifecycleOwner
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.manager.FloorCreator
import com.github.floormanager.floorManager.manager.FloorManagerProxy
import com.github.floormanager.floorManager.viewholder.FloorViewHolder

/**
 * FloorViewHolder工厂类
 * 负责统一管理FloorViewHolder的创建逻辑
 * 
 * @param context 上下文
 * @param moduleName 模块名称
 */
class FloorViewHolderFactory(
    private val context: Context,
    private val moduleName: String
) {
    private val floorCreator = FloorCreator()
    
    /**
     * 创建FloorViewHolder
     * 
     * @param floorEntity 楼层实体
     * @return FloorViewHolder实例
     */
    fun createFloorViewHolder(floorEntity: FloorEntity): FloorViewHolder {
        val floorManager = FloorManagerProxy.getInstances(moduleName)
        
        // 获取楼层配置
        val floorConfig = floorManager.getFloorConfigById(floorEntity.floorType)
            ?: throw IllegalStateException("楼层未注册: ${floorEntity.floorType}")
        
        // 获取楼层类
        val floorClass = floorManager.getClassById(floorEntity.floorType)
            ?: throw IllegalStateException("楼层类不存在: ${floorEntity.floorType}")
        
        // 创建楼层实例
        val floor = floorCreator.createFloor(context, floorEntity, floorClass)
            ?: throw IllegalStateException("楼层创建失败: ${floorEntity.floorType}")
        
        // 将楼层添加到生命周期观察者
        (context as? LifecycleOwner)?.lifecycle?.addObserver(floor)
        
        // 创建根视图
        val rootView = createFloorRootView(floorConfig.floorLayoutId)
        
        // 返回FloorViewHolder实例
        return FloorViewHolder(rootView, floor)
    }
    
    /**
     * 创建楼层根视图
     * 
     * @param layoutId 布局ID
     * @return 根视图
     */
    private fun createFloorRootView(layoutId: Int): android.view.View {
        val rootView = FrameLayout(context)
        LayoutInflater.from(context).inflate(layoutId, rootView, true)
        return rootView
    }
    
    /**
     * 批量创建FloorViewHolder
     * 
     * @param floorEntities 楼层实体列表
     * @return FloorViewHolder列表
     */
    fun createFloorViewHolders(floorEntities: List<FloorEntity>): List<FloorViewHolder> {
        return floorEntities.map { createFloorViewHolder(it) }
    }
    
    /**
     * 检查楼层类型是否已注册
     * 
     * @param floorType 楼层类型
     * @return 是否已注册
     */
    fun isFloorTypeRegistered(floorType: String): Boolean {
        return FloorManagerProxy.getInstances(moduleName).isFloorHasBeenRegistered(floorType)
    }
    
    /**
     * 获取已注册的楼层类型列表
     * 
     * @return 已注册的楼层类型列表
     */
    fun getRegisteredFloorTypes(): List<String> {
        return FloorManagerProxy.getInstances(moduleName).getRegisteredFloorTypes()
    }
} 