package com.github.floormanager.floorManager.manager

import android.text.TextUtils
import com.github.floormanager.floorManager.entity.FloorConfig
import com.github.floormanager.floorManager.entity.FloorCustomConfig
import com.github.floormanager.floorManager.entity.ModuleConfig
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor

/**
 * 楼层管理器
 * 负责单模块的配置存储与楼层注册管理
 */

class FloorManager {

    //楼层的index
    private var floorIndex: Int = 0

    //楼层配置存储
    private val floorClassMap: LinkedHashMap<String, FloorConfig> = LinkedHashMap(32)

    //楼层type存储
    private val floorTypeMap: LinkedHashMap<String, Int> = LinkedHashMap(32)

    //模块配置
    var moduleConfig: ModuleConfig? = null


    /**
     *
     * 注册楼层
     *
     * @param floorId 楼层唯一标识
     * @param floorClass 楼层实现类Class
     * @param floorCustomConfig 楼层视图配置
     *
     */

    @Synchronized
    fun registerFloor(
        floorId: String,
        floorClass: Class<out OptimizedBaseFloor?>,
        floorCustomConfig: FloorCustomConfig
    ) {
        if (!TextUtils.isEmpty(floorId)) {
            if (!floorClassMap.containsKey(floorId)) {
                floorClassMap[floorId] = FloorConfig(floorClass, floorCustomConfig)
                if (!floorTypeMap.containsKey(floorId)) {
                    val index: Int = this.generateFloorIndex()
                    floorTypeMap[floorId] = index
                }
            }
        }
    }

    /**
     *
     * 获取楼层index（暂时没用到，后续实现楼层复用逻辑时可能会用到，每个楼层有一个唯一的index值）
     *
     * @return 楼层唯一的int类型的index值
     *
     */

    @Synchronized
    private fun generateFloorIndex(): Int {
        return ++floorIndex
    }

    /**
     *
     * 获根据楼层id取楼层实现类Class
     *
     * @param mId 楼层id
     * @return 楼层实现类Class
     *
     */

    fun getClassById(mId: String): Class<out OptimizedBaseFloor?>? {
        if (floorClassMap.containsKey(mId)) {
            return floorClassMap[mId]!!.floorClass
        }
        return null
    }

    /**
     *
     * 根据楼层id获取楼层配置
     *
     * @param mId 楼层唯一标识
     * @return 楼层配置
     *
     *
     */

    fun getFloorConfigById(mId: String): FloorCustomConfig? {
        if (floorClassMap.containsKey(mId)) {
            return floorClassMap[mId]!!.floorCustomConfig
        }
        return null
    }


    /**
     *
     * 楼层是否已注册
     *
     * @param floorId 楼层唯一标识
     * @return 楼层是否已注册
     *
     */
    fun isFloorHasBeenRegistered(floorId: String): Boolean {
        return floorClassMap.containsKey(floorId)
    }

    /**
     *
     * 获取所有已注册的楼层类型列表
     *
     * @return 已注册的楼层类型列表
     *
     */
    fun getRegisteredFloorTypes(): List<String> {
        return floorClassMap.keys.toList()
    }

}