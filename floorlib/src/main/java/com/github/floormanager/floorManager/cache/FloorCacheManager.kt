package com.github.floormanager.floorManager.cache

import android.content.Context
import androidx.lifecycle.LifecycleOwner
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.factory.FloorViewHolderFactory
import com.github.floormanager.floorManager.viewholder.FloorViewHolder

/**
 * 楼层缓存管理器
 * 负责 ViewHolder 的缓存、复用和生命周期管理
 */
class FloorCacheManager(
    private val context: Context,
    private val moduleName: String
) {
    
    private val viewHolderFactory = FloorViewHolderFactory(context, moduleName)
    private val floorCacheMap = mutableMapOf<String, FloorViewHolder>()
    private val positionToTypeMap = mutableMapOf<Int, String>()
    
    /**
     * 获取或创建楼层 ViewHolder
     * @param position 位置
     * @param floorEntity 楼层实体
     * @return 楼层 ViewHolder
     */
    fun getOrCreateFloorHolder(position: Int, floorEntity: FloorEntity): FloorViewHolder {
        val floorType = floorEntity.floorType
        positionToTypeMap[position] = floorType
        
        // 检查缓存中是否有可复用的 ViewHolder
        val cachedHolder = floorCacheMap[floorType]
        if (cachedHolder != null && canReuse(cachedHolder, floorEntity)) {
            return cachedHolder
        }
        
        // 使用工厂创建新的 ViewHolder
        val newHolder = viewHolderFactory.createFloorViewHolder(floorEntity)
        floorCacheMap[floorType] = newHolder
        
        return newHolder
    }
    
    /**
     * 检查 ViewHolder 是否可以复用
     */
    private fun canReuse(holder: FloorViewHolder, floorEntity: FloorEntity): Boolean {
        return holder.floor.floorEntity.floorType == floorEntity.floorType
    }
    
    /**
     * 根据位置获取缓存的 ViewHolder
     */
    fun getCachedHolder(position: Int): FloorViewHolder? {
        val floorType = positionToTypeMap[position] ?: return null
        return floorCacheMap[floorType]
    }
    
    /**
     * 清除指定楼层类型的缓存
     */
    fun clearCache(floorType: String) {
        floorCacheMap.remove(floorType)
        positionToTypeMap.entries.removeAll { it.value == floorType }
    }
    
    /**
     * 清除所有缓存
     */
    fun clearAllCache() {
        floorCacheMap.values.forEach { holder ->
            (context as? LifecycleOwner)?.lifecycle?.removeObserver(holder.floor)
        }
        floorCacheMap.clear()
        positionToTypeMap.clear()
    }
    
    /**
     * 获取缓存大小
     */
    fun getCacheSize(): Int = floorCacheMap.size
    
    /**
     * 检查楼层类型是否已注册
     */
    fun isFloorTypeRegistered(floorType: String): Boolean {
        return viewHolderFactory.isFloorTypeRegistered(floorType)
    }
    
    /**
     * 获取已注册的楼层类型列表
     */
    fun getRegisteredFloorTypes(): List<String> {
        return viewHolderFactory.getRegisteredFloorTypes()
    }
} 