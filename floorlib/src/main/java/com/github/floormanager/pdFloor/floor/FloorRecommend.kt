package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.view.View
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.floormanager.pdFloor.utils.getAttrDimension
import com.github.floormanager.floorManager.entity.FloorClickData
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.adapter.FloorRecommendAdapter
import com.github.floormanager.pdFloor.entity.FloorRecommendData
import com.github.floormanager.utils.dp2px

/**
 * 推荐楼层实现类
 * 负责渲染商品推荐列表，采用2列网格布局展示推荐商品
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */
class FloorRecommend(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity),
    View.OnClickListener {

    //推荐瀑布流对应的RecyclerView
    private var recommendRecyclerView: RecyclerView? = null

    //推荐瀑布流对应的RecyclerView的Adapter
    private val adapter = FloorRecommendAdapter()

    override fun initView() {
        //设置推荐瀑布流条目点击监听，触发跳转至单个商品的商详页
        adapter.itemClickListenr = this

        recommendRecyclerView = findViewById(R.id.recyclerView)

        //瀑布流的列数为2列
        recommendRecyclerView?.layoutManager = GridLayoutManager(context, 2)

        recommendRecyclerView?.adapter = adapter

        //根据标准化设计标准，推荐这里需要单独指定一个左右的边距
        val margin = context.getAttrDimension(R.attr.isv_space_W12) - 3f.dp2px
        val layoutParams = recommendRecyclerView?.layoutParams as ConstraintLayout.LayoutParams
        layoutParams.leftMargin = margin.toInt()
        layoutParams.rightMargin = margin.toInt()
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorRecommendData) {
            return
        }
        //具体渲染逻辑交给推荐的Adapter
        adapter.setData(floorData.productList)
    }

    override fun onDestroy() {

    }

    override fun onClick(v: View?) {
        //点击楼层将事件传递给商详组件触发跳转至推荐页面
        eventViewModel?.clickEvent?.value = FloorClickData(floorEntity.floorType)
    }


}