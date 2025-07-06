package com.github.floormanager

import android.app.Application
import com.github.floormanager.utils.dp2px
import com.github.floormanager.floorManager.entity.FloorCustomConfig
import com.github.floormanager.floorManager.entity.ModuleConfig
import com.github.floormanager.floorManager.manager.FloorManagerProxy
import com.github.floormanager.floorManager.manager.FloorManager
import com.github.floormanager.pdFloor.floor.*
import com.github.floormanager.pdFloor.util.PDBaseFloorConstants

/**
 * 应用程序主类
 * 负责楼层管理框架的全局初始化和配置
 */
class MyApp : Application() {
    companion object {
        const val PRODUCT_DETAIL_MODULE = "product_detail"
    }

    override fun onCreate() {
        super.onCreate()
        initFloor()
    }

    /**
     * 初始化楼层配置
     * 全局配置，所有使用该模块的地方都会生效
     */
    private fun initFloor() {
        val floorManager = FloorManagerProxy.getInstances(PRODUCT_DETAIL_MODULE)
        
        // 设置模块配置
        setupModuleConfig(floorManager)
        
        // 注册所有楼层
        registerAllFloors(floorManager)
    }

    /**
     * 设置模块配置
     */
    private fun setupModuleConfig(floorManager: FloorManager) {
        floorManager.moduleConfig = ModuleConfig(
            floorBackgroundColor = 0xFFFFFFFF.toInt(),
            floorGroupTopMargin = 12f.dp2px.toFloat(),
            floorGroupBottomMargin = 0f,
            floorGroupHorizontalMargin = 12f.dp2px.toFloat(),
            floorCornerTopRadius = 8f.dp2px.toFloat(),
            floorCornerBottomRadius = 8f.dp2px.toFloat()
        )
    }

    /**
     * 注册所有楼层
     * 使用分组和子分组来控制楼层显示顺序和样式
     */
    private fun registerAllFloors(floorManager: FloorManager) {
        // 组0: 头图楼层 (单独显示，无背景)
        floorManager.registerFloor(
            PDBaseFloorConstants.floorTopImageType,
            FloorTopImage::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_top_image_layout, 0, 0)
        )

        // 组1: 基础信息楼层 (价格、标题、促销、属性等)
        floorManager.registerFloor(
            PDBaseFloorConstants.floorPriceType,
            FloorPrice::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_price_layout, 1, 0)
        )
        
        floorManager.registerFloor(
            PDBaseFloorConstants.floorTextType,
            FloorTitle::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_title_layout, 1, 1)
        )
        
        floorManager.registerFloor(
            PDBaseFloorConstants.floorPromoType,
            FloorPromo::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_promo_layout, 1, 2)
        )
        
        floorManager.registerFloor(
            PDBaseFloorConstants.floorAttrType,
            FloorAttr::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_attr_layout, 1, 3)
        )
        
        floorManager.registerFloor(
            PDBaseFloorConstants.floorStoreType,
            FloorStore::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_store_layout, 1, 4)
        )
        
        floorManager.registerFloor(
            PDBaseFloorConstants.floorAddressType,
            FloorAddress::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_address_layout, 1, 5)
        )
        
        floorManager.registerFloor(
            PDBaseFloorConstants.floorServiceType,
            FloorService::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_service_layout, 1, 6)
        )

        // 组2: 交互楼层 (评论、店铺)
        floorManager.registerFloor(
            PDBaseFloorConstants.floorCommentType,
            FloorComment::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_comment_layout, 2, 0)
        )
        
        floorManager.registerFloor(
            PDBaseFloorConstants.floorShopType,
            FloorShop::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_shop_layout, 2, 1)
        )

        // 组3: 详细信息楼层 (规格、售后)
        floorManager.registerFloor(
            PDBaseFloorConstants.floorSpecificationType,
            FloorSpecification::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_specification_layout, 3, 0)
        )
        
        floorManager.registerFloor(
            PDBaseFloorConstants.floorAfterSaleType,
            FloorAfterSale::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_after_sale_layout, 3, 1)
        )

        // 组4: 推荐楼层 (无背景)
        floorManager.registerFloor(
            PDBaseFloorConstants.floorRecommendType,
            FloorRecommend::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_recommend_layout, 4, 0)
        )

        // 组5: 媒体内容楼层 (视频、Web内容)
        floorManager.registerFloor(
            PDBaseFloorConstants.floorVideoType,
            FloorVideo::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_video_layout, 5, 0)
        )
        
        floorManager.registerFloor(
            PDBaseFloorConstants.floorWebContentType,
            FloorWebContent::class.java,
            FloorCustomConfig(com.github.floormanager.floorlib.R.layout.pd_base_floor_web_content_layout, 5, 1)
        )
    }
}