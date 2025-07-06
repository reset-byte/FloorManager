package com.github.floormanager.pdFloor.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.floormanager.pdFloor.entity.FloorSpecificationData
import com.github.floormanager.floorlib.databinding.PdBaseFloorSpecificationItemContentLayoutBinding
import com.github.floormanager.floorlib.databinding.PdBaseFloorSpecificationItemTitleLayoutBinding

/**
 * 规格列表适配器
 * 负责渲染商品规格参数列表，支持展开收起功能
 */

class FloorSpecificationAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    //规格标题的itemType值定义
    private val itemTypeTitle = 1

    //规格内容的itemType值定义
    private val itemTypeContent = 2

    //当前列表是否为展开状态
    private var isExpansion = false

    //需要显示展开隐藏的最小内容数量
    private val minSize = 7

    private val itemList = ArrayList<FloorSpecificationData.SpecItemData>()

    fun setData(data: ArrayList<FloorSpecificationData.SpecItemData>?) {
        data?.apply {
            itemList.clear()
            itemList.addAll(data)
            notifyDataSetChanged()
        }
    }

    /**
     * 改变列表展开隐藏状态
     */
    fun changeExpansion() {
        isExpansion = !isExpansion
        notifyDataSetChanged()
    }

    /**
     * 获取需要显示展开隐藏的最小内容数量
     * 
     * @return 需要显示展开隐藏的最小内容数量
     */
    fun getMinSize(): Int {
        return minSize
    }

    /**
     * 获取当前是否为展开状态
     * 
     * @return 当前是否为展开状态
     */
    fun isExpansion(): Boolean {
        return isExpansion
    }

    class SpecTitleViewHolder(private val binding: PdBaseFloorSpecificationItemTitleLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: FloorSpecificationData.SpecItemData) {
            //渲染规格的标题
            binding.title.text = data.title
        }
    }

    class SpecContentViewHolder(private val binding: PdBaseFloorSpecificationItemContentLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bind(data: FloorSpecificationData.SpecItemData, isLastItem: Boolean) {
            //渲染规格内容的标题与内容
            binding.contentTitle.text = data.title
            binding.content.text = data.content
            //如果是最后一个内容项则显示底部分割线
            binding.borderBottom.visibility = if (isLastItem) View.INVISIBLE else View.VISIBLE
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecyclerView.ViewHolder {
        if (p1 == itemTypeTitle) {
            val binding = PdBaseFloorSpecificationItemTitleLayoutBinding.inflate(
                LayoutInflater.from(p0.context),
                p0,
                false
            )
            return SpecTitleViewHolder(binding)
        } else {
            val binding = PdBaseFloorSpecificationItemContentLayoutBinding.inflate(
                LayoutInflater.from(p0.context),
                p0,
                false
            )
            return SpecContentViewHolder(binding)
        }
    }

    override fun onBindViewHolder(p0: RecyclerView.ViewHolder, p1: Int) {
        val itemData = itemList[p1]
        if (p0 is SpecTitleViewHolder) {
            p0.bind(itemData)
        } else if (p0 is SpecContentViewHolder) {
            p0.bind(itemData, p1 == itemCount - 1)
        }
    }

    override fun getItemCount(): Int {
        //如果展开则渲染全部item，隐藏则只渲染最小数量的item
        return if (isExpansion) itemList.size else if (itemList.size > minSize) minSize else itemList.size
    }

    override fun getItemViewType(position: Int): Int {
        val itemData = itemList[position]
        //根据是否存在内容决定渲染成标题还是内容
        return if (itemData.content != null) itemTypeContent else itemTypeTitle
    }
}