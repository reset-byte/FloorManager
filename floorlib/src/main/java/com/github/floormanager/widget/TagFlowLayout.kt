package com.github.floormanager.widget

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.BaseAdapter

/**
 * 标签流式布局
 */
class TagFlowLayout @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FlowLayout(context, attrs, defStyleAttr) {

    private var mAdapter: BaseAdapter? = null
    private var maxLines = -1

    /**
     * 设置适配器
     */
    var adapter: BaseAdapter?
        get() = mAdapter
        set(value) {
            if (mAdapter != null) {
                // 清除旧的观察者
            }
            mAdapter = value
            mAdapter?.let { 
                setData()
            }
        }

    /**
     * 设置最大行数
     */
    fun setMaxLines(maxLines: Int) {
        this.maxLines = maxLines
    }

    private fun setData() {
        removeAllViews()
        val adapter = mAdapter ?: return
        
        for (i in 0 until adapter.count) {
            val view = adapter.getView(i, null, this)
            addView(view)
        }
    }
} 