package com.github.floormanager.pdFloor.view

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.VelocityTracker
import android.view.ViewConfiguration
import android.webkit.WebView
import android.widget.Scroller
import androidx.core.view.NestedScrollingChild2
import androidx.core.view.NestedScrollingChildHelper
import androidx.core.view.ViewCompat
import kotlin.math.abs
import kotlin.math.max
import kotlin.math.min

/**
 * 实现嵌套滑动, 与NestedScrollingGroup 配合使用
 *
 * @author cdsongjian
 * @date 2020-05-21 13:47
 */
class NestedScrollingWebView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyle: Int = 0
) : WebView(context, attrs, defStyle), NestedScrollingChild2, Runnable {
    private var mLastY = 0
    private val mTouchSlop: Int
    private val mScrollConsumed = IntArray(2)
    private val mMaxFlingVelocity: Int
    private val mMinFlingVelocity: Int
    private val mScroller: Scroller
    private var mVelocityTracker: VelocityTracker? = null
    private val mChildHelper: NestedScrollingChildHelper
    private var hasDestroyed = false
    private val mNestedOffsets = IntArray(2)
    private val mScrollOffset = IntArray(2)
    private var mDownY = 0
    private var mIsBeingDragged = false
    override fun onTouchEvent(event: MotionEvent): Boolean {
        var result = false
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
                run {
                    mLastY = event.rawY.toInt()
                    mDownY = mLastY
                }
                stopNestedScroll(ViewCompat.TYPE_NON_TOUCH)
                startNestedScroll(ViewCompat.SCROLL_AXIS_VERTICAL, ViewCompat.TYPE_TOUCH)
                mIsBeingDragged = false
                result = super.onTouchEvent(event)
            }
            MotionEvent.ACTION_MOVE -> {
                val y = event.rawY.toInt()
                val dy = y - mLastY
                mLastY = y
                if (abs((mLastY - mDownY + 0.5f).toInt()) > mTouchSlop && !mIsBeingDragged) {
                    mIsBeingDragged = true
                    val ev = MotionEvent.obtain(event)
                    ev.action = MotionEvent.ACTION_CANCEL
                    super.onTouchEvent(ev)
                    ev.recycle()
                }
                if (!mIsBeingDragged) {
                    result = super.onTouchEvent(event)
                }
                result = true
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
            MotionEvent.ACTION_CANCEL, MotionEvent.ACTION_UP -> {
                if (!mIsBeingDragged) {
                    result = super.onTouchEvent(event)
                } else {
                    val ev = MotionEvent.obtain(event)
                    ev.action = MotionEvent.ACTION_CANCEL
                    result = super.onTouchEvent(ev)
                    ev.recycle()
                }
                if (mVelocityTracker != null) {
                    mVelocityTracker!!.addMovement(event)
                    mVelocityTracker!!.computeCurrentVelocity(1000, mMaxFlingVelocity.toFloat())
                    eventAddedToVelocityTracker = true
                    fling((-mVelocityTracker!!.yVelocity).toInt())
                }
                resetTouch()
            }
            else -> {
            }
        }
        if (!eventAddedToVelocityTracker) {
            mVelocityTracker!!.addMovement(motionEvent)
        }
        motionEvent.recycle()
        return result
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

    private fun postOnAnimation() {
        if (hasDestroyed) {
            return
        }
        removeCallbacks(this)
        ViewCompat.postOnAnimation(this, this)
    }

    override fun run() {
        if (hasDestroyed) {
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
        hasDestroyed = true
        removeCallbacks(this)
    }
    /**
     * Perform inflation from XML and apply a class-specific base style from a
     * theme attribute. This constructor of View allows subclasses to use their
     * own base style when they are inflating. For example, a Button class's
     * constructor would call this version of the super class constructor and
     * supply `R.attr.buttonStyle` for <var>defStyleAttr</var>; this
     * allows the theme's button style to modify all of the base view attributes
     * (in particular its background) as well as the Button class's attributes.
     *
     * @param context  The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs    The attributes of the XML tag that is inflating the view.
     * @param defStyle An attribute in the current theme that contains a
     * reference to a style resource that supplies default values for
     * the view. Can be 0 to not look for defaults.
     */
    /**
     * Constructor that is called when inflating a view from XML. This is called
     * when a view is being constructed from an XML file, supplying attributes
     * that were specified in the XML file. This version uses a default style of
     * 0, so the only attribute values applied are those in the Context's Theme
     * and the given AttributeSet.
     *
     *
     *
     * The method onFinishInflate() will be called after all children have been
     * added.
     *
     * @param context The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     * @param attrs   The attributes of the XML tag that is inflating the view.
     */
    /**
     * Simple constructor to use when creating a view from code.
     *
     * @param context The Context the view is running in, through which it can
     * access the current theme, resources, etc.
     */
    init {
        overScrollMode = OVER_SCROLL_NEVER
        mScroller = Scroller(getContext())
        mChildHelper = NestedScrollingChildHelper(this)
        isNestedScrollingEnabled = true
        val configuration = ViewConfiguration.get(getContext())
        mMinFlingVelocity = configuration.scaledMinimumFlingVelocity
        mMaxFlingVelocity = configuration.scaledMaximumFlingVelocity
        mTouchSlop = configuration.scaledTouchSlop
    }
}