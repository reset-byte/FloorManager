package com.github.floormanager.pdFloor.entity

/**
 * 视频楼层数据类
 * 包含视频播放相关的媒体信息
 * 
 * @param videoUrl 视频url
 * @param videoCoverUrl 视频封面图url
 * @param videoDuration 视频时长（单位：秒）
 */

class FloorVideoData(
    val videoUrl: String?,
    val videoCoverUrl: String?,
    val videoDuration: Int? = 0
)