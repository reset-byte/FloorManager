package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.text.TextUtils
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.github.floormanager.floorManager.entity.FloorClickData
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorShopData
import com.github.floormanager.pdFloor.utils.getAttrDimension
import com.github.floormanager.utils.ImageLoader
import com.github.floormanager.utils.dp2px

/**
 * 店铺楼层实现类
 * 负责渲染店铺信息，包括店铺名称、图标、进店按钮、客服按钮和关注按钮
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */
class FloorShop(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity),
    View.OnClickListener {

    companion object {
        const val CLICK_POSITION_SHOP = "shop"
        const val CLICK_POSITION_FOLLOW = "follow"
        const val CLICK_POSITION_CHAT = "chat"
    }

    //店铺名称的TextView
    private lateinit var shopName: TextView

    //店铺图标的ImageView
    private lateinit var shopIcon: ImageView

    //查看店铺详情按钮的TextView
    private lateinit var shopButton: TextView

    //店铺客服按钮的TextView
    private lateinit var serviceButton: TextView

    //店铺关注按钮的TextView
    private lateinit var followButton: TextView

    //店铺图标的圆角半径
    private var imageCornerRadius: Int = 0

    //店铺图标的尺寸
    private var imageSize: Int = 0

    init {
        imageCornerRadius = 6f.dp2px
        imageSize = 60f.dp2px
    }

    override fun initView() {
        shopName = requireViewById(R.id.name)
        shopIcon = requireViewById(R.id.icon)
        shopButton = requireViewById(R.id.button)
        serviceButton = requireViewById(R.id.service)
        followButton = requireViewById(R.id.like)
        shopButton.setOnClickListener(this)
        serviceButton.setOnClickListener(this)
        followButton.setOnClickListener(this)
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorShopData) {
            return
        }

        //渲染店铺名称
        shopName.text = floorData.shopName

        //判断店铺客服按钮是否显示
        serviceButton.visibility = if (floorData.showChat) View.VISIBLE else View.GONE

        //渲染店铺图标
        val cornerRadius = context.getAttrDimension(R.attr.isv_corner_R1).toInt()
        ImageLoader.loadRoundedImage(
            shopIcon,
            if (!TextUtils.isEmpty(floorData.shopIconUrl)) floorData.shopIconUrl else "",
            cornerRadius,
            R.drawable.pd_base_floor_bg_shop_icon_shadow
        )

        //设置关注状态
        followButton.text = if (floorData.isFollow) {
            context.getText(R.string.pd_base_floor_btn_shop_has_like)
        } else {
            context.getText(R.string.pd_base_floor_btn_shop_like)
        }
        followButton.tag = !floorData.isFollow
        followButton.visibility = View.VISIBLE
    }

    override fun onDestroy() {

    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.button -> {
                //点击进店按钮
                eventViewModel?.clickEvent?.value = FloorClickData(
                    floorEntity.floorType,
                    CLICK_POSITION_SHOP
                )
            }
            R.id.service -> {
                //点击客服按钮
                eventViewModel?.clickEvent?.value = FloorClickData(
                    floorEntity.floorType,
                    CLICK_POSITION_CHAT
                )
            }
            R.id.like -> {
                //点击关注按钮
                val newFollowStatus = v.tag as Boolean
                eventViewModel?.clickEvent?.value = FloorClickData(
                    floorEntity.floorType,
                    CLICK_POSITION_FOLLOW,
                    mapOf("isFollow" to newFollowStatus)
                )
            }
        }
    }

}