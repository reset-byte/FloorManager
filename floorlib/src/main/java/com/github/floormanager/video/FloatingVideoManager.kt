package com.github.floormanager.video

import android.content.Context
import android.net.Uri
import android.view.ViewGroup
import android.widget.FrameLayout
import com.google.android.exoplayer2.ExoPlayer
import com.google.android.exoplayer2.MediaItem
import com.google.android.exoplayer2.Player
import com.google.android.exoplayer2.ui.PlayerView

/**
 * 浮动视频播放管理器
 * 基于ExoPlayer实现
 */
class FloatingVideoManager(
    private val rootContainer: ViewGroup,
    private val normalContainer: FrameLayout,
    private val videoUrl: String
) {
    
    private var exoPlayer: ExoPlayer? = null
    private var playerView: PlayerView? = null
    private var smallVideoContainer: FrameLayout? = null
    private var context: Context = normalContainer.context
    
    // 播放状态
    var isPlaying: Boolean = false
        private set
    
    // 当前播放模式
    private var currentMode = PlayMode.NORMAL
    
    enum class PlayMode {
        NORMAL,     // 正常播放模式
        SMALL,      // 小窗播放模式
        FULLSCREEN  // 全屏播放模式
    }
    
    init {
        initializePlayer()
    }
    
    /**
     * 初始化播放器
     */
    private fun initializePlayer() {
        // 创建ExoPlayer实例
        exoPlayer = ExoPlayer.Builder(context).build()
        
        // 创建PlayerView
        playerView = PlayerView(context).apply {
            player = exoPlayer
            useController = true
            setShowBuffering(PlayerView.SHOW_BUFFERING_WHEN_PLAYING)
        }
        
        // 添加播放状态监听
        exoPlayer?.addListener(object : Player.Listener {
            override fun onPlaybackStateChanged(playbackState: Int) {
                when (playbackState) {
                    Player.STATE_BUFFERING -> {
                        // 缓冲中
                    }
                    Player.STATE_READY -> {
                        // 准备就绪
                    }
                    Player.STATE_ENDED -> {
                        // 播放结束
                        isPlaying = false
                    }
                }
            }
            
            override fun onIsPlayingChanged(playing: Boolean) {
                isPlaying = playing
            }
        })
        
        // 准备媒体资源
        val mediaItem = MediaItem.fromUri(Uri.parse(videoUrl))
        exoPlayer?.setMediaItem(mediaItem)
        exoPlayer?.prepare()
    }
    
    /**
     * 开始播放
     */
    fun startPlay() {
        if (playerView?.parent != null) {
            (playerView?.parent as ViewGroup).removeView(playerView)
        }
        
        // 添加到正常容器中
        normalContainer.addView(playerView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))
        
        currentMode = PlayMode.NORMAL
        exoPlayer?.play()
        isPlaying = true
    }
    
    /**
     * 暂停播放
     */
    fun pause() {
        exoPlayer?.pause()
        isPlaying = false
    }
    
    /**
     * 恢复播放
     */
    fun resume() {
        exoPlayer?.play()
        isPlaying = true
    }
    
    /**
     * 停止播放
     */
    fun stop() {
        exoPlayer?.stop()
        isPlaying = false
    }
    
    /**
     * 退出播放
     */
    fun exitPlay() {
        stop()
        if (playerView?.parent != null) {
            (playerView?.parent as ViewGroup).removeView(playerView)
        }
        removeSmallVideoContainer()
        currentMode = PlayMode.NORMAL
    }
    
    /**
     * 显示小窗播放
     */
    fun showSmallVideo(topMargin: Int) {
        if (currentMode == PlayMode.SMALL) return
        
        if (playerView?.parent != null) {
            (playerView?.parent as ViewGroup).removeView(playerView)
        }
        
        // 创建小窗容器
        if (smallVideoContainer == null) {
            smallVideoContainer = FrameLayout(context)
            val params = FrameLayout.LayoutParams(
                (context.resources.displayMetrics.widthPixels * 0.4).toInt(),
                (context.resources.displayMetrics.widthPixels * 0.4 * 9 / 16).toInt()
            )
            params.setMargins(
                context.resources.displayMetrics.widthPixels - params.width - 20,
                topMargin,
                0,
                0
            )
            smallVideoContainer!!.layoutParams = params
            rootContainer.addView(smallVideoContainer)
        }
        
        // 添加到小窗容器
        smallVideoContainer!!.addView(playerView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))
        
        currentMode = PlayMode.SMALL
    }
    
    /**
     * 显示正常模式
     */
    fun showNormalMode() {
        if (currentMode == PlayMode.NORMAL) return
        
        if (playerView?.parent != null) {
            (playerView?.parent as ViewGroup).removeView(playerView)
        }
        
        // 移除小窗容器
        removeSmallVideoContainer()
        
        // 添加到正常容器
        normalContainer.addView(playerView, FrameLayout.LayoutParams(
            FrameLayout.LayoutParams.MATCH_PARENT,
            FrameLayout.LayoutParams.MATCH_PARENT
        ))
        
        currentMode = PlayMode.NORMAL
    }
    
    /**
     * 移除小窗容器
     */
    private fun removeSmallVideoContainer() {
        smallVideoContainer?.let {
            if (it.parent != null) {
                rootContainer.removeView(it)
            }
        }
        smallVideoContainer = null
    }
    
    /**
     * 销毁播放器
     */
    fun onDestroy() {
        exoPlayer?.release()
        exoPlayer = null
        
        if (playerView?.parent != null) {
            (playerView?.parent as ViewGroup).removeView(playerView)
        }
        playerView = null
        
        removeSmallVideoContainer()
    }
    
    /**
     * 获取播放位置
     */
    fun getCurrentPosition(): Long {
        return exoPlayer?.currentPosition ?: 0
    }
    
    /**
     * 设置播放位置
     */
    fun seekTo(position: Long) {
        exoPlayer?.seekTo(position)
    }
    
    /**
     * 获取视频总时长
     */
    fun getDuration(): Long {
        return exoPlayer?.duration ?: 0
    }
} 