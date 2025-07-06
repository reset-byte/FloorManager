package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.floormanager.floorManager.entity.FloorClickData
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorAddressData

/**
 * 地址楼层实现类
 * 负责显示用户选择的收货地址信息，点击可触发地址选择弹窗
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */

class FloorAddress(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity),
    View.OnClickListener {

    //选择地址的TextView
    private lateinit var addressText: TextView

    override fun initView() {
        addressText = requireViewById(R.id.selectedAddressText)
        addressText.setOnClickListener(this)
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorAddressData) {
            return
        }

        //渲染地址信息
        addressText.text = floorData.selectedAddressText
    }

    override fun onDestroy() {

    }

    override fun onClick(v: View?) {
        //点击地址View将事件传递给商详组件触发选择地址的弹窗
        eventViewModel?.clickEvent?.value = FloorClickData(floorEntity.floorType, data = null)
    }
}