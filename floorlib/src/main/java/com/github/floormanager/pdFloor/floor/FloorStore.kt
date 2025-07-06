package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.floormanager.floorManager.entity.FloorClickData
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorStoreData

/**
 * 门店楼层实现类
 * 负责渲染门店信息，包括门店名称和地址，点击可跳转至门店页面
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */
class FloorStore(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity),
    View.OnClickListener {

    //门店名称的TextView
    private lateinit var storeName: TextView
    //门店地址的TextView
    private lateinit var storeAddress: TextView

    override fun initView() {
        storeName = requireViewById<TextView>(R.id.storeName)
        storeAddress = requireViewById<TextView>(R.id.storeAddress)
        rootView?.setOnClickListener(this)
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorStoreData) {
            return
        }

        //渲染门店名称与地址
        storeName.text = floorData.storeName
        storeAddress.text = floorData.storeAddress
    }

    override fun onDestroy() {

    }

    override fun onClick(v: View?) {
        //点击店铺名称View将事件传递给商详组件触发跳转到店铺页面
        eventViewModel?.clickEvent?.value = FloorClickData(floorEntity.floorType, data = null)
    }
}