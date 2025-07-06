package com.github.floormanager.pdFloor.adapter

import android.app.Activity
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import androidx.viewpager.widget.PagerAdapter
import com.github.floormanager.utils.ImageLoader
import com.github.floormanager.utils.ImageLoader.NOT_SET
import com.github.floormanager.utils.SimpleImageLoadStateCallback
import com.github.floormanager.video.FloatingVideoManager
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorTopImageData

/**
 * 头图ViewPager适配器
 * 负责渲染头图楼层的图片和视频内容，支持视频播放和图片点击交互
 * 
 * @param itemClickListener 图片与视频播放按钮的点击监听
 * @param placeHolderImg 占位图的图片资源id
 */
class FloorTopImageAdapter(
    private val itemClickListener: View.OnClickListener,
    private val placeHolderImg: Int = NOT_SET
) : PagerAdapter() {

    //头图楼层Data
    private lateinit var data: FloorTopImageData

    //视频管理类
    private var videoManager: FloatingVideoManager? = null

    /**
     * 获取视频管理类
     * 头图楼层需要拿到这个类并处理生命周期相关逻辑
     * 
     * @return 视频管理类
     */
    fun getVideoManager(): FloatingVideoManager? {
        return videoManager
    }

    /**
     * 清除视频管理类
     */
    fun cleanViewManager() {
        videoManager = null
    }

    fun setData(data: FloorTopImageData) {
        this.data = data
        notifyDataSetChanged()
    }

    override fun instantiateItem(container: ViewGroup, position: Int): Any {
        val itemView: View
        if (data.hasVideoData() && position == 0) {
            //如果存在视频数据且是第一页则渲染成视频页
            itemView = initVideoView(container, data.mediaData)
        } else {
            //否则渲染成普通图片页
            val imgUrl = data.imageList[if (data.hasVideoData()) position - 1 else position]
            itemView = initImageView(container, imgUrl)
            itemView.tag = position
        }
        container.addView(itemView)
        return itemView
    }

    override fun destroyItem(container: ViewGroup, position: Int, `object`: Any) {
        container.removeView(`object` as View)
    }

    private fun initVideoView(
        container: ViewGroup,
        videoData: FloorTopImageData.MediaItemData?
    ): View {
        val rootView: View = LayoutInflater.from(container.context)
            .inflate(R.layout.pd_base_floor_top_image_item_video, container, false)

        //视频封面的ImageView
        val image = rootView.findViewById<ImageView>(R.id.img)

        //视频播放按钮的ImageView
        val btn = rootView.findViewById<ImageView>(R.id.btnVideo)

        //为视频播放按钮添加点击监听
        btn.setOnClickListener(itemClickListener)

        //初始化视频管理类
        if (videoManager == null) {
            videoManager = FloatingVideoManager(
                ((container.context as Activity).findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup),
                rootView.findViewById<FrameLayout>(R.id.videoContentLayout),
                videoData!!.mediaUrl!!
            )
        }

        //渲染视频封面图
        ImageLoader.loadImage(
            image,
            videoData?.coverUrl,
            placeHolderImg,
            placeHolderImg,
            SimpleImageLoadStateCallback(
                image,
                backgroundColor = Color.parseColor("#FFeeeeee")
            )
        )

        return rootView
    }

    private fun initImageView(container: ViewGroup, imgUrl: String?): View {
        val rootView: View = LayoutInflater.from(container.context)
            .inflate(R.layout.pd_base_floor_top_image_item_normal, container, false)

        //商品图的ImageView
        val image = rootView.findViewById<ImageView>(R.id.img)

        //为商品图View添加点击监听
        rootView.setOnClickListener(itemClickListener)

        //渲染商品图
        ImageLoader.loadImage(
            image,
            imgUrl,
            placeHolderImg,
            placeHolderImg,
            SimpleImageLoadStateCallback(
                image,
                backgroundColor = Color.parseColor("#FFeeeeee")
            )
        )

        return rootView
    }

    override fun getCount(): Int {
        return data.getCount()
    }

    override fun isViewFromObject(view: View, `object`: Any): Boolean {
        return view == `object`
    }

}