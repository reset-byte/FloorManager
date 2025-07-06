package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import com.github.floormanager.widget.TagFlowLayout
import com.github.floormanager.floorManager.entity.FloorClickData
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.adapter.FloorPromoCouponAdapter

/**
 * 促销楼层实现类
 * 负责渲染商品促销信息，包括优惠券和促销活动内容
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */
class FloorPromo(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity),
    View.OnClickListener {

    //优惠券这一行的整体Layout
    private lateinit var couponLayout: LinearLayout
    //优惠券内容的Layout，采用标签来实现
    private lateinit var couponItemLayout: TagFlowLayout
    //促销部分的整体Layout
    private lateinit var promoLayout: LinearLayout

    //优惠券内容的Adapter
    private val couponAdapter: FloorPromoCouponAdapter = FloorPromoCouponAdapter()

    override fun initView() {
        couponLayout = requireViewById(R.id.couponsLayout)
        couponItemLayout = requireViewById(R.id.coupons)
        promoLayout = requireViewById(R.id.promos)
        //优惠券在促销楼层里最多只显示1行
        couponItemLayout.setMaxLines(1)
        couponItemLayout.adapter = couponAdapter
        rootView?.setOnClickListener(this)
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is com.github.floormanager.pdFloor.entity.FloorPromoData) {
            return
        }
        //优惠券渲染相关逻辑
        showCoupons(floorData.couponList)
        //促销渲染相关逻辑
        showPromos(floorData.promoList)
    }

    private fun showCoupons(coupons: List<String>?) {
        //如果没有优惠券则隐藏优惠券部分的布局
        if (coupons.isNullOrEmpty()) {
            couponLayout.visibility = View.GONE
            return
        }
        //具体渲染逻辑交给优惠券的Adapter
        couponAdapter.setData(coupons)
    }

    private fun showPromos(promos: List<com.github.floormanager.pdFloor.entity.FloorPromoData.PromoItemData>?) {
        //如果没有优惠券则隐藏促销部分的布局
        if (promos.isNullOrEmpty()) {
            promoLayout.visibility = View.GONE
            return
        }
        //添加前先对布局进行清空，防止刷新时出现问题
        promoLayout.removeAllViews()

        for (itemData in promos) {
            val view: View = LayoutInflater.from(context)
                .inflate(R.layout.pd_base_floor_promo_item_promo, promoLayout, false)
            val tagView = view.findViewById<TextView>(R.id.tag)
            val contentView = view.findViewById<TextView>(R.id.content)
            tagView.text = itemData.title
            contentView.text = itemData.content
            promoLayout.addView(view)
        }

    }

    override fun onDestroy() {
    }

    override fun onClick(v: View?) {
        //点击楼层将事件传递给商详组件触发跳转至促销页面
        eventViewModel?.clickEvent?.value = FloorClickData(floorEntity.floorType)
    }
}