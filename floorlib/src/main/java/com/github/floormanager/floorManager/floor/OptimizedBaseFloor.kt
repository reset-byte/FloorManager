package com.github.floormanager.floorManager.floor

import android.content.Context
import android.util.Log
import android.view.View
import androidx.activity.viewModels
import androidx.annotation.IdRes
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.OnLifecycleEvent
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.lifecycle.FloorLifecycleObserver
import com.github.floormanager.floorManager.viewmodel.FloorEventViewModel

/**
 * 优化版楼层基类
 * 相比原版本的改进：
 * 1. 更安全的Context转换
 * 2. 更好的错误处理
 * 3. 规范化的生命周期管理
 * 4. 增强的调试信息
 */
abstract class OptimizedBaseFloor(
    protected val context: Context,
    val floorEntity: FloorEntity
) : LifecycleObserver, FloorLifecycleObserver {

    companion object {
        private const val TAG = "OptimizedBaseFloor"
    }

    // 楼层根视图 - 改为私有属性
    private var _rootView: View? = null
    
    // 公共访问器
    val rootView: View?
        get() = _rootView

    // 楼层状态
    protected var isInitialized = false
        private set
    
    protected var isDataRendered = false
        private set

    // 事件ViewModel，使用惰性初始化
    protected val eventViewModel: FloorEventViewModel? by lazy {
        try {
            (context as? AppCompatActivity)?.let {
                it.viewModels<FloorEventViewModel>().value
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to get FloorEventViewModel", e)
            null
        }
    }

    /**
     * 设置楼层根视图
     */
    fun setRootView(view: View) {
        _rootView = view
        if (!isInitialized) {
            try {
                initView()
                isInitialized = true
            } catch (e: Exception) {
                Log.e(TAG, "Failed to initialize floor: ${floorEntity.floorType}", e)
                onInitializationFailed(e)
            }
        }
    }

    /**
     * 楼层初始化 - 改为可访问
     * 子类实现具体的初始化逻辑
     */
    abstract fun initView()

    /**
     * 楼层数据渲染
     * 子类实现具体的数据渲染逻辑
     */
    protected abstract fun renderData(floorData: Any?)

    /**
     * 显示数据的公共方法
     */
    fun showData(floorData: Any?) {
        if (!isInitialized) {
            Log.w(TAG, "Floor not initialized: ${floorEntity.floorType}")
            return
        }

        try {
            renderData(floorData)
            isDataRendered = true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to render data for floor: ${floorEntity.floorType}", e)
            onRenderDataFailed(e)
        }
    }

    /**
     * 根据ID获取视图
     */
    protected fun <T : View> findViewById(@IdRes id: Int): T? {
        return try {
            _rootView?.findViewById(id)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to find view with id: $id", e)
            null
        }
    }

    /**
     * 安全地获取视图
     */
    protected fun <T : View> requireViewById(@IdRes id: Int): T {
        return findViewById<T>(id) ?: throw IllegalStateException("View with id $id not found")
    }

    /**
     * 楼层与窗口关联时的回调
     */
    open fun onAttachToWindow() {
        
    }

    /**
     * 楼层与窗口分离时的回调
     */
    open fun onDetachFromWindow() {
        
    }

    /**
     * 初始化失败时的回调
     */
    protected open fun onInitializationFailed(error: Throwable) {
        Log.e(TAG, "Floor initialization failed: ${floorEntity.floorType}", error)
    }

    /**
     * 数据渲染失败时的回调
     */
    protected open fun onRenderDataFailed(error: Throwable) {
        Log.e(TAG, "Floor data rendering failed: ${floorEntity.floorType}", error)
    }

    /**
     * 获取楼层状态信息
     */
    fun getFloorStatus(): String {
        return "Floor: ${floorEntity.floorType}, " +
               "Initialized: $isInitialized, " +
               "DataRendered: $isDataRendered, " +
               "Height: ${floorEntity.floorHeight}"
    }

    // 生命周期回调
    @OnLifecycleEvent(Lifecycle.Event.ON_PAUSE)
    override fun onPause() {
        Log.d(TAG, "Floor paused: ${floorEntity.floorType}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_RESUME)
    override fun onResume() {
        Log.d(TAG, "Floor resumed: ${floorEntity.floorType}")
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    override fun onDestroy() {
        Log.d(TAG, "Floor destroyed: ${floorEntity.floorType}")
        _rootView = null
        isInitialized = false
        isDataRendered = false
    }
} 