package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.floormanager.floorManager.entity.FloorClickData
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorAttrData

/**
 * 已选属性楼层实现类
 * 负责显示用户已选择的商品属性信息，点击可触发属性选择弹窗
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */
class FloorAttr(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity),
    View.OnClickListener {

    //已选属性TextView
    private lateinit var selectedAttrText: TextView

    override fun initView() {
        selectedAttrText = requireViewById(R.id.selectedAttrText)
        rootView?.setOnClickListener(this)
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorAttrData) {
            return
        }

        //商详组件会将选择的属性与数量拼接成一整个String，这里只负责显示就好
        selectedAttrText.text = floorData.selectedAttrText
    }

    override fun onDestroy() {
    }

    override fun onClick(v: View?) {
        //点击楼层将事件传递给商详组件触发显示属性选择弹窗
        eventViewModel?.clickEvent?.value = FloorClickData(floorEntity.floorType, data = null)
    }


}