package com.github.floormanager.utils

import android.widget.TextView
import java.math.BigDecimal

/**
 * 价格格式化工具类 - ISV 风格
 */
object IsvPriceFormatUtils {
    
    /**
     * 设置价格显示样式1
     */
    fun setTextWithPriceStyle1(textView: TextView, price: BigDecimal, showSymbol: Boolean = true) {
        textView.text = if (showSymbol) {
            "¥${PriceFormatUtils.formatToNumber(price, false)}"
        } else {
            PriceFormatUtils.formatToNumber(price, false)
        }
    }
    
    /**
     * 设置价格显示样式2
     */
    fun setTextWithPriceStyle2(textView: TextView, price: BigDecimal, showSymbol: Boolean = true) {
        textView.text = if (showSymbol) {
            "¥${PriceFormatUtils.formatToNumber(price, false)}"
        } else {
            PriceFormatUtils.formatToNumber(price, false)
        }
    }
    
    /**
     * 设置价格显示
     */
    fun setPriceShow(textView: TextView, price: BigDecimal) {
        textView.text = "¥${PriceFormatUtils.formatToNumber(price, false)}"
    }
} 