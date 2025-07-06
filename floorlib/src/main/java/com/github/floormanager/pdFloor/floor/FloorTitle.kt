package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.text.SpannableString
import android.text.Spanned
import android.text.style.LeadingMarginSpan
import android.view.View
import android.widget.TextView
import com.github.floormanager.pdFloor.utils.getAttrDimensionPixelSize
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorTitleData
import com.github.floormanager.utils.dp2px

/**
 * 商品标题楼层实现类
 * 负责渲染商品标题和标签信息，支持标签与标题的复合排版
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */

class FloorTitle(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity) {

    //商品标题的TextView
    private lateinit var title: TextView

    //商品标签的TextView
    private lateinit var tag: TextView

    override fun initView() {
        title = requireViewById(R.id.title)
        tag = requireViewById(R.id.tag)
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorTitleData) {
            return
        }

        //渲染商品标签
        tag.text = floorData.tag

        if (!floorData.tag.isNullOrEmpty()) {
            //如果标签显示，需要给商品标题的第一行预留出足够显示标签的间距，这里采用SpannableString来实现
            val spannableString = SpannableString(floorData.title)

            val leadingMarginSpan: LeadingMarginSpan = LeadingMarginSpan.Standard(
                (floorData.tag.length * context.getAttrDimensionPixelSize(R.attr.isv_font_size_T9)
                        + 14f.dp2px.toInt()), 0
            )

            spannableString.setSpan(
                leadingMarginSpan,
                0,
                floorData.title.length,
                Spanned.SPAN_INCLUSIVE_EXCLUSIVE
            )
            title.text = spannableString
            tag.visibility = View.VISIBLE
        } else {
            //如果标签不显示，则正常渲染标题，隐藏标签View
            title.text = floorData.title
            tag.visibility = View.GONE
        }
    }

    override fun onDestroy() {

    }


}