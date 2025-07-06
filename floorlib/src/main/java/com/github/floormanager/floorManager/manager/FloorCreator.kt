package com.github.floormanager.floorManager.manager

import android.content.Context
import android.util.Log
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import java.lang.reflect.Constructor
import java.lang.reflect.InvocationTargetException

/**
 * 楼层创建器
 * 负责通过反射的方式创建楼层实现类实例，支持灵活的楼层实例化和错误处理
 *
 * 负责通过反射的方式对楼层实现类进行创建，这样可以使外部只需传入楼层的Class和Data就能完成创建，方便替换，具体调用的地方在OptimizedFloorAdapter
 *
 *
 */

class FloorCreator {

    companion object {
        private const val TAG = "FloorCreator"
    }

    /**
     * 创建楼层实例
     * 
     * @param context 楼层上下文，一般直接传入业务模块的Activity
     * @param floorEntity 楼层数据
     * @param floorClass 楼层实现类的Class
     * @return 楼层实例或null
     */
    fun createFloor(
        context: Context?,
        floorEntity: FloorEntity?,
        floorClass: Class<*>?
    ): OptimizedBaseFloor? {
        if (context == null) {
            Log.e(TAG, "Context is null")
            return null
        }
        
        if (floorEntity == null) {
            Log.e(TAG, "FloorEntity is null")
            return null
        }
        
        if (floorClass == null) {
            Log.e(TAG, "FloorClass is null")
            return null
        }
        
        return try {
            createFloorInternal(context, floorEntity, floorClass)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to create floor: ${floorClass.simpleName}", e)
            null
        }
    }

    /**
     * 内部创建楼层实例的方法
     */
    private fun createFloorInternal(
        context: Context,
        floorEntity: FloorEntity,
        floorClass: Class<*>
    ): OptimizedBaseFloor? {
        // 验证类是否继承自OptimizedBaseFloor
        if (!OptimizedBaseFloor::class.java.isAssignableFrom(floorClass)) {
            Log.e(TAG, "Class ${floorClass.simpleName} does not extend OptimizedBaseFloor")
            return null
        }
        
        // 获取构造函数
        val constructor = findSuitableConstructor(floorClass)
        if (constructor == null) {
            Log.e(TAG, "No suitable constructor found for ${floorClass.simpleName}")
            return null
        }
        
        // 创建实例
        return try {
            constructor.isAccessible = true
            val instance = constructor.newInstance(context, floorEntity)
            
            // 类型检查
            if (instance is OptimizedBaseFloor) {
                instance
            } else {
                Log.e(TAG, "Created instance is not of type OptimizedBaseFloor")
                null
            }
        } catch (e: InstantiationException) {
            Log.e(TAG, "Failed to instantiate ${floorClass.simpleName}", e)
            null
        } catch (e: IllegalAccessException) {
            Log.e(TAG, "Constructor is not accessible for ${floorClass.simpleName}", e)
            null
        } catch (e: InvocationTargetException) {
            Log.e(TAG, "Constructor threw exception for ${floorClass.simpleName}", e.cause ?: e)
            null
        }
    }

    /**
     * 查找合适的构造函数
     * 优先查找 (Context, FloorEntity) 参数的构造函数
     */
    private fun findSuitableConstructor(floorClass: Class<*>): Constructor<*>? {
        val constructors = floorClass.constructors
        
        // 优先查找标准构造函数
        for (constructor in constructors) {
            val parameterTypes = constructor.parameterTypes
            if (parameterTypes.size == 2 &&
                Context::class.java.isAssignableFrom(parameterTypes[0]) &&
                FloorEntity::class.java.isAssignableFrom(parameterTypes[1])
            ) {
                return constructor
            }
        }
        
        // 如果没有找到标准构造函数，返回第一个构造函数
        return constructors.firstOrNull()
    }

    /**
     * 检查楼层类是否有效
     * @param floorClass 楼层类
     * @return 是否有效
     */
    fun isValidFloorClass(floorClass: Class<*>): Boolean {
        return OptimizedBaseFloor::class.java.isAssignableFrom(floorClass) && 
               findSuitableConstructor(floorClass) != null
    }
}