package com.github.floormanager.pdFloor.floor

import android.content.Context
import android.view.View
import android.widget.TextView
import com.github.floormanager.floorManager.entity.FloorClickData
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorSlashData
import com.github.floormanager.pdFloor.view.ProductDetailCountDownView
import com.github.floormanager.utils.dp2px

/**
 * 砍价楼层实现类
 * 负责渲染砍价活动信息，包括砍价提示、成功人数和倒计时功能
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */
class FloorSlash(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity) {

    //砍价提示文案的TextView
    private lateinit var mainHintContent: TextView

    //砍价成功人数的TextView
    private lateinit var successNum: TextView

    //砍价倒计时的View
    private lateinit var countDownView: ProductDetailCountDownView

    //砍价倒计时提示文案的TextView
    private lateinit var countDownHint: TextView

    override fun initView() {
        mainHintContent = requireViewById(R.id.mainHintContent)
        successNum = requireViewById(R.id.successNum)
        countDownView = requireViewById(R.id.countDownView)
        countDownHint = requireViewById(R.id.countDownHint)
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorSlashData) {
            return
        }

        //渲染砍价提示文案
        mainHintContent.text = floorData.content

        //渲染成功人数，根据是否显示成功人数来调整提示文案的间距
        if (!floorData.successNum.isNullOrEmpty()) {
            mainHintContent.setPadding(0, 0, 0, 0)
            successNum.visibility = View.VISIBLE
            successNum.text =
                context.getString(
                    R.string.pd_base_floor_slash_success_num_hint,
                    floorData.successNum
                )
        } else {
            mainHintContent.setPadding(0, 5f.dp2px, 0, 0)
            successNum.visibility = View.GONE
        }

        //渲染倒计时和提示文案，只有数值存在且大于0才显示
        if (floorData.countDown != null && floorData.countDown > 0) {
            countDownView.visibility = View.VISIBLE
            countDownHint.visibility = View.VISIBLE
            countDownView.startCountDown(floorData.countDown)
        } else {
            countDownView.visibility = View.GONE
            countDownHint.visibility = View.GONE
        }
    }

    override fun onDestroy() {

    }

}