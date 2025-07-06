package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorAfterSaleData

/**
 * 售后楼层实现类
 * 负责渲染售后服务和包装信息，包括售后政策和包装说明内容
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */
class FloorAfterSale(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity) {

    //售后部分的标题TextView
    private lateinit var afterSaleTitle: TextView
    //包装部分的标题TextView
    private lateinit var packageTitle: TextView
    //售后部分的内容TextView
    private lateinit var afterSaleContent: TextView
    //包装部分的内容TextView
    private lateinit var packageContent: TextView

    override fun initView() {
        afterSaleTitle = requireViewById(R.id.afterSaleTitle)
        packageTitle = requireViewById(R.id.packageTitle)
        afterSaleContent = requireViewById(R.id.afterSaleContent)
        packageContent = requireViewById(R.id.packageContent)
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorAfterSaleData) {
            return
        }

        if (!floorData.afterSaleContent.isNullOrEmpty()) {
            afterSaleTitle.visibility = View.VISIBLE
            afterSaleContent.visibility = View.VISIBLE
            afterSaleContent.text = floorData.afterSaleContent
        } else {
            afterSaleTitle.visibility = View.GONE
            afterSaleContent.visibility = View.GONE
        }

        if (!floorData.packageContent.isNullOrEmpty()) {
            packageTitle.visibility = View.VISIBLE
            packageContent.visibility = View.VISIBLE
            packageContent.text = floorData.packageContent
        } else {
            packageTitle.visibility = View.GONE
            packageContent.visibility = View.GONE
        }
    }

    override fun onDestroy() {

    }


}