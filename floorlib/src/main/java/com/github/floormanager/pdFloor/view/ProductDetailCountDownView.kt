package com.github.floormanager.pdFloor.view

import android.content.Context
import android.os.Handler
import android.text.format.DateUtils
import android.util.AttributeSet
import android.view.LayoutInflater
import android.view.View
import android.widget.FrameLayout
import android.widget.TextView
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.OnLifecycleEvent
import com.github.floormanager.floorlib.R

/**
 * 倒计时视图组件
 * 目前只有砍价楼层使用，后续秒杀等新促销可能需要，支持生命周期管理
 * 
 * @param context 上下文环境
 * @param attrs 属性集合
 * @param defStyleAttr 默认样式属性
 */
class ProductDetailCountDownView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : FrameLayout(context, attrs, defStyleAttr), Runnable, LifecycleObserver {
    //显示小时的TextView
    private val mHourContainer: TextView

    //显示分钟的TextView
    private val mMinContainer: TextView

    //显示秒的TextView
    private val mSecondContainer: TextView

    //显示天的TextView
    private val mDayContainer: TextView

    //显示天的分隔线的TextView
    private val mDayDivider: TextView

    //当前倒计时时间
    private var mElapsedTimeInMillisecond = 0L

    //当前设备系统时间
    private var mLastDeviceTime: Long = 0

    //倒计时改变的监听
    private var mListener: OnCountDownListener? = null

    /**
     * 渲染倒计时的内容
     * 
     * @param day 倒计时中的日期
     * @param hour 倒计时中的小时
     * @param minute 倒计时中的分钟
     * @param second 倒计时中的秒
     */
    private fun innerUpdateCountDown(day: Int, hour: Int, minute: Int, second: Int) {
        try {
            //日期不是必须显示的，如果没有则隐藏日期的内容与分隔
            if (day > 0) {
                mDayDivider.visibility = View.VISIBLE
                mDayContainer.visibility = View.VISIBLE
                mDayContainer.text = day.toString()
            } else {
                mDayDivider.visibility = View.GONE
                mDayContainer.visibility = View.GONE
            }

            mHourContainer.text = String.format("%02d", hour)
            mMinContainer.text = String.format("%02d", minute)
            mSecondContainer.text = String.format("%02d", second)
        } catch (e: IllegalArgumentException) {
            //格式化出现异常则使用兜底内容来显示
            mDayDivider.visibility = View.GONE
            mDayContainer.visibility = View.GONE
            mHourContainer.text = "00"
            mMinContainer.text = "00"
            mSecondContainer.text = "00"
        }
    }

    /**
     * 开始倒计时
     *
     * @param elapsedTime 倒计时的总时间，毫秒
     */
    fun startCountDown(elapsedTime: Long) {
        mElapsedTimeInMillisecond = elapsedTime
        if (0 < mElapsedTimeInMillisecond) {
            mHandler.removeCallbacks(this)
            mLastDeviceTime = System.currentTimeMillis()
            mHandler.post(this)
        }
    }

    /**
     * 停止倒计时，onDetachedFromWindows 时会自动调用
     */
    fun stopCountDown() {
        if (mHandler != null) {
            mHandler.removeCallbacks(this)
        }
    }

    override fun run() {
        //倒计时时间戳的单位为毫秒，需要转换成秒
        var elapsedTimeInSecond =
            if (0 < mElapsedTimeInMillisecond) mElapsedTimeInMillisecond / DateUtils.SECOND_IN_MILLIS else 0

        var hours: Long = 0
        var minutes: Long = 0
        var seconds: Long = 0
        var day: Long = 0

        //从时间戳对所有需要渲染的时间进行转换
        if (elapsedTimeInSecond >= 3600) {
            hours = elapsedTimeInSecond / 3600
            elapsedTimeInSecond -= hours * 3600
        }
        if (elapsedTimeInSecond >= 60) {
            minutes = elapsedTimeInSecond / 60
            elapsedTimeInSecond -= minutes * 60
        }
        if (hours >= 24) {
            day = hours / 24
            hours -= day * 24
        }

        seconds = elapsedTimeInSecond
        innerUpdateCountDown(day.toInt(), hours.toInt(), minutes.toInt(), seconds.toInt())

        //通过计算两次触发Runnable时系统时间的差值来计算时间过了多久
        val deviceTime = System.currentTimeMillis()
        mElapsedTimeInMillisecond -= deviceTime - mLastDeviceTime
        mLastDeviceTime = deviceTime

        if (0 < mElapsedTimeInMillisecond) {
            //未结束
            if (mHandler != null) {
                mHandler.postDelayed(this, DateUtils.SECOND_IN_MILLIS)
            }
            if (null != mListener) {
                mListener!!.onTimeElapse(mElapsedTimeInMillisecond)
            }
        } else {
            //计时结束, 回调
            mElapsedTimeInMillisecond = 0
            if (null != mListener) {
                mListener!!.onTimeOut()
            }
        }
    }

    @OnLifecycleEvent(Lifecycle.Event.ON_DESTROY)
    fun onDestroy() {
        //宿主Activity销毁时结束倒计时避免内存泄露
        stopCountDown()
    }

    /**
     * 设置倒计时完成时的回调， 设置为 null 时会注销 listener
     *
     * @param listener 倒计时结束时回调
     */
    fun addOnCountListener(listener: OnCountDownListener?) {
        mListener = listener
    }

    interface OnCountDownListener {
        /**
         * 每次隔一秒回调, 时间变化
         *
         * @param currentTime 当前剩余时间
         */
        fun onTimeElapse(currentTime: Long)

        /**
         * 倒计时结束回调
         */
        fun onTimeOut()
    }

    companion object {
        private val mHandler = Handler()
    }

    init {
        val root =
            LayoutInflater.from(context).inflate(R.layout.pd_base_floor_count_down_view, this, true)
        mHourContainer = root.findViewById(R.id.hour_container)
        mMinContainer = root.findViewById(R.id.minute_container)
        mSecondContainer = root.findViewById(R.id.second_container)
        mDayContainer = root.findViewById(R.id.day_container)
        mDayDivider = root.findViewById(R.id.day_divider)
        if (getContext() is LifecycleOwner) {
            (getContext() as LifecycleOwner).lifecycle.addObserver(this)
        }
    }
}