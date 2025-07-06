package com.github.floormanager

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.github.floormanager.floorManager.adapter.OptimizedFloorAdapter
import com.github.floormanager.floorManager.entity.FloorUIConfig
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.pdFloor.entity.*
import com.github.floormanager.pdFloor.util.PDBaseFloorConstants
import com.github.floormanager.utils.dp2px
import java.math.BigDecimal

/**
 * 主活动类
 * 负责展示楼层列表和管理楼层数据渲染的核心Activity
 */
class MainActivity : AppCompatActivity() {

    private lateinit var adapter: OptimizedFloorAdapter
    private lateinit var recycler: RecyclerView
    private val moduleName = MyApp.PRODUCT_DETAIL_MODULE

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initOptimizedAdapter()
        initRecyclerView()
        initFloor()
    }

    /**
     * 初始化优化版 Adapter
     */
    private fun initOptimizedAdapter() {
        adapter = OptimizedFloorAdapter(
            context = this,
            moduleName = moduleName,
            coroutineScope = lifecycleScope // 支持异步处理
        )
        
        // 设置数据更新监听器
        adapter.onDataUpdateListener = { floorList ->
            Log.d("MainActivity", "楼层数据已更新: ${floorList.size} 个楼层")
            Toast.makeText(this, "楼层数据已更新: ${floorList.size} 个楼层", Toast.LENGTH_SHORT).show()
        }
        
        // 设置错误处理监听器
        adapter.onErrorListener = { message, throwable ->
            Log.e("MainActivity", "楼层错误: $message", throwable)
            Toast.makeText(this, "楼层错误: $message", Toast.LENGTH_LONG).show()
        }
    }

    private fun initRecyclerView() {
        recycler = findViewById(R.id.recycler)
        recycler.setItemViewCacheSize(20)
        recycler.adapter = adapter
    }

    private fun initFloor() {
        val floorList = createFloorData()
        
        // 使用优化版 Adapter 设置数据（支持异步处理）
        adapter.setFloorData(floorList)
        
        // 打印缓存信息（用于调试）
        Log.d("MainActivity", "缓存信息: ${adapter.getCacheInfo()}")
    }

    /**
     * 创建楼层数据
     */
    private fun createFloorData(): List<FloorEntity> {
        val floorList = ArrayList<FloorEntity>()

        // 头图楼层
        val floorTopImageEntity = FloorEntity(PDBaseFloorConstants.floorTopImageType)
        floorTopImageEntity.floorUIConfig.needSetBackground = false
        floorTopImageEntity.floorData = FloorTopImageData(
            arrayListOf("https://picsum.photos/400/400"),
            FloorTopImageData.MediaItemData(true, "a")
        )
        floorList.add(floorTopImageEntity)

        // 价格楼层
        val floorPriceEntity = FloorEntity(PDBaseFloorConstants.floorPriceType)
        floorPriceEntity.floorData = FloorPriceData(
            BigDecimal(1000.00),
            BigDecimal(1000.00),
            isSlash = true, 
            isLike = false
        )
        floorList.add(floorPriceEntity)

        // 标题楼层
        val floorTitleEntity = FloorEntity(PDBaseFloorConstants.floorTextType)
        floorTitleEntity.floorData = FloorTitleData("极狐 阿尔法T1000元订金购车订金 冰灰")
        floorList.add(floorTitleEntity)

        // 促销楼层
        val floorPromoEntity = FloorEntity(PDBaseFloorConstants.floorPromoType)
        floorPromoEntity.floorData = com.github.floormanager.pdFloor.entity.FloorPromoData(
            arrayListOf("优惠券1", "优惠券2"),
            arrayListOf(
                com.github.floormanager.pdFloor.entity.FloorPromoData.PromoItemData("标题", "内容")
            )
        )
        floorList.add(floorPromoEntity)

        // 属性楼层
        val floorAttrEntity = FloorEntity(PDBaseFloorConstants.floorAttrType)
        floorAttrEntity.floorData = FloorAttrData("冰灰")
        floorList.add(floorAttrEntity)

        // 门店楼层
        val floorStoreEntity = FloorEntity(PDBaseFloorConstants.floorStoreType)
        floorStoreEntity.floorData = FloorStoreData(
            "ARCFOX 北京极狐交付中心",
            "北京大兴区亦庄地区经济技术开发区科创三街15号"
        )
        floorList.add(floorStoreEntity)

        // 地址楼层
        val floorAddressEntity = FloorEntity(PDBaseFloorConstants.floorAddressType)
        floorAddressEntity.floorData = FloorAddressData("收货地址blablabla")
        floorList.add(floorAddressEntity)

        // 服务楼层
        val floorServiceEntity = FloorEntity(PDBaseFloorConstants.floorServiceType)
        floorServiceEntity.floorData = FloorServiceData(
            arrayListOf(FloorServiceData.ServiceItemData("支持自提"))
        )
        // 自定义UI配置（不使用默认配置）
        floorServiceEntity.floorUIConfig.useDefaultSetting = false
        floorServiceEntity.floorUIConfig.backgroundColor = 0xB3FFFFFF.toInt()
        floorServiceEntity.floorUIConfig.cornerTopRadius = 8f.dp2px.toFloat()
        floorServiceEntity.floorUIConfig.cornerBottomRadius = 8f.dp2px.toFloat()
        floorServiceEntity.floorUIConfig.cornerType = FloorUIConfig.CornerType.BOTTOM
        floorList.add(floorServiceEntity)

        // 评论楼层
        val floorCommentEntity = FloorEntity(PDBaseFloorConstants.floorCommentType)
        floorCommentEntity.floorData = FloorCommentData(
            789, 99, com.github.floormanager.pdFloor.entity.CommentItemData(
                "樱****子", "", 4, "2021.09.17", 
                arrayListOf("一流服务", "清澈透亮", "高级", "正品")
            )
        )
        floorList.add(floorCommentEntity)

        // 店铺楼层
        val floorShopEntity = FloorEntity(PDBaseFloorConstants.floorShopType)
        floorShopEntity.floorData = FloorShopData(
            "ARCFOX 极狐新能源汽车官方旗舰店",
            null,
            showChat = true,
            isFollow = true
        )
        floorList.add(floorShopEntity)

        // 规格楼层
        val floorSpecificationEntity = FloorEntity(PDBaseFloorConstants.floorSpecificationType)
        floorSpecificationEntity.floorData = FloorSpecificationData(
            arrayListOf(
                FloorSpecificationData.SpecItemData("商品编号", "40533"),
                FloorSpecificationData.SpecItemData("主体", null),
                FloorSpecificationData.SpecItemData("容量", "500mL"),
                FloorSpecificationData.SpecItemData("酒精度", "53%vol"),
                FloorSpecificationData.SpecItemData("参数", null),
                FloorSpecificationData.SpecItemData("度数", "50度及以上"),
                FloorSpecificationData.SpecItemData("包装形式", "瓶装"),
                FloorSpecificationData.SpecItemData("香型", "酱香型"),
                FloorSpecificationData.SpecItemData("产地", "贵州"),
                FloorSpecificationData.SpecItemData("容量", "500-749mL")
            )
        )
        floorList.add(floorSpecificationEntity)

        // 售后楼层
        val floorAfterSaleEntity = FloorEntity(PDBaseFloorConstants.floorAfterSaleType)
        floorAfterSaleEntity.floorData = FloorAfterSaleData(
            null,
            "平台卖家销售并发货的商品，由平台卖家提供发票和相应的售后服务。请您放心购买！\n" +
                    "注：因厂家会在没有任何提前通知的情况下更改产品包装、产地或者一些附件，本司不能确保客户收到的货物与商城图片、产地、附件说明完全一致。只能确保为原厂正货！并且保证与当时市场上同样主流新品一致。若本商城没有及时更新，请大家谅解！"
        )
        floorList.add(floorAfterSaleEntity)

        // 推荐楼层
        val floorRecommendEntity = FloorEntity(PDBaseFloorConstants.floorRecommendType)
        val recommendItemData = FloorRecommendData.RecommendItemData(
            "123",
            "五芳斋288型五芳皓月糯月饼礼盒",
            BigDecimal("190.00"),
            BigDecimal("192.00"),
            "https://picsum.photos/300/300",
            true
        )
        floorRecommendEntity.floorData = FloorRecommendData(
            arrayListOf(
                recommendItemData, recommendItemData, recommendItemData, recommendItemData,
                recommendItemData, recommendItemData, recommendItemData, recommendItemData,
                recommendItemData, recommendItemData, recommendItemData, recommendItemData
            )
        )
        floorRecommendEntity.floorUIConfig.needSetBackground = false
        floorList.add(floorRecommendEntity)

        // Web内容楼层
        val floorWebEntity = FloorEntity(PDBaseFloorConstants.floorWebContentType)
        floorWebEntity.floorData = FloorWebContentData(
            "<p><img src=\"https://picsum.photos/600/400\"><br></p>" +
            "<p><img src=\"https://picsum.photos/600/500\"><br></p>" +
            "<p><img src=\"https://picsum.photos/600/450\"></p>" +
            "<p><img src=\"https://picsum.photos/600/350\"></p>" +
            "<p><br></p><p><br></p>"
        )
        floorList.add(floorWebEntity)

        return floorList
    }

    /**
     * 演示如何刷新单个楼层的数据
     */
    private fun refreshSingleFloor() {
        // 刷新价格楼层的数据
        val newPriceData = FloorPriceData(
            BigDecimal(800.00),
            BigDecimal(1000.00),
            isSlash = true,
            isLike = false
        )
        adapter.refreshFloorData(PDBaseFloorConstants.floorPriceType, newPriceData)
    }

    override fun onDestroy() {
        super.onDestroy()
        // 清理资源（优化版本提供的清理方法）
        adapter.cleanup()
    }
}