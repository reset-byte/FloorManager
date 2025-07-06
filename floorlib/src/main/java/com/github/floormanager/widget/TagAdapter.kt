package com.github.floormanager.widget

import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter

/**
 * 标签适配器基类
 */
abstract class TagAdapter<T> : BaseAdapter() {
    
    protected var dataList: List<T> = emptyList()
    
    /**
     * 设置数据
     */
    fun setData(data: List<T>?) {
        dataList = data ?: emptyList()
        notifyDataSetChanged()
    }
    
    override fun getCount(): Int = dataList.size
    
    override fun getItem(position: Int): T = dataList[position]
    
    override fun getItemId(position: Int): Long = position.toLong()
    
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return getView(parent as? FlowLayout, position, getItem(position))
    }
    
    /**
     * 原有的getView方法，保持兼容性
     */
    abstract fun getView(parent: FlowLayout?, position: Int, item: T?): View
} 