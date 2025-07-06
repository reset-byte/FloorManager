package com.github.floormanager.floorManager.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.github.floormanager.floorManager.entity.FloorClickData

/**
 * 楼层事件处理ViewModel
 * 负责处理楼层内的各种事件，包括点击事件和其他交互事件
 */

class FloorEventViewModel : ViewModel() {

    //楼层点击事件的LiveData，通过对该LiveData进行赋值与观测就可以处理楼层内触发的点击事件
    val clickEvent = MutableLiveData<FloorClickData>()

}