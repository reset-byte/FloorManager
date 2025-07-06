package com.github.floormanager.view

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.widget.LinearLayout

/**
 * 错误提示视图
 */
class ErrorTipView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : LinearLayout(context, attrs, defStyleAttr) {
    
    /**
     * 设置重试按钮的可见性
     */
    fun setRetryVisibility(visible: Boolean) {
        visibility = if (visible) View.VISIBLE else View.GONE
    }
} 