package com.github.floormanager.pdFloor.floor

import android.content.Context
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorWebContentData
import com.github.floormanager.pdFloor.view.WebDescriptionView

/**
 * Web详情楼层实现类
 * 负责渲染商品的HTML详情内容，内嵌WebView进行展示
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */
class FloorWebContent(context: Context, floorEntity: FloorEntity) :
    OptimizedBaseFloor(context, floorEntity) {

    //Web内容的包装视图View
    private lateinit var webDescriptionView: WebDescriptionView


    override fun initView() {
        webDescriptionView = requireViewById(R.id.webDescription)
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorWebContentData) {
            return
        }

        //将详情的html内容传递给web包装view来进行渲染相关逻辑处理
        webDescriptionView.loadHtml(floorData.content)
    }

    override fun onDestroy() {
        //宿主Activity被销毁时需要销毁内嵌的WebView
        webDescriptionView.onDestroy()
    }


}