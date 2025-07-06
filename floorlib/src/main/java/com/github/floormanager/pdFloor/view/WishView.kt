package com.github.floormanager.pdFloor.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.floormanager.pdFloor.utils.getAttrColor
import com.github.floormanager.floorlib.R

/**
 * 关注按钮视图
 * 用于展示和处理商品收藏/关注功能的自定义视图
 * 
 * @param context 上下文环境
 * @param attrs 属性集合
 */

class WishView : LinearLayout {

    //关注状态的TextView
    private lateinit var wishText: TextView

    //关注图标的ImageView
    private lateinit var wishImage: ImageView

    constructor(context: Context) : super(context) {
        init()
    }

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    private fun init() {
        LayoutInflater.from(context).inflate(R.layout.pd_base_floor_wish_view_layout, this, true)
        wishImage = findViewById(R.id.wishImage)
        wishText = findViewById(R.id.wishText)
    }

    fun setWishListSelected(isSelected: Boolean) {
        //记录当前关注状态，点击按钮时需要
        setSelected(isSelected)

        //渲染关注图标 - 使用简单的Android drawable资源
        val drawableRes = if (isSelected) {
            android.R.drawable.btn_star_big_on
        } else {
            android.R.drawable.btn_star_big_off
        }
        wishImage.setImageResource(drawableRes)
        
        //设置图标颜色
        wishImage.setColorFilter(
            context.getAttrColor(if (isSelected) R.attr.isv_color_C9 else R.attr.isv_color_C7)
        )

        //渲染关注文案
        wishText.text =
            resources.getString(if (isSelected) R.string.pd_base_floor_btn_like_selected else R.string.pd_base_floor_btn_like_normal)
    }
}