package com.github.floormanager.pdFloor.adapter

import android.graphics.Paint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.floormanager.utils.PriceFormatUtils
import com.github.floormanager.pdFloor.utils.getAttrDimension
import com.github.floormanager.pdFloor.entity.FloorRecommendData
import com.github.floormanager.utils.ImageLoader
import com.github.floormanager.utils.dp2px
import com.github.floormanager.floorlib.R
import com.github.floormanager.floorlib.databinding.PdBaseFloorRecommendItemLayoutBinding
import java.math.BigDecimal

/**
 * 推荐瀑布流适配器
 * 负责渲染推荐楼层中的商品推荐列表，实现瀑布流布局和商品卡片展示
 */

class FloorRecommendAdapter : RecyclerView.Adapter<FloorRecommendAdapter.RecommendViewHolder>() {

    //数据list
    private val productList = ArrayList<FloorRecommendData.RecommendItemData>()

    //卡片点击监听
    var itemClickListenr: View.OnClickListener? = null

    fun setData(data: ArrayList<FloorRecommendData.RecommendItemData>?) {
        data?.apply {
            productList.clear()
            productList.addAll(data)
            notifyDataSetChanged()
        }
    }

    inner class RecommendViewHolder(private val binding: PdBaseFloorRecommendItemLayoutBinding) : RecyclerView.ViewHolder(binding.root) {

        private val imgRadius = binding.root.context.getAttrDimension(R.attr.isv_corner_R1).toInt()

        fun bind(data: FloorRecommendData.RecommendItemData) {

            //渲染商品卡片标题
            binding.title.text = data.title

            //渲染商品卡片中商品图，因头部撑满卡片尺寸故需要设置圆角
            ImageLoader.loadGranularRoundedImage(
                binding.img,
                data.imgUrl,
                imgRadius,
                imgRadius,
                0,
                0,
                binding.img.layoutParams.height,
                binding.img.layoutParams.height
            )

            //渲染商品实际售价，如果没有则显示暂无报价
            if (data.salePrice != null && data.salePrice >= BigDecimal.ZERO) {
                binding.salePrice.text = PriceFormatUtils.formatToNumber(data.salePrice, true)
            } else {
                binding.salePrice.text =
                    binding.root.resources.getString(R.string.pd_base_floor_dialog_product_attr_price_empty)
            }

            //渲染商品划线价，如果没有或小等于商品实际售价则不显示
            if (data.salePrice != null && data.originalPrice != null && data.salePrice < data.originalPrice) {
                binding.originalPrice.text =
                    PriceFormatUtils.formatToNumber(data.originalPrice, true)
            } else {
                binding.originalPrice.text = null
            }

            //渲染商品无货提示
            binding.outOfStockTag.visibility = if (data.hasStock) View.VISIBLE else View.GONE

            //为商品卡片View设置tag，用于点击商品卡时跳转到指定的商详页
            binding.root.tag = data.skuId

            // 设置点击事件
            binding.root.setOnClickListener {
                // 通过调用外部的点击监听器来处理点击事件
                itemClickListenr?.onClick(it)
            }
        }
    }

    override fun onCreateViewHolder(p0: ViewGroup, p1: Int): RecommendViewHolder {
        val binding = PdBaseFloorRecommendItemLayoutBinding.inflate(
            LayoutInflater.from(p0.context),
            p0,
            false
        )

        //为划线价View设置划线属性
        binding.originalPrice.paint.flags =
            binding.originalPrice.paint.flags or Paint.STRIKE_THRU_TEXT_FLAG

        //根据设计规范要求设置卡片高度
        binding.img.layoutParams.height =
            (p0.resources.displayMetrics.widthPixels / 2) - (binding.root.context.getAttrDimension(R.attr.isv_space_W3)
                .toInt()) - 7f.dp2px

        return RecommendViewHolder(binding)
    }

    override fun onBindViewHolder(p0: RecommendViewHolder, p1: Int) {
        p0.bind(productList[p1])
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}