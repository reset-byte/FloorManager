package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.adapter.FloorSpecificationAdapter
import com.github.floormanager.pdFloor.entity.FloorSpecificationData

/**
 * 规格楼层实现类
 * 负责渲染商品规格参数信息，支持展开收起功能
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */
class FloorSpecification(context: Context, floorEntity: FloorEntity) :
    OptimizedBaseFloor(context, floorEntity) {

    //规格列表的RecyclerView
    private lateinit var specificationRecyclerView: RecyclerView

    //展开更多按钮的TextView
    private lateinit var btnExpansion: TextView

    //规格列表的RecyclerView的Adapter
    private val adapter = FloorSpecificationAdapter()

    override fun initView() {
        specificationRecyclerView = requireViewById(R.id.recyclerView)
        btnExpansion = requireViewById(R.id.btnExpansion)
        specificationRecyclerView.adapter = adapter
        btnExpansion.setOnClickListener {
            //切换规格列表的展开or隐藏
            adapter.changeExpansion()
            btnExpansion.text =
                context.getText(if (adapter.isExpansion()) R.string.pd_base_floor_pack_up else R.string.pd_base_floor_expansion)
            //通过是否选中来显示展开隐藏对应的不同icon图标
            btnExpansion.isSelected = adapter.isExpansion()
        }
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorSpecificationData) {
            return
        }

        //具体渲染逻辑交给规格的Adapter
        adapter.setData(floorData.specList)

        //规格的行数大于最小行数时才需要显示展开按钮
        btnExpansion.visibility =
            if (floorData.specList != null && floorData.specList.size > adapter.getMinSize()) View.VISIBLE else View.GONE
    }

    override fun onDestroy() {

    }


}