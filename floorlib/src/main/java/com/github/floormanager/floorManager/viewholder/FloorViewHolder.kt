package com.github.floormanager.floorManager.viewholder

import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.github.floormanager.floorManager.entity.FloorUIConfig
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor

/**
 * 楼层的ViewHolder
 * 独立的ViewHolder类，不依赖于特定的Adapter
 * 
 * @param itemView 楼层根View
 * @param floor 楼层实现类
 */
class FloorViewHolder(itemView: View, var floor: OptimizedBaseFloor) :
    RecyclerView.ViewHolder(itemView), View.OnLayoutChangeListener {

    init {
        //初始化楼层间距
        setFloorUIMargin(floor.floorEntity.floorUIConfig)
        //初始化楼层的背景
        setFloorUI(itemView, floor.floorEntity.floorUIConfig)
        //设置楼层根视图并初始化
        floor.setRootView(itemView)
    }

    /**
     * 初始化楼层背景
     * 
     * @param rootView 楼层根View
     * @param config 楼层的UI配置
     */
    private fun setFloorUI(rootView: View, config: FloorUIConfig?) {

        //当宿主Activity或者Fragment已经被销毁时不执行下面的逻辑，避免crash
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN_MR1) {
            if (itemView.context is androidx.fragment.app.FragmentActivity && (itemView.context as androidx.fragment.app.FragmentActivity).isDestroyed) {
                return
            }

            if (itemView.context is Activity && (itemView.context as Activity).isDestroyed) {
                return
            }
        }

        if (itemView.measuredWidth > 0 && itemView.measuredHeight > 0) {
            //如果itemView已经完成了宽高的测量，则进行背景设置，为楼层实现类的楼层高度赋值，并移除布局改变的监听
            floor.floorEntity.floorHeight = itemView.measuredHeight
            setFloorUIBg(rootView, config)
            itemView.removeOnLayoutChangeListener(this)
        } else {
            //如果itemView没有完成宽高的测量，则添加布局改变的监听，等能获取到宽高了再设置背景，避免使用图片时因为宽高不准产生拉伸
            itemView.removeOnLayoutChangeListener(this)
            itemView.addOnLayoutChangeListener(this)
        }
    }

    /**
     * 初始化楼层背景的真正实现
     * 
     * @param rootView 楼层根View
     * @param config 楼层的UI配置
     */
    private fun setFloorUIBg(rootView: View, config: FloorUIConfig?) {
        //如果配置未空或者配置中明确指出不需要设置背景则直接返回
        if (config == null || !config.needSetBackground) {
            return
        }

        if (!TextUtils.isEmpty(config.backgroundImgUrl)) {
            //如果配置中图片url不为空则走设置网络图片逻辑
            setFloorUIBgImg(config.backgroundImgUrl)
        } else if (config.backgroundImgId != 0) {
            //如果配置中图片id不为空则走设置本地图片逻辑
            setFloorUIBgImgLocal(config.backgroundImgId)
        } else {
            //如果配置中没有指定背景图相关字段则走设置背景色与圆角逻辑
            setFloorUICornerAndBgColor(rootView, config)
        }
    }

    /**
     * 设置楼层的间距
     * 
     * @param config 楼层的UI配置
     */
    private fun setFloorUIMargin(config: FloorUIConfig) {
        val layoutParams = RecyclerView.LayoutParams(
            RecyclerView.LayoutParams.MATCH_PARENT,
            RecyclerView.LayoutParams.WRAP_CONTENT
        )

        layoutParams.topMargin = config.marginTop.toInt()
        layoutParams.bottomMargin = config.marginBottom.toInt()

        //这里不单独指定具体的左还是右边距，统一用一个水平边距值来表示
        layoutParams.leftMargin = config.marginLeft.toInt()
        layoutParams.rightMargin = config.marginRight.toInt()

        itemView.layoutParams = layoutParams
    }

    /**
     * 为楼层设置本地图背景（商详暂无真实业务使用场景，未考虑圆角，如果有圆角需求可以先直接做到切图里）
     * 
     * @param imgId 背景图资源id
     */
    private fun setFloorUIBgImgLocal(imgId: Int) {
        itemView.setBackgroundResource(imgId)
    }

    /**
     * 为楼层设置网络图背景（商详暂无真实业务使用场景，未考虑圆角，如果有圆角需求可以先直接做到切图里）
     * 
     * @param imgUrl 背景图URL
     */
    private fun setFloorUIBgImg(imgUrl: String) {
        //先根据楼层尺寸下载好图片
        Glide.with(itemView.context)
            .asBitmap()
            .load(imgUrl)
            .into(object : CustomTarget<Bitmap>() {
                override fun onResourceReady(resource: Bitmap, transition: Transition<in Bitmap>?) {
                    //下载成功后为楼层根View设置背景
                    @Suppress("DEPRECATION")
                    itemView.setBackgroundDrawable(BitmapDrawable(itemView.resources, resource))
                }

                override fun onLoadCleared(placeholder: Drawable?) {
                    // Handle image loading error
                }
            })
    }

    /**
     * 为楼层设置背景色与圆角
     * 
     * @param rootView 楼层根View
     * @param config 楼层的UI配置
     */
    private fun setFloorUICornerAndBgColor(rootView: View, config: FloorUIConfig) {
        //初始化楼层背景Drawable，因为要指定圆角所以使用GradientDrawable
        val gd = GradientDrawable()
        //shape指定为圆角矩形
        gd.shape = GradientDrawable.RECTANGLE
        //设置背景色
        gd.setColor(config.backgroundColor)

        //根据楼层不同的圆角类型进行drawable的圆角半径设置
        when (config.cornerType) {
            FloorUIConfig.CornerType.TOP -> {
                gd.cornerRadii = floatArrayOf(
                    config.cornerTopRadius,
                    config.cornerTopRadius,
                    config.cornerTopRadius,
                    config.cornerTopRadius,
                    0f,
                    0f,
                    0f,
                    0f
                )
            }
            FloorUIConfig.CornerType.BOTTOM -> {
                gd.cornerRadii = floatArrayOf(
                    0f,
                    0f,
                    0f,
                    0f,
                    config.cornerBottomRadius,
                    config.cornerBottomRadius,
                    config.cornerBottomRadius,
                    config.cornerBottomRadius
                )
            }
            FloorUIConfig.CornerType.ALL -> {
                gd.cornerRadii = floatArrayOf(
                    config.cornerTopRadius,
                    config.cornerTopRadius,
                    config.cornerTopRadius,
                    config.cornerTopRadius,
                    config.cornerBottomRadius,
                    config.cornerBottomRadius,
                    config.cornerBottomRadius,
                    config.cornerBottomRadius
                )
            }
            FloorUIConfig.CornerType.NONE -> {
                // 不设置圆角
            }
        }
        //根据不同Android版本的要求来设置楼层View的背景
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
            rootView.background = gd
        } else {
            rootView.setBackgroundDrawable(gd)
        }
    }

    /**
     * 根据业务数据刷新楼层View
     * 
     * @param floorData 楼层业务数据
     */
    fun showData(floorData: Any?) {
        //调用楼层实现类的渲染方法进行刷新
        floor.showData(floorData)
        //如果楼层高度还没初始化，手动调用楼层根View的测量方法进行测量
        if (floor.floorEntity.floorHeight <= 0) {
            itemView.measure(
                View.MeasureSpec.makeMeasureSpec(
                    itemView.resources.displayMetrics.widthPixels,
                    View.MeasureSpec.EXACTLY
                ),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
            );
            floor.floorEntity.floorHeight = itemView.measuredHeight
        }
    }

    override fun onLayoutChange(
        v: View?,
        left: Int,
        top: Int,
        right: Int,
        bottom: Int,
        oldLeft: Int,
        oldTop: Int,
        oldRight: Int,
        oldBottom: Int
    ) {
        //楼层布局发生改变时尝试设置楼层背景，设置成功就会移除这个监听，该方法便不再调用
        setFloorUI(itemView, floor.floorEntity.floorUIConfig)
    }
} 