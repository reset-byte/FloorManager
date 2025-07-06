package com.github.floormanager.pdFloor.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.github.floormanager.widget.FlowLayout
import com.github.floormanager.widget.TagAdapter
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorServiceData

/**
 * 服务楼层适配器
 * 负责渲染服务楼层中的服务项目列表，实现简单的TextView赋值逻辑
 */
class FloorServiceAdapter : TagAdapter<FloorServiceData.ServiceItemData>() {
    override fun getView(
        parent: FlowLayout?,
        position: Int,
        item: FloorServiceData.ServiceItemData?
    ): View {
        val view: View = LayoutInflater.from(parent?.context)
            .inflate(R.layout.pd_base_floor_service_item, parent, false)
        (view as TextView).text = item?.content

        return view
    }
}