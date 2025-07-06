package com.github.floormanager.pdFloor.entity

import android.text.TextUtils

/**
 * 头图楼层数据类
 * 包含商品头图列表和多媒体内容信息
 * 
 * @param imageList 商品图片url的列表
 * @param mediaData 多媒体数据
 */
class FloorTopImageData(
    val imageList: ArrayList<String?>,
    val mediaData: MediaItemData? = null
) {

    /**
     * 多媒体项数据类
     * 包含视频或VR相关的媒体资源信息
     * 
     * @param isVR 是否支持VR
     * @param mediaUrl 媒体资源url
     * @param duration 媒体时长
     * @param coverUrl 封面图url
     */
    class MediaItemData(
        val isVR: Boolean,
        val mediaUrl: String? = null,
        val duration: Int? = 0,
        val coverUrl: String? = null
    )

    /**
     * 是否包含视频数据
     * 
     * @return true为包含
     */
    fun hasVideoData(): Boolean {
        return mediaData != null && !mediaData.isVR && !TextUtils.isEmpty(mediaData.mediaUrl)
    }

    /**
     * 是否包含VR数据
     * 
     * @return true为包含
     */
    fun hasVRData(): Boolean {
        return mediaData != null && mediaData.isVR && !TextUtils.isEmpty(mediaData.mediaUrl)
    }

    /**
     * 获取头图页数
     * 
     * @return 页数
     */
    fun getCount(): Int {
        return imageList.size + (if (hasVideoData()) 1 else 0)
    }

}