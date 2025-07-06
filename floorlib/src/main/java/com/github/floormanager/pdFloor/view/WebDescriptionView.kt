package com.github.floormanager.pdFloor.view

import android.content.Context
import android.os.Build
import android.text.TextUtils
import android.util.AttributeSet
import android.view.*
import android.webkit.WebView
import android.webkit.WebViewClient
import android.widget.RelativeLayout
import android.widget.Scroller
import androidx.core.view.NestedScrollingChild2
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import com.github.floormanager.view.ErrorTipView
import com.github.floormanager.floorlib.R
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 封装WebView和错误兜底页
 * 展示WebView由WebView完成嵌套滑动
 * 展示错误兜底页是由NestedScrollingDescription 完成嵌套滑动
 *
 *
 * 嵌套滑动, 与NestedScrollingGroup 配合使用
 *
 * @author cdsongjian
 * @date 2020-05-21 13:47
 */
class WebDescriptionView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : RelativeLayout(context, attrs, defStyle), NestedScrollingChild2, Runnable {
    private var mLastY = 0
    private val mScrollConsumed = IntArray(2)
    private val mMaxFlingVelocity: Int
    private val mMinFlingVelocity: Int
    private val mScroller: Scroller = Scroller(getContext())
    private var mVelocityTracker: VelocityTracker? = null
    private val mChildHelper: NestedScrollingChildHelper = NestedScrollingChildHelper(this)
    private var mWebView: NestedScrollingWebView? = null

    private var mHasDestroyed = false

    /**
     * 获取相关view, 并进行初始操作
     */
    private fun initView(context: Context) {
        val inflater = LayoutInflater.from(context)
        if (inflater != null) {
            try {
                inflater.inflate(R.layout.pd_base_floor_web_container_view_description, this, true)
            } catch (ex: Exception) {
                showNoWebViewTip(inflater)
                return
            }
            mWebView = findViewById(R.id.web_view)
            mWebView?.overScrollMode = OVER_SCROLL_NEVER
            mWebView?.settings?.javaScriptEnabled = true
            mWebView?.settings?.savePassword = false
            // 关闭file域
            mWebView?.settings?.allowFileAccess = false
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                mWebView?.settings?.allowFileAccessFromFileURLs = false
                mWebView?.settings?.allowUniversalAccessFromFileURLs = false
            }

            mWebView?.webViewClient = object : WebViewClient() {

                override fun onPageFinished(view: WebView?, url: String?) {
                    super.onPageFinished(view, url)

                    val w = MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)
                    val h = MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED)

                    mWebView?.measure(w, h)
                    mWebView?.visibility = View.VISIBLE
                }

            }

        }
    }

    private val mNestedOffsets = IntArray(2)
    private val mScrollOffset = IntArray(2)
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var eventAddedToVelocityTracker = false
        val motionEvent = MotionEvent.obtain(event)
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain()
        }
        val action = event.actionMasked
        if (action == 0) {
            mNestedOffsets[1] = 0
            mNestedOffsets[0] = mNestedOffsets[1]
        }
        motionEvent.offsetLocation(mNestedOffsets[0].toFloat(), mNestedOffsets[1].toFloat())
        when (action) {
            MotionEvent.ACTION_DOWN -> {
                mLastY = event.rawY.toInt()
                stopNestedScroll(ViewCompat.TYPE_NON_TOUCH)
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
            }
            MotionEvent.ACTION_MOVE -> {
                val y = event.rawY.toInt()
                val dy = y - mLastY
                mLastY = y
                if (dispatchNestedPreScroll(
                        0,
                        -dy,
                        mScrollConsumed,
                        mScrollOffset,
                        ViewCompat.TYPE_TOUCH
                    )
                ) {
                    //dy -=  mScrollConsumed[1];
                    motionEvent.offsetLocation(
                        mScrollOffset[0].toFloat(), mScrollOffset[1].toFloat()
                    )
                    var offsets = mNestedOffsets
                    offsets[0] += mScrollOffset[0]
                    offsets = mNestedOffsets
                    offsets[1] += mScrollOffset[1]
                }
            }
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> if (mVelocityTracker != null) {
                mVelocityTracker!!.addMovement(event)
                mVelocityTracker!!.computeCurrentVelocity(1000, mMaxFlingVelocity.toFloat())
                eventAddedToVelocityTracker = true
                fling((-mVelocityTracker!!.yVelocity).toInt())
                resetTouch()
            }
            else -> {
            }
        }
        if (!eventAddedToVelocityTracker) {
            mVelocityTracker!!.addMovement(motionEvent)
        }
        motionEvent.recycle()
        return true
    }

    private fun resetTouch() {
        if (mVelocityTracker != null) {
            mVelocityTracker!!.clear()
        }
        stopNestedScroll(ViewCompat.TYPE_TOUCH)
    }

    private var mLastFlingX = 0
    private var mLastFlingY = 0
    private fun fling(velocityY: Int) {
        var velocityY = velocityY
        if (abs(velocityY) < mMinFlingVelocity) {
            velocityY = 0
        }
        if (velocityY != 0) {
            if (!dispatchNestedPreFling(0.toFloat(), velocityY.toFloat())) {
                dispatchNestedFling(0.toFloat(), velocityY.toFloat(), true)
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_NON_TOUCH)
                velocityY = max(-mMaxFlingVelocity, min(velocityY, mMaxFlingVelocity))
                mLastFlingY = 0
                mLastFlingX = mLastFlingY
                mScroller.fling(
                    0,
                    0,
                    0,
                    velocityY,
                    Int.MIN_VALUE,
                    Int.MAX_VALUE,
                    Int.MIN_VALUE,
                    Int.MAX_VALUE
                )
                postOnAnimation()
            }
        }
    }

    fun postOnAnimation() {
        if (mHasDestroyed) {
            return
        }
        removeCallbacks(this)
        ViewCompat.postOnAnimation(this, this)
    }

    override fun run() {
        if (mHasDestroyed) {
            return
        }
        if (mScroller.computeScrollOffset()) {
            val x = mScroller.currX
            val y = mScroller.currY
            val dx = x - mLastFlingX
            val dy = y - mLastFlingY
            mLastFlingX = x
            mLastFlingY = y
            dispatchNestedPreScroll(dx, dy, mScrollConsumed, null, ViewCompat.TYPE_NON_TOUCH)
            if (mScroller.isFinished) {
                stopNestedScroll(ViewCompat.TYPE_NON_TOUCH)
            } else {
                postOnAnimation()
            }
        }
    }

    private fun showNoWebViewTip(inflater: LayoutInflater) {
        inflater.inflate(R.layout.pd_base_floor_web_container_view_no_webview_tip, this, true)
        val tipView: ErrorTipView = findViewById(R.id.no_webview_tip)
        tipView.setRetryVisibility(false)
    }

    fun loadHtml(content: String?) {
        if (mWebView != null) {
            if (!TextUtils.isEmpty(content)) {
                val realContent = content?.replace("src=\"//", "src=\"https://")
                val realContent2 = realContent?.replace("http://", "https://")

                val css =
                    "<style>p{margin:0 auto}img{width:100%!important;height:auto!important;}body{height:100%;width:100%;margin:0;padding:0;touch-action:none;touch-action:pan-y;word-break:normal;word-wrap:break-word;}body{-webkit-user-select:none;-moz-user-select:none;-ms-user-select:none;user-select:none;margin:0auto!important;max-width:720px;overflow:hidden;overflow-y:auto;font-size:14px;color:#000100;}*{-webkit-tap-highlight-color:rgba(0,0,0,0);-ms-tap-highlight-color:rgba(0,0,0,0);-moz-tap-highlight-color:rgba(0,0,0,0);tap-highlight-color:rgba(0,0,0,0);outline:none;}</style>"
                val html =
                    "<html><header><metaname=\"viewport\"content=\"width=device-width,initial-scale=1.0,maximum-scale=1.0,user-scalable=0,shrink-to-fit=no,viewport-fit=cover\">$css</header><body>$realContent2</body></html>"
                mWebView!!.loadDataWithBaseURL("https://", html, "text/html", "UTF-8", null)
            }
        }
    }

    override fun canScrollVertically(direction: Int): Boolean {
        return if (mWebView != null && mWebView!!.visibility == VISIBLE) {
            mWebView!!.canScrollVertically(direction)
        } else {
            false
        }
    }

    override fun scrollBy(x: Int, y: Int) {
        if (mWebView != null && mWebView!!.visibility == VISIBLE) {
            mWebView!!.scrollBy(x, y)
        } else {
            super.scrollBy(x, y)
        }
    }

    override fun scrollTo(x: Int, y: Int) {
        if (mWebView != null && mWebView!!.visibility == VISIBLE) {
            mWebView!!.scrollTo(x, y)
        } else {
            super.scrollTo(x, y)
        }
    }

    val scrollVertically: Int
        get() = if (mWebView != null && mWebView!!.visibility == VISIBLE) {
            mWebView!!.scrollY
        } else {
            super.getScrollY()
        }

    /****** NestedScrollingChild BEGIN  */
    override fun setNestedScrollingEnabled(enabled: Boolean) {
        mChildHelper.isNestedScrollingEnabled = enabled
    }

    override fun isNestedScrollingEnabled(): Boolean {
        return mChildHelper.isNestedScrollingEnabled
    }

    override fun startNestedScroll(axes: Int, type: Int): Boolean {
        return mChildHelper.startNestedScroll(axes, type)
    }

    override fun stopNestedScroll(type: Int) {
        if (mWebView != null) {
            mWebView!!.stopNestedScroll(type)
        }
        mChildHelper.stopNestedScroll(type)
    }

    override fun hasNestedScrollingParent(type: Int): Boolean {
        return mChildHelper.hasNestedScrollingParent(type)
    }

    override fun dispatchNestedScroll(
        dxConsumed: Int,
        dyConsumed: Int,
        dxUnconsumed: Int,
        dyUnconsumed: Int,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mChildHelper.dispatchNestedScroll(
            dxConsumed,
            dyConsumed,
            dxUnconsumed,
            dyUnconsumed,
            offsetInWindow,
            type
        )
    }

    override fun dispatchNestedPreScroll(
        dx: Int,
        dy: Int,
        consumed: IntArray?,
        offsetInWindow: IntArray?,
        type: Int
    ): Boolean {
        return mChildHelper.dispatchNestedPreScroll(dx, dy, consumed, offsetInWindow, type)
    }

    override fun dispatchNestedPreFling(velocityX: Float, velocityY: Float): Boolean {
        return mChildHelper.dispatchNestedPreFling(velocityX, velocityY)
    }

    override fun dispatchNestedFling(
        velocityX: Float,
        velocityY: Float,
        consumed: Boolean
    ): Boolean {
        return mChildHelper.dispatchNestedFling(velocityX, velocityY, consumed)
    }

    fun onDestroy() {
        mHasDestroyed = true
        removeCallbacks(this)
        if (mWebView != null) {
            try {
                mWebView!!.onDestroy()
                val parent = mWebView!!.parent
                if (parent != null) {
                    (parent as ViewGroup).removeView(mWebView)
                }
                mWebView!!.stopLoading()
                mWebView!!.settings.javaScriptEnabled = false
                mWebView!!.clearHistory()
                mWebView!!.removeAllViews()
                mWebView!!.destroy()
            } catch (ex: Throwable) {
            }
            mWebView = null
        }
    }

    init {
        isNestedScrollingEnabled = true
        val configuration = ViewConfiguration.get(getContext())
        mMinFlingVelocity = configuration.scaledMinimumFlingVelocity
        mMaxFlingVelocity = configuration.scaledMaximumFlingVelocity
        initView(context)
    }
}