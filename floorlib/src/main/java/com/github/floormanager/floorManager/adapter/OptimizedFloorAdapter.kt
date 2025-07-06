package com.github.floormanager.floorManager.adapter

import android.content.Context
import android.util.Log
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.floormanager.floorManager.adapter.FloorDataProcessor
import com.github.floormanager.floorManager.cache.FloorCacheManager
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.lifecycle.FloorLifecycleManager
import com.github.floormanager.floorManager.lifecycle.FloorLifecycleObserver
import com.github.floormanager.floorManager.viewholder.FloorViewHolder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

/**
 * 优化版楼层渲染Adapter
 * 解决了原版本的性能问题和代码结构问题，提供更好的缓存管理和生命周期管理
 * 
 * @param context 上下文环境
 * @param moduleName 模块名称
 * @param coroutineScope 协程作用域，用于支持异步数据预处理
 */
class OptimizedFloorAdapter(
    private val context: Context,
    private val moduleName: String,
    private val coroutineScope: CoroutineScope? = null
) : RecyclerView.Adapter<RecyclerView.ViewHolder>(), FloorLifecycleObserver {

    companion object {
        private const val TAG = "OptimizedFloorAdapter"
    }

    // 数据处理器
    private val dataProcessor = FloorDataProcessor(moduleName)
    
    // 缓存管理器
    private val cacheManager = FloorCacheManager(context, moduleName)
    
    // 生命周期管理器
    private val lifecycleManager = FloorLifecycleManager()
    
    // 原始数据
    private var originalData: List<FloorEntity> = emptyList()
    
    // 处理后的数据
    private var processedData: List<FloorEntity> = emptyList()
    
    // 数据更新监听器
    var onDataUpdateListener: ((List<FloorEntity>) -> Unit)? = null
    
    // 错误处理监听器
    var onErrorListener: ((String, Throwable?) -> Unit)? = null

    init {
        lifecycleManager.addObserver(this)
    }

    /**
     * 设置楼层数据列表
     * 支持异步处理以提高性能
     */
    fun setFloorData(data: List<FloorEntity>) {
        originalData = data
        
        if (coroutineScope != null) {
            // 异步处理数据
            coroutineScope.launch {
                try {
                    val processed = withContext(Dispatchers.Default) {
                        dataProcessor.processFloorData(data)
                    }
                    
                    withContext(Dispatchers.Main) {
                        updateProcessedData(processed)
                    }
                } catch (e: Exception) {
                    Log.e(TAG, "Error processing floor data", e)
                    onErrorListener?.invoke("数据处理失败", e)
                }
            }
        } else {
            // 同步处理数据
            try {
                val processed = dataProcessor.processFloorData(data)
                updateProcessedData(processed)
            } catch (e: Exception) {
                Log.e(TAG, "Error processing floor data", e)
                onErrorListener?.invoke("数据处理失败", e)
            }
        }
    }

    /**
     * 更新处理后的数据
     */
    private fun updateProcessedData(data: List<FloorEntity>) {
        val oldSize = processedData.size
        processedData = data
        
        // 通知数据变化
        if (oldSize == 0 && data.isNotEmpty()) {
            notifyItemRangeInserted(0, data.size)
        } else if (oldSize > 0 && data.isEmpty()) {
            notifyItemRangeRemoved(0, oldSize)
        } else {
            notifyDataSetChanged()
        }
        
        onDataUpdateListener?.invoke(data)
        Log.d(TAG, "Floor data updated: ${data.size} items")
    }

    /**
     * 刷新指定楼层数据
     */
    fun refreshFloorData(floorType: String, floorData: Any) {
        val index = processedData.indexOfFirst { it.floorType == floorType }
        if (index >= 0) {
            processedData[index].floorData = floorData
            
            // 刷新对应的ViewHolder
            val holder = cacheManager.getCachedHolder(index)
            holder?.let { viewHolder ->
                try {
                    lifecycleManager.notifyOnStartRenderData()
                    viewHolder.showData(floorData)
                    lifecycleManager.notifyOnDataRendered()
                } catch (e: Exception) {
                    Log.e(TAG, "Error refreshing floor data", e)
                    onErrorListener?.invoke("楼层刷新失败", e)
                }
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        if (viewType >= processedData.size) {
            throw IllegalArgumentException("Invalid viewType: $viewType")
        }
        
        val floorEntity = processedData[viewType]
        return try {
            lifecycleManager.notifyOnCreate()
            val holder = cacheManager.getOrCreateFloorHolder(viewType, floorEntity)
            lifecycleManager.notifyOnInitView()
            holder
        } catch (e: Exception) {
            Log.e(TAG, "Error creating ViewHolder", e)
            onErrorListener?.invoke("楼层创建失败", e)
            throw e
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (position >= processedData.size) {
            Log.w(TAG, "Invalid position: $position")
            return
        }
        
        val floorEntity = processedData[position]
        val floorViewHolder = holder as FloorViewHolder
        
        try {
            lifecycleManager.notifyOnStartRenderData()
            floorViewHolder.showData(floorEntity.floorData)
            lifecycleManager.notifyOnDataRendered()
        } catch (e: Exception) {
            Log.e(TAG, "Error binding ViewHolder", e)
            onErrorListener?.invoke("楼层绑定失败", e)
        }
    }

    override fun getItemCount(): Int = processedData.size

    override fun getItemViewType(position: Int): Int = position

    override fun onViewAttachedToWindow(holder: RecyclerView.ViewHolder) {
        super.onViewAttachedToWindow(holder)
        try {
            (holder as FloorViewHolder).floor.onAttachToWindow()
            lifecycleManager.notifyOnAttachedToWindow()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onViewAttachedToWindow", e)
        }
    }

    override fun onViewDetachedFromWindow(holder: RecyclerView.ViewHolder) {
        super.onViewDetachedFromWindow(holder)
        try {
            (holder as FloorViewHolder).floor.onDetachFromWindow()
            lifecycleManager.notifyOnDetachedFromWindow()
        } catch (e: Exception) {
            Log.e(TAG, "Error in onViewDetachedFromWindow", e)
        }
    }

    /**
     * 清理资源
     */
    fun cleanup() {
        lifecycleManager.notifyOnDestroy()
        cacheManager.clearAllCache()
        processedData = emptyList()
        originalData = emptyList()
    }

    /**
     * 获取缓存信息
     */
    fun getCacheInfo(): String {
        return "Cache size: ${cacheManager.getCacheSize()}, Data size: ${processedData.size}"
    }

    // FloorLifecycleObserver 的实现
    override fun onCreate() {
        Log.d(TAG, "Floor adapter created")
    }

    override fun onDestroy() {
        Log.d(TAG, "Floor adapter destroyed")
        cleanup()
    }

    override fun onPause() {
        Log.d(TAG, "Floor adapter paused")
        lifecycleManager.notifyOnPause()
    }

    override fun onResume() {
        Log.d(TAG, "Floor adapter resumed")
        lifecycleManager.notifyOnResume()
    }
} 