package com.github.floormanager.ui

import android.annotation.SuppressLint
import android.content.Context
import android.util.AttributeSet
import android.widget.RatingBar
import androidx.core.content.ContextCompat

/**
 * 自定义评分控件
 */
class ScaleRatingBar @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = android.R.attr.ratingBarStyle
) : androidx.appcompat.widget.AppCompatRatingBar(context, attrs, defStyleAttr) {

    init {
        setupRatingBar()
    }

    private fun setupRatingBar() {
        // 设置评分控件的基本属性
        stepSize = 0.1f
        
        // 可以设置自定义的星星图标
        try {
            val starDrawable = ContextCompat.getDrawable(context, android.R.drawable.btn_star)
            progressDrawable = starDrawable
        } catch (e: Exception) {
            // 如果设置失败，使用默认的
        }
    }
    
    /**
     * 设置评分值
     */
    override fun setRating(rating: Float) {
        super.setRating(rating)
    }
    
    /**
     * 获取评分值
     */
    override fun getRating(): Float {
        return super.getRating()
    }
    
    /**
     * 设置星星数量（如果需要的话）
     */
    @SuppressLint("SoonBlockedPrivateApi")
    fun setStarCount(num: Int) {
        if (num > 0) {
            try {
                // 通过反射设置，因为numStars是只读的
                val field = RatingBar::class.java.getDeclaredField("mNumStars")
                field.isAccessible = true
                field.set(this, num)
                requestLayout()
            } catch (e: Exception) {
                // 如果反射失败，忽略
            }
        }
    }
    
    /**
     * 设置是否为指示器模式
     * 注意：isIndicator是只读属性，只能在构造函数中设置
     */
    fun setIndicatorMode(isIndicator: Boolean) {
        // RatingBar的isIndicator属性是只读的，不能在运行时修改
        // 如果需要修改，需要在构造函数中设置
        // this.isIndicator = isIndicator // 此行会导致编译错误
        
        // 可以通过反射来设置，但不推荐
        try {
            val field = RatingBar::class.java.getDeclaredField("mIsIndicator")
            field.isAccessible = true
            field.set(this, isIndicator)
        } catch (e: Exception) {
            // 如果反射失败，忽略
        }
    }
} 