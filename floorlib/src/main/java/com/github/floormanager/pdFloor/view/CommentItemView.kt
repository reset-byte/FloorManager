package com.github.floormanager.pdFloor.view

import android.content.Context
import android.util.AttributeSet
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import com.github.floormanager.pdFloor.utils.getAttrResourceId
import com.github.floormanager.ui.ScaleRatingBar
import com.github.floormanager.widget.TagFlowLayout
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.adapter.CommentTagAdapter
import com.github.floormanager.utils.ImageLoader
import com.github.floormanager.utils.dp2px

/**
 * 单条评论的封装视图
 * 用于展示单条评论信息，包括用户信息、评分、时间和标签
 * 
 * @param context 上下文环境
 * @param attrs 属性集合
 */

class CommentItemView : ConstraintLayout {

    //提交评论的用户名的TextView
    private lateinit var userName: TextView

    //提交评论的用户头像的ImageView
    private lateinit var userIcon: ImageView

    //提交评论时间的TextView
    private lateinit var time: TextView

    //评论星级的RatingBar
    private lateinit var ratingBar: ScaleRatingBar

    //评论标签的Layout
    private lateinit var tagList: TagFlowLayout

    //评论标签的Adapter
    private val tagAdapter = CommentTagAdapter()

    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
        init()
    }

    /**
     * 初始化视图组件
     */
    private fun init() {
        LayoutInflater.from(context)
            .inflate(R.layout.pd_base_floor_comment_item_view_layout, this, true)
        userName = findViewById(R.id.userName)
        userIcon = findViewById(R.id.icon)
        time = findViewById(R.id.time)
        ratingBar = findViewById(R.id.ratingBar)
        tagList = findViewById(R.id.tagList)
        tagList.adapter = tagAdapter
    }

    /**
     * 渲染视图数据
     * 
     * @param data 评论数据
     */
    fun setData(data: com.github.floormanager.pdFloor.entity.CommentItemData) {
        userName.text = data.userName
        time.text = data.time
        ratingBar.rating = data.commentPoint?.toFloat() ?: 5f
        ImageLoader.loadCircleImage(
            userIcon,
            data.userIcon,
            40f.dp2px,
            40f.dp2px,
            context.getAttrResourceId(R.attr.isv_icon_user_default),
            context.getAttrResourceId(R.attr.isv_icon_user_default)
        )
        tagAdapter.setData(data.tagList?.filterNotNull() ?: emptyList())
    }
}