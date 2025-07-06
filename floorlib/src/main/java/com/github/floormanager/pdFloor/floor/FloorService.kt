package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.view.View
import com.github.floormanager.widget.TagFlowLayout
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.adapter.FloorServiceAdapter
import com.github.floormanager.pdFloor.entity.FloorServiceData

/**
 * 服务楼层实现类
 * 负责渲染商品服务信息列表，采用标签流布局展示各项服务
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */

class FloorService(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity) {


    //服务的Layout，采用标签来实现
    private lateinit var serviceLayout: TagFlowLayout
    //服务Layout对应的Adapter
    private val serviceAdapter = FloorServiceAdapter()

    override fun initView() {
        serviceLayout = requireViewById(R.id.services)
        serviceLayout.adapter = serviceAdapter
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorServiceData) {
            return
        }

        //如果没有业务数据则不显示布局
        if (floorData.serviceList.isNullOrEmpty()) {
            serviceLayout.visibility = View.GONE
            return
        }

        //具体渲染逻辑交给服务的Adapter
        serviceAdapter.setData(floorData.serviceList)
    }

    override fun onDestroy() {

    }


}