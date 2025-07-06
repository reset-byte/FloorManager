package com.github.floormanager.floorManager.lifecycle

/**
 * 楼层生命周期观察者接口
 * 规范化楼层的生命周期回调
 */
interface FloorLifecycleObserver {
    
    /**
     * 楼层创建时调用
     */
    fun onCreate() {}
    
    /**
     * 楼层初始化视图时调用
     */
    fun onInitView() {}
    
    /**
     * 楼层开始渲染数据时调用
     */
    fun onStartRenderData() {}
    
    /**
     * 楼层完成数据渲染时调用
     */
    fun onDataRendered() {}
    
    /**
     * 楼层附加到窗口时调用
     */
    fun onAttachedToWindow() {}
    
    /**
     * 楼层从窗口分离时调用
     */
    fun onDetachedFromWindow() {}
    
    /**
     * 楼层暂停时调用
     */
    fun onPause() {}
    
    /**
     * 楼层恢复时调用
     */
    fun onResume() {}
    
    /**
     * 楼层销毁时调用
     */
    fun onDestroy() {}
}

/**
 * 楼层生命周期管理器
 * 负责管理楼层的生命周期回调
 */
class FloorLifecycleManager {
    
    private val observers = mutableListOf<FloorLifecycleObserver>()
    
    /**
     * 添加生命周期观察者
     */
    fun addObserver(observer: FloorLifecycleObserver) {
        if (!observers.contains(observer)) {
            observers.add(observer)
        }
    }
    
    /**
     * 移除生命周期观察者
     */
    fun removeObserver(observer: FloorLifecycleObserver) {
        observers.remove(observer)
    }
    
    /**
     * 通知所有观察者创建事件
     */
    fun notifyOnCreate() {
        observers.forEach { it.onCreate() }
    }
    
    /**
     * 通知所有观察者初始化视图事件
     */
    fun notifyOnInitView() {
        observers.forEach { it.onInitView() }
    }
    
    /**
     * 通知所有观察者开始渲染数据事件
     */
    fun notifyOnStartRenderData() {
        observers.forEach { it.onStartRenderData() }
    }
    
    /**
     * 通知所有观察者数据渲染完成事件
     */
    fun notifyOnDataRendered() {
        observers.forEach { it.onDataRendered() }
    }
    
    /**
     * 通知所有观察者附加到窗口事件
     */
    fun notifyOnAttachedToWindow() {
        observers.forEach { it.onAttachedToWindow() }
    }
    
    /**
     * 通知所有观察者从窗口分离事件
     */
    fun notifyOnDetachedFromWindow() {
        observers.forEach { it.onDetachedFromWindow() }
    }
    
    /**
     * 通知所有观察者暂停事件
     */
    fun notifyOnPause() {
        observers.forEach { it.onPause() }
    }
    
    /**
     * 通知所有观察者恢复事件
     */
    fun notifyOnResume() {
        observers.forEach { it.onResume() }
    }
    
    /**
     * 通知所有观察者销毁事件
     */
    fun notifyOnDestroy() {
        observers.forEach { it.onDestroy() }
        observers.clear()
    }
} 