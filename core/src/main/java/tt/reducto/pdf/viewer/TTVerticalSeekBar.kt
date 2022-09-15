package tt.reducto.pdf.viewer


import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.MotionEvent
import android.widget.ProgressBar
import androidx.appcompat.widget.AppCompatSeekBar
import androidx.core.view.ViewCompat
import tt.reducto.pdf.R
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method


/**
 * 垂直样式 seekbar。
 *
 * <p>......。</p>
 * <ul><li></li></ul>
 * <br>
 * <strong>Time</strong>&nbsp;&nbsp;&nbsp;&nbsp;2022/3/3 18:04<br>
 * <strong>CopyRight</strong>&nbsp;&nbsp;&nbsp;&nbsp;2021, tt.reducto<br>
 *
 * @version  : 1.0.0
 * @author   : hetao
 */
class TTVerticalSeekBar : AppCompatSeekBar {


    companion object {
        const val ROTATION_ANGLE_CW_90 = 90
        const val ROTATION_ANGLE_CW_270 = 270
    }


    private var mIsDragging = false
    private var mThumb_: Drawable? = null
    private var mMethodSetProgressFromUser: Method? = null
    private var mRotationAngle = ROTATION_ANGLE_CW_90


    constructor(context: Context) : super(context) {
        initialize(context, null, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs) {

        initialize(context, attrs, 0, 0)
    }

    constructor(context: Context, attrs: AttributeSet?, defStyle: Int) : super(context, attrs, defStyle) {

        initialize(context, attrs, defStyle, 0)
    }

    private fun initialize(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) {
        ViewCompat.setLayoutDirection(this, ViewCompat.LAYOUT_DIRECTION_LTR)
        if (attrs != null) {
            val a = context.obtainStyledAttributes(attrs, R.styleable.TTVerticalSeekBar, defStyleAttr, defStyleRes)
            val rotationAngle = a.getInteger(R.styleable.TTVerticalSeekBar_seekBarRotation, 0)
            if (isValidRotationAngle(rotationAngle)) {
                mRotationAngle = rotationAngle
            }
            a.recycle()
        }
    }

    override fun setThumb(thumb: Drawable?) {
        mThumb_ = thumb
        super.setThumb(thumb)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return if (useViewRotation()) {
            onTouchEventUseViewRotation(event)
        } else {
            onTouchEventTraditionalRotation(event)
        }
    }

    private fun onTouchEventTraditionalRotation(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> {
                isPressed = true
                onStartTrackingTouch()
                trackTouchEvent(event)
                attemptClaimDrag(true)
                invalidate()
            }
            MotionEvent.ACTION_MOVE -> if (mIsDragging) {
                trackTouchEvent(event)
            }
            MotionEvent.ACTION_UP -> {
                if (mIsDragging) {
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    isPressed = false
                } else {
                    // Touch up when we never crossed the touch slop threshold
                    // should
                    // be interpreted as a tap-seek to that location.
                    onStartTrackingTouch()
                    trackTouchEvent(event)
                    onStopTrackingTouch()
                    attemptClaimDrag(false)
                }
                // ProgressBar doesn't know to repaint the thumb drawable
                // in its inactive state when the touch stops (because the
                // value has not apparently changed)
                invalidate()
            }
            MotionEvent.ACTION_CANCEL -> {
                if (mIsDragging) {
                    onStopTrackingTouch()
                    isPressed = false
                }
                invalidate() // see above explanation
            }
        }
        return true
    }

    private fun onTouchEventUseViewRotation(event: MotionEvent): Boolean {
        val handled = super.onTouchEvent(event)
        if (handled) {
            val action = event.action
            when (action) {
                MotionEvent.ACTION_DOWN -> attemptClaimDrag(true)
                MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> attemptClaimDrag(false)
            }
        }
        return handled
    }

    private fun trackTouchEvent(event: MotionEvent) {
        val paddingLeft = super.getPaddingLeft()
        val paddingRight = super.getPaddingRight()
        val height = height
        val available = height - paddingLeft - paddingRight
        val y = event.y.toInt()
        val scale: Float
        var value = 0f
        when (mRotationAngle) {
            ROTATION_ANGLE_CW_90 -> value = (y - paddingLeft).toFloat()
            ROTATION_ANGLE_CW_270 -> value = (height - paddingLeft - y).toFloat()
        }
        scale = if (value < 0 || available == 0) {
            0.0f
        } else if (value > available) {
            1.0f
        } else {
            value / available.toFloat()
        }
        val max = max
        val progress = scale * max
        _setProgressFromUser(progress.toInt(), true)
    }

    /**
     * Tries to claim the user's drag motion, and requests disallowing any
     * ancestors from stealing events in the drag.
     */
    private fun attemptClaimDrag(active: Boolean) {
        val parent = parent
        parent?.requestDisallowInterceptTouchEvent(active)
    }

    /**
     * This is called when the user has started touching this widget.
     */
    private fun onStartTrackingTouch() {
        mIsDragging = true
    }

    /**
     * This is called when the user either releases his touch or the touch is
     * canceled.
     */
    private fun onStopTrackingTouch() {
        mIsDragging = false
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (isEnabled) {
            val handled: Boolean
            var direction = 0
            when (keyCode) {
                KeyEvent.KEYCODE_DPAD_DOWN -> {
                    direction = if (mRotationAngle == ROTATION_ANGLE_CW_90) 1 else -1
                    handled = true
                }
                KeyEvent.KEYCODE_DPAD_UP -> {
                    direction = if (mRotationAngle == ROTATION_ANGLE_CW_270) 1 else -1
                    handled = true
                }
                KeyEvent.KEYCODE_DPAD_LEFT, KeyEvent.KEYCODE_DPAD_RIGHT ->                     // move view focus to previous/next view
                    return false
                else -> handled = false
            }
            if (handled) {
                val keyProgressIncrement = keyProgressIncrement
                var progress = progress
                progress += direction * keyProgressIncrement
                if (progress >= 0 && progress <= max) {
                    _setProgressFromUser(progress, true)
                }
                return true
            }
        }
        return super.onKeyDown(keyCode, event)
    }

    @Synchronized
    override fun setProgress(progress: Int) {
        super.setProgress(progress)
        if (!useViewRotation()) {
            refreshThumb()
        }
    }

    @Synchronized
    private fun _setProgressFromUser(progress: Int, fromUser: Boolean) {
        if (mMethodSetProgressFromUser == null) {
            try {
                val m: Method
                m = ProgressBar::class.java.getDeclaredMethod(
                    "setProgress",
                    Int::class.javaPrimitiveType,
                    Boolean::class.javaPrimitiveType
                )
                m.isAccessible = true
                mMethodSetProgressFromUser = m
            } catch (e: NoSuchMethodException) {
            }
        }
        if (mMethodSetProgressFromUser != null) {
            try {
                mMethodSetProgressFromUser!!.invoke(this, progress, fromUser)
            } catch (e: IllegalArgumentException) {
            } catch (e: IllegalAccessException) {
            } catch (e: InvocationTargetException) {
            }
        } else {
            super.setProgress(progress)
        }
        refreshThumb()
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        if (useViewRotation()) {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        } else {
            super.onMeasure(heightMeasureSpec, widthMeasureSpec)
            val lp = layoutParams
            if (isInEditMode && lp != null && lp.height >= 0) {
                setMeasuredDimension(super.getMeasuredHeight(), lp.height)
            } else {
                setMeasuredDimension(super.getMeasuredHeight(), super.getMeasuredWidth())
            }
        }
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (useViewRotation()) {
            super.onSizeChanged(w, h, oldw, oldh)
        } else {
            super.onSizeChanged(h, w, oldh, oldw)
        }
    }

    @Synchronized
    override fun onDraw(canvas: Canvas) {
        if (!useViewRotation()) {
            when (mRotationAngle) {
                ROTATION_ANGLE_CW_90 -> {
                    canvas.rotate(90f)
                    canvas.translate(0f, -super.getWidth().toFloat())
                }
                ROTATION_ANGLE_CW_270 -> {
                    canvas.rotate(-90f)
                    canvas.translate(-super.getHeight().toFloat(), 0f)
                }
            }
        }
        super.onDraw(canvas)
    }

    fun getRotationAngle(): Int {
        return mRotationAngle
    }

    fun setRotationAngle(angle: Int) {
        require(isValidRotationAngle(angle)) { "Invalid angle specified :$angle" }
        if (mRotationAngle == angle) {
            return
        }
        mRotationAngle = angle
        if (useViewRotation()) {
            val wrapperTT: TTVerticalSeekBarWrapper? = getWrapper()
            if (wrapperTT != null) {
                wrapperTT.applyViewRotation()
            }
        } else {
            requestLayout()
        }
    }

    // refresh thumb position
    private fun refreshThumb() {
        onSizeChanged(super.getWidth(), super.getHeight(), 0, 0)
    }

    /*package*/
    fun useViewRotation(): Boolean {
        return !isInEditMode
    }

    private fun getWrapper(): TTVerticalSeekBarWrapper? {
        val parent = parent
        return if (parent is TTVerticalSeekBarWrapper) {
            parent as TTVerticalSeekBarWrapper
        } else {
            null
        }
    }

    private fun isValidRotationAngle(angle: Int): Boolean {
        return angle == ROTATION_ANGLE_CW_90 || angle == ROTATION_ANGLE_CW_270
    }
}