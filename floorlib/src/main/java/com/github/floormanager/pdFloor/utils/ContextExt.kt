package com.github.floormanager.pdFloor.utils

import android.content.Context
import android.graphics.Color
import android.util.TypedValue
import androidx.annotation.AttrRes

/**
 * 获取主题属性对应的资源ID
 */
fun Context.getAttrResourceId(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return if (typedValue.resourceId != 0) {
        typedValue.resourceId
    } else {
        // 如果没有找到资源，返回默认的占位图
        android.R.drawable.ic_menu_gallery
    }
}

/**
 * 获取主题属性对应的颜色值
 */
fun Context.getAttrColor(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return if (typedValue.resourceId != 0) {
        getColor(typedValue.resourceId)
    } else if (typedValue.data != 0) {
        typedValue.data
    } else {
        Color.TRANSPARENT
    }
}

/**
 * 获取主题属性对应的尺寸值
 */
fun Context.getAttrDimension(@AttrRes attrRes: Int): Float {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return if (typedValue.resourceId != 0) {
        resources.getDimension(typedValue.resourceId)
    } else {
        TypedValue.complexToDimension(typedValue.data, resources.displayMetrics)
    }
}

/**
 * 获取主题属性对应的尺寸值（像素）
 */
fun Context.getAttrDimensionPixelSize(@AttrRes attrRes: Int): Int {
    val typedValue = TypedValue()
    theme.resolveAttribute(attrRes, typedValue, true)
    return if (typedValue.resourceId != 0) {
        resources.getDimensionPixelSize(typedValue.resourceId)
    } else {
        TypedValue.complexToDimensionPixelSize(typedValue.data, resources.displayMetrics)
    }
} 