package com.github.floormanager.utils

import android.graphics.*
import com.bumptech.glide.load.engine.bitmap_recycle.BitmapPool
import com.bumptech.glide.load.resource.bitmap.BitmapTransformation
import java.security.MessageDigest

/**
 * 支持指定每个角圆角半径的图片变换
 */
class GranularRoundedCorners(
    private val topLeft: Int,
    private val topRight: Int,
    private val bottomLeft: Int,
    private val bottomRight: Int
) : BitmapTransformation() {

    companion object {
        private const val ID = "com.github.floormanager.utils.GranularRoundedCorners"
        private val ID_BYTES = ID.toByteArray(Charsets.UTF_8)
    }

    override fun transform(
        pool: BitmapPool,
        toTransform: Bitmap,
        outWidth: Int,
        outHeight: Int
    ): Bitmap {
        val result = pool.get(outWidth, outHeight, Bitmap.Config.ARGB_8888)
        result.setHasAlpha(true)

        val canvas = Canvas(result)
        val paint = Paint(Paint.ANTI_ALIAS_FLAG)
        paint.shader = BitmapShader(toTransform, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP)

        val path = Path()
        val rectF = RectF(0f, 0f, outWidth.toFloat(), outHeight.toFloat())
        
        // 创建圆角矩形路径
        val radii = floatArrayOf(
            topLeft.toFloat(), topLeft.toFloat(),       // 左上角 x,y 半径
            topRight.toFloat(), topRight.toFloat(),     // 右上角 x,y 半径
            bottomRight.toFloat(), bottomRight.toFloat(), // 右下角 x,y 半径
            bottomLeft.toFloat(), bottomLeft.toFloat()  // 左下角 x,y 半径
        )
        
        path.addRoundRect(rectF, radii, Path.Direction.CW)
        canvas.drawPath(path, paint)

        return result
    }

    override fun updateDiskCacheKey(messageDigest: MessageDigest) {
        messageDigest.update(ID_BYTES)
        messageDigest.update(topLeft.toString().toByteArray())
        messageDigest.update(topRight.toString().toByteArray())
        messageDigest.update(bottomLeft.toString().toByteArray())
        messageDigest.update(bottomRight.toString().toByteArray())
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is GranularRoundedCorners) return false
        return topLeft == other.topLeft &&
                topRight == other.topRight &&
                bottomLeft == other.bottomLeft &&
                bottomRight == other.bottomRight
    }

    override fun hashCode(): Int {
        var result = ID.hashCode()
        result = 31 * result + topLeft
        result = 31 * result + topRight
        result = 31 * result + bottomLeft
        result = 31 * result + bottomRight
        return result
    }
} 