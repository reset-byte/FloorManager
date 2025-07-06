package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.viewpager.widget.ViewPager
import com.github.floormanager.utils.DensityUtils
import com.github.floormanager.utils.dp2px
import com.github.floormanager.floorManager.entity.FloorClickData
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.adapter.FloorTopImageAdapter
import com.github.floormanager.pdFloor.entity.FloorTopImageData
import com.github.floormanager.pdFloor.utils.getAttrResourceId

/**
 * 头图楼层实现类
 * 负责渲染商品头图和视频内容，支持轮播展示和视频播放功能
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */
class FloorTopImage(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity),
    ViewPager.OnPageChangeListener, View.OnClickListener {

    //定义不同点击位置的标识，为了给商详组件处理点击逻辑时做区分
    companion object {
        const val CLICK_POSITION_IMAGE = "image"
        const val CLICK_POSITION_MEDIA = "media"
    }

    //头图整体的ViewPager
    private lateinit var topImageViewPager: ViewPager

    //头图当前页数指示器的TextView
    private lateinit var imageIndicator: TextView

    //为了处理头部为状态栏高度预留的padding而存在的View
    private lateinit var paddingView: View

    private val pagerAdapter =
        FloorTopImageAdapter(this, context.getAttrResourceId(R.attr.isv_image_placeholder))

    override fun initView() {
        topImageViewPager = requireViewById(R.id.pager)
        imageIndicator = requireViewById(R.id.pageIndicator)
        paddingView = requireViewById<View>(R.id.paddingView)

        topImageViewPager.addOnPageChangeListener(this)
        topImageViewPager.setOnClickListener(this)

        //将头图渲染的部分设置为屏幕宽度尺寸的正方形
        var params: ViewGroup.LayoutParams? = topImageViewPager.layoutParams
        params?.height = context.resources.displayMetrics.widthPixels
        topImageViewPager.layoutParams = params

        //将头部预留的高度设置为状态栏的真实高度
        params = paddingView.layoutParams
        params?.height = DensityUtils.getStatusBarHeight(context)
        paddingView.layoutParams = params
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorTopImageData) {
            return
        }

        //如果没有图片数据则不显示
        if (floorData.imageList.isNullOrEmpty()) {
            return
        }

        //具体渲染逻辑交给头图的Adapter
        pagerAdapter.setData(floorData)
        topImageViewPager.adapter = pagerAdapter

        //设置指示器
        if (floorData.imageList.size > 1) {
            imageIndicator.visibility = View.VISIBLE
            updatePageIndicator()
        } else {
            imageIndicator.visibility = View.GONE
        }
    }

    override fun onDestroy() {
        //页面被销毁时需要清理视频数据
        pagerAdapter.getVideoManager()?.onDestroy()
    }

    override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {
    }

    override fun onPageSelected(position: Int) {
        updatePageIndicator()
    }

    override fun onPageScrollStateChanged(state: Int) {
    }

    override fun onClick(v: View?) {
        //点击图片将事件传递给商详组件触发查看大图
        eventViewModel?.clickEvent?.value = FloorClickData(floorEntity.floorType, data = topImageViewPager.currentItem)
    }

    /**
     * 当楼层不可见时处理相关逻辑
     */
    override fun onDetachFromWindow() {
        pagerAdapter.getVideoManager()?.let {
            if (it.isPlaying) {
                //正在播放状态则切换至小窗播放，传入的参数为小窗距离顶部的高度
                it.showSmallVideo(70f.dp2px)
            } else {
                //非播放状态则重置播放状态
                it.exitPlay()
            }
        }
    }

    /**
     * 当楼层可见时处理相关逻辑
     */
    override fun onAttachToWindow() {
        pagerAdapter.getVideoManager()?.let {
            if (it.isPlaying) {
                //正在播放状态则切换至楼层内正常播放状态
                it.showNormalMode()
            } else {
                //非播放状态则重置播放状态
                it.exitPlay()
            }
        }
    }

    override fun onPause() {
        //宿主Activity切换至非前台状态则重置播放状态
        pagerAdapter.getVideoManager()?.exitPlay()
    }

    private fun updatePageIndicator() {
        val adapter = topImageViewPager.adapter as? FloorTopImageAdapter
        val currentItem = topImageViewPager.currentItem
        val totalCount = adapter?.count ?: 0
        val text = "${currentItem + 1}/$totalCount"
        imageIndicator.text = text
    }

}