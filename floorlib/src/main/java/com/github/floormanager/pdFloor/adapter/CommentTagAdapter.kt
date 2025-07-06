package com.github.floormanager.pdFloor.adapter

import android.view.LayoutInflater
import android.view.View
import android.widget.TextView
import com.github.floormanager.widget.FlowLayout
import com.github.floormanager.widget.TagAdapter
import com.github.floormanager.floorlib.R

/**
 * 评价标签适配器
 * 负责渲染评价楼层中的标签列表，实现简单的TextView赋值逻辑
 */

class CommentTagAdapter : TagAdapter<String>() {
    override fun getView(
        parent: FlowLayout?,
        position: Int,
        item: String?
    ): View {
        val view: View = LayoutInflater.from(parent?.context)
            .inflate(R.layout.pd_base_floor_comment_item_tag_layout, parent, false)
        (view as TextView).text = item
        return view
    }
}