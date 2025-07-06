package com.github.floormanager.utils

import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.widget.ImageView
import androidx.lifecycle.MutableLiveData
import com.bumptech.glide.Glide
import com.bumptech.glide.load.resource.bitmap.CenterCrop
import com.bumptech.glide.load.resource.bitmap.RoundedCorners
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition

/**
 * 图片加载工具类
 * 基于Glide实现的图片加载功能，支持多种加载模式和圆角处理
 */
object ImageLoader {
    
    const val NOT_SET = -1
    
    /**
     * 加载普通图片
     */
    fun loadImage(
        imageView: ImageView,
        url: String?,
        placeHolder: Int = NOT_SET,
        error: Int = NOT_SET,
        loadStateCallback: ImageLoadStateCallback? = null
    ) {
        val context = imageView.context
        val requestBuilder = Glide.with(context)
            .load(url)
            
        if (placeHolder != NOT_SET) {
            requestBuilder.placeholder(placeHolder)
        }
        
        if (error != NOT_SET) {
            requestBuilder.error(error)
        }
        
        requestBuilder.into(imageView)
        loadStateCallback?.onSuccess()
    }
    
    /**
     * 加载圆角图片
     */
    fun loadRoundedImage(
        imageView: ImageView,
        url: String?,
        cornerRadius: Int,
        width: Int = 0,
        height: Int = 0,
        placeHolder: Int = NOT_SET,
        error: Int = NOT_SET,
        loadStateCallback: ImageLoadStateCallback? = null
    ) {
        val context = imageView.context
        val requestBuilder = Glide.with(context)
            .load(url)
            .transform(CenterCrop(), RoundedCorners(cornerRadius))
            
        if (placeHolder != NOT_SET) {
            requestBuilder.placeholder(placeHolder)
        }
        
        if (error != NOT_SET) {
            requestBuilder.error(error)
        }
        
        if (width > 0 && height > 0) {
            requestBuilder.override(width, height)
        }
        
        requestBuilder.into(imageView)
    }
    
    /**
     * 加载圆形图片
     */
    fun loadCircleImage(
        imageView: ImageView,
        url: String?,
        width: Int = 0,
        height: Int = 0,
        placeHolder: Int = NOT_SET,
        error: Int = NOT_SET,
        loadStateCallback: ImageLoadStateCallback? = null
    ) {
        val context = imageView.context
        val requestBuilder = Glide.with(context)
            .load(url)
            .circleCrop()
            
        if (placeHolder != NOT_SET) {
            requestBuilder.placeholder(placeHolder)
        }
        
        if (error != NOT_SET) {
            requestBuilder.error(error)
        }
        
        if (width > 0 && height > 0) {
            requestBuilder.override(width, height)
        }
        
        requestBuilder.into(imageView)
    }
    
    /**
     * 加载指定边角的圆角图片
     */
    fun loadGranularRoundedImage(
        imageView: ImageView,
        url: String?,
        topLeftRadius: Int,
        topRightRadius: Int,
        bottomLeftRadius: Int,
        bottomRightRadius: Int,
        width: Int = 0,
        height: Int = 0,
        placeHolder: Int = NOT_SET,
        error: Int = NOT_SET,
        loadStateCallback: ImageLoadStateCallback? = null
    ) {
        val context = imageView.context
        val requestBuilder = Glide.with(context)
            .load(url)
            .transform(CenterCrop(), GranularRoundedCorners(topLeftRadius, topRightRadius, bottomLeftRadius, bottomRightRadius))
            
        if (placeHolder != NOT_SET) {
            requestBuilder.placeholder(placeHolder)
        }
        
        if (error != NOT_SET) {
            requestBuilder.error(error)
        }
        
        if (width > 0 && height > 0) {
            requestBuilder.override(width, height)
        }
        
        requestBuilder.into(imageView)
        loadStateCallback?.onSuccess()
    }
    
    /**
     * 下载图片bitmap
     */
    fun downloadImageBitmap(
        context: Context,
        url: String,
        width: Int,
        height: Int
    ): MutableLiveData<Bitmap> {
        val liveData = MutableLiveData<Bitmap>()
        
        Glide.with(context)
            .asBitmap()
            .load(url)
            .override(width, height)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    liveData.value = resource
                }
                
                override fun onLoadCleared(placeholder: Drawable?) {
                    // 清理资源
                }
            })
            
        return liveData
    }
} 