package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.floormanager.floorManager.entity.FloorClickData
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorCommentData
import com.github.floormanager.pdFloor.view.CommentItemView

/**
 * 评论楼层实现类
 * 负责渲染商品评论信息，包括评论数量、好评率和第一条评论展示
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */

class FloorComment(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity),
    View.OnClickListener {

    //评论标题的TextView
    private lateinit var title: TextView
    //评论楼层查看更多TextView
    private lateinit var button: TextView
    //楼层内显示的一条评论的View
    private lateinit var itemView: CommentItemView

    override fun initView() {
        title = requireViewById(R.id.title)
        button = requireViewById(R.id.button)
        itemView = requireViewById(R.id.itemView)
        rootView?.setOnClickListener(this)
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorCommentData) {
            return
        }

        //根据评论数是否大于0来决定标题显示的内容
        val hasComment = floorData.count != null && floorData.count > 0

        title.text = if (hasComment) context.getString(
            R.string.pd_base_floor_comment_title,
            floorData.count
        ) else context.getString(R.string.pd_base_floor_comment_title_no_comment)


        //如果没有评论则不显示好评率只显示查看更多的icon
        val buttonText = StringBuilder()
        buttonText.append(
            if (floorData.applauseRate != null && hasComment) context.getString(
                R.string.pd_base_floor_comment_applause_rate,
                floorData.applauseRate
            ) else {
                ""
            }
        )
        buttonText.append(" ")
        buttonText.append(
            context.getString(
                R.string.pd_base_floor_icon_more
            )
        )
        button.text = buttonText.toString()

        //渲染外部显示的一条评论的具体内容
        if (floorData.itemData != null && hasComment) {
            itemView.visibility = View.VISIBLE
            itemView.setData(floorData.itemData)
        } else {
            itemView.visibility = View.GONE
        }
    }

    override fun onDestroy() {

    }

    override fun onClick(v: View?) {
        //点击楼层将事件传递给商详组件触发跳转至评论页面
        eventViewModel?.clickEvent?.value = FloorClickData(floorEntity.floorType)
    }


}