package com.github.floormanager.pdFloor.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.github.floormanager.widget.FlowLayout
import com.github.floormanager.widget.TagAdapter
import com.github.floormanager.floorlib.R

/**
 * 促销楼层中的优惠券适配器
 * 负责渲染促销楼层中的优惠券标签，实现简单的TextView赋值逻辑
 */

class FloorPromoCouponAdapter : TagAdapter<String>() {
    override fun getView(
        parent: FlowLayout?,
        position: Int,
        item: String?
    ): View {
        val view: View = LayoutInflater.from(parent?.context)
            .inflate(R.layout.pd_base_floor_promo_item_coupon, parent, false)
        (view as TextView).text = item
        return view
    }
}