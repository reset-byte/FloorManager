package com.github.floormanager.pdFloor.floor

import android.app.Activity
import android.content.Context
import android.text.TextUtils
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.github.floormanager.utils.ImageLoader
import com.github.floormanager.video.FloatingVideoManager
import com.github.floormanager.floorManager.entity.FloorEntity
import com.github.floormanager.floorManager.floor.OptimizedBaseFloor
import com.github.floormanager.floorlib.R
import com.github.floormanager.pdFloor.entity.FloorVideoData
import com.github.floormanager.pdFloor.utils.getAttrResourceId
import java.util.*
import com.github.floormanager.floorManager.entity.FloorClickData

/**
 * 视频播放楼层实现类
 * 负责渲染单独的视频播放功能，支持视频播放控制和生命周期管理
 * 
 * @param context 上下文环境
 * @param floorEntity 楼层实体对象
 */

class FloorVideo(context: Context, floorEntity: FloorEntity) : OptimizedBaseFloor(context, floorEntity),
    View.OnClickListener {

    // 视频区域布局
    private lateinit var videoLayout: LinearLayout
    // 播放按钮
    private lateinit var playBtn: View
    // 视频封面
    private lateinit var coverView: ImageView
    // 视频时长
    private lateinit var durationText: TextView
    // 视频内容布局
    private lateinit var videoContentLayout: FrameLayout
    // 视频URL
    private var videoUrl: String? = null

    //视频播放管理类
    private var videoManager: FloatingVideoManager? = null

    override fun initView() {
        videoLayout = requireViewById(R.id.videoLayout)
        playBtn = requireViewById(R.id.playBtn)
        coverView = requireViewById(R.id.coverView)
        durationText = requireViewById(R.id.durationText)
        videoContentLayout = requireViewById(R.id.videoContentLayout)

        //视频播放区域的比例设置为16比9
        val layoutParams = videoContentLayout.layoutParams
        layoutParams.height = (context.resources.displayMetrics.widthPixels * 9 / 16f).toInt()
        videoContentLayout.layoutParams = layoutParams

        playBtn.setOnClickListener(this)
    }

    /**
     * 当楼层不可见时重置播放状态
     */
    override fun onDetachFromWindow() {
        if (videoManager != null) {
            videoManager!!.exitPlay()
        }
    }

    override fun renderData(floorData: Any?) {
        //如果传入的业务数据并不是该楼层对应的业务数据类，则不往下执行渲染
        if (floorData !is FloorVideoData) {
            return
        }

        // 保存视频URL
        videoUrl = floorData.videoUrl

        //对楼层播放管理类进行初始化
        if (videoManager == null) {
            videoManager = FloatingVideoManager(
                ((context as Activity).findViewById<View>(Window.ID_ANDROID_CONTENT) as ViewGroup),
                videoContentLayout,
                floorData.videoUrl!!
            )
        }

        //渲染视频播放时长
        if (floorData.videoDuration != null) {
            setPlayBtnText(floorData.videoDuration)
        }

        //渲染视频封面图
        if (!TextUtils.isEmpty(floorData.videoCoverUrl)) {
            setImageUrl(floorData.videoCoverUrl!!)
        }
    }


    /**
     * 设置播放按钮上文字和时长，按时分秒格式
     * @param time 单位为秒的播放时长
     *
     */
    private fun setPlayBtnText(time: Int) {
        val hour = time / 3600
        val minute = time % 3600 / 60
        val second = time % 60
        val builder = StringBuilder()
        if (hour > 0) {
            builder.append(hour)
            builder.append("'")
        }
        builder.append(if (minute > 0) getFormatTime(minute) else "00")
        builder.append("'")
        builder.append(if (second > 0) getFormatTime(second) else "00")
        builder.append("''")
        durationText.text = builder.toString()
    }

    /**
     * 将时间数字进行格式化
     * 
     * @param time 时间
     * @return 格式化时间
     */
    private fun getFormatTime(time: Int): String {
        return String.format(Locale.getDefault(), "%02d", time)
    }

    /**
     * 设置视频封面图
     *
     * @param url 封面图的url
     */
    private fun setImageUrl(url: String) {
        ImageLoader.loadImage(
            coverView,
            url,
            context.getAttrResourceId(R.attr.isv_image_placeholder),
            context.getAttrResourceId(R.attr.isv_image_placeholder)
        )
    }

    override fun onDestroy() {
        //页面被销毁时需要清理视频数据
        if (videoManager != null) {
            videoManager!!.onDestroy()
        }
    }

    override fun onPause() {
        //宿主Activity切换至非前台状态则重置播放状态
        if (videoManager != null) {
            videoManager!!.exitPlay()
        }
    }

    override fun onClick(v: View?) {
        //点击播放按钮将事件传递给商详组件触发播放视频
        eventViewModel?.clickEvent?.value = FloorClickData(floorEntity.floorType, data = videoUrl)
    }

}