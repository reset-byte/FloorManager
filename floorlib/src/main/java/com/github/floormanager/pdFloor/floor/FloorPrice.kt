package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.graphics.Paint
import android.view.View
import android.widget.TextView
import com.github.floormanager.utils.IsvPriceFormatUtils
import com.github.floormanager.utils.PriceFormatUtils
import com.github.floormanager.pdFloor.utils.getAttrColor
import com.github.floormanager.floorManager.entity.FloorClickData
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorPriceData
import com.github.floormanager.pdFloor.view.WishView
import java.math.BigDecimal

/**
 * 价格楼层实现类
 * 负责渲染商品价格信息，包括实际售价、划线价、砍价提示和关注按钮
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */
class FloorPrice(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity),
    View.OnClickListener {

    //暂无报价的TextView
    private lateinit var noPriceText: TextView
    //实际售价的TextView
    private lateinit var salePriceText: TextView
    //划线价的TextView
    private lateinit var priceText: TextView
    //砍价提示的TextView
    private lateinit var slashText: TextView
    //关注按钮的View
    private lateinit var wishView: WishView

    override fun initView() {
        slashText = requireViewById(R.id.slashHint)
        noPriceText = requireViewById(R.id.noPrice)
        salePriceText = requireViewById(R.id.salePrice)
        priceText = requireViewById(R.id.price)
        wishView = requireViewById(R.id.wish)

        //为划线价设置划线属性
        priceText.paint?.flags = priceText.paint?.flags?.or(Paint.STRIKE_THRU_TEXT_FLAG)!!

        wishView.setOnClickListener(this)
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorPriceData) {
            return
        }

        //针对砍价商品设置特殊的价格颜色
        if (floorData.isSlash == true) {
            slashText.visibility = View.VISIBLE
            salePriceText.setTextColor(context.getAttrColor(R.attr.isv_color_C30))
            noPriceText.setTextColor(context.getAttrColor(R.attr.isv_color_C30))
        } else {
            slashText.visibility = View.GONE
            salePriceText.setTextColor(context.getAttrColor(R.attr.isv_color_C9))
            noPriceText.setTextColor(context.getAttrColor(R.attr.isv_color_C9))
        }

        //设置关注状态
        if (floorData.isLike != null) {
            wishView.visibility = View.VISIBLE
            wishView.setWishListSelected(floorData.isLike!!)
        } else {
            wishView.visibility = View.GONE
        }

        //暂无报价
        if (floorData.salePrice == null || floorData.salePrice < BigDecimal.ZERO) {
            noPriceText.visibility = View.VISIBLE
            salePriceText.visibility = View.GONE
            priceText.text = ""
            return
        }

        //正常设置实际售价，使用IsvPriceFormatUtils.setTextWithPriceStyle1来处理价格单位与数字的字体大小区分
        noPriceText.visibility = View.INVISIBLE
        salePriceText.visibility = View.VISIBLE
        IsvPriceFormatUtils.setTextWithPriceStyle1(salePriceText, floorData.salePrice, true)

        //如果划线价不为空且比实际售价高才显示
        if (floorData.price != null && floorData.salePrice < floorData.price) {
            priceText.text = PriceFormatUtils.formatToNumber(floorData.price, true)
        } else {
            priceText.text = null
        }
    }

    override fun onDestroy() {

    }

    override fun onClick(v: View?) {
        //点击关注View将事件传递给商详组件触发请求改变关注状态的阿波罗接口
        eventViewModel?.clickEvent?.value =
            FloorClickData(floorEntity.floorType, data = !wishView.isSelected)
    }
}