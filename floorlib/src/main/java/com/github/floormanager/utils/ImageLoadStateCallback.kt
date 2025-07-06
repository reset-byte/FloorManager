package com.github.floormanager.utils

import android.widget.ImageView

/**
 * 图片加载状态回调接口
 */
interface ImageLoadStateCallback {
    /**
     * 图片加载成功
     */
    fun onSuccess()
    
    /**
     * 图片加载失败
     */
    fun onError()
    
    /**
     * 图片加载开始
     */
    fun onStart()
}

/**
 * 简单的图片加载状态回调实现
 */
class SimpleImageLoadStateCallback(
    private val imageView: ImageView,
    private val backgroundColor: Int = -1
) : ImageLoadStateCallback {
    
    override fun onSuccess() {
        if (backgroundColor != -1) {
            imageView.setBackgroundColor(backgroundColor)
        }
    }
    
    override fun onError() {
        // 错误处理
    }
    
    override fun onStart() {
        // 开始加载
    }
} 