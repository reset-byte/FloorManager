package com.github.floormanager.utils

import java.math.BigDecimal
import java.text.DecimalFormat

/**
 * 价格格式化工具类
 */
object PriceFormatUtils {
    
    private val decimalFormat = DecimalFormat("#0.00")
    
    /**
     * 格式化价格为字符串
     */
    fun formatPrice(price: BigDecimal?): String {
        return if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            "¥${decimalFormat.format(price)}"
        } else {
            ""
        }
    }
    
    /**
     * 格式化价格为字符串（不带货币符号）
     */
    fun formatPriceValue(price: BigDecimal?): String {
        return if (price != null && price.compareTo(BigDecimal.ZERO) > 0) {
            decimalFormat.format(price)
        } else {
            "0.00"
        }
    }
    
    /**
     * 格式化价格为数字字符串
     */
    fun formatToNumber(price: BigDecimal, withSymbol: Boolean = false): String {
        val formattedPrice = decimalFormat.format(price)
        return if (withSymbol) {
            "¥$formattedPrice"
        } else {
            formattedPrice
        }
    }
    
    /**
     * 字符串转换为 BigDecimal
     */
    fun string2BigDecimal(priceStr: String?): BigDecimal? {
        return try {
            if (priceStr.isNullOrEmpty()) {
                null
            } else {
                BigDecimal(priceStr)
            }
        } catch (e: NumberFormatException) {
            null
        }
    }
} 