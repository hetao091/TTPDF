package tt.reducto.pdf.viewer

import android.content.Context
import android.util.AttributeSet
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import androidx.core.view.ViewCompat


/**
 * ……。
 *
 * <p>......。</p>
 * <ul><li></li></ul>
 * <br>
 * <strong>Time</strong>&nbsp;&nbsp;&nbsp;&nbsp;2022/3/4 10:19<br>
 * <strong>CopyRight</strong>&nbsp;&nbsp;&nbsp;&nbsp;2021, tt.reducto<br>
 *
 * @version  : 1.0.0
 * @author   : hetao
 */
class TTVerticalSeekBarWrapper:FrameLayout {
    
    constructor(context: Context) : super(context)
    constructor(context: Context, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int) : super(context, attrs, defStyleAttr)
    constructor(context: Context, attrs: AttributeSet?, defStyleAttr: Int, defStyleRes: Int) : super(
        context,
        attrs,
        defStyleAttr,
        defStyleRes
    )

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        if (useViewRotation()) {
            onSizeChangedUseViewRotation(w, h, oldw, oldh)
        } else {
            onSizeChangedTraditionalRotation(w, h, oldw, oldh)
        }
    }

    private fun onSizeChangedTraditionalRotation(w: Int, h: Int, oldw: Int, oldh: Int) {
        val seekBar: TTVerticalSeekBar? = getChildSeekBar()
        if (seekBar != null) {
            val hPadding = paddingLeft + paddingRight
            val vPadding = paddingTop + paddingBottom
            val lp = seekBar.layoutParams as LayoutParams
            lp.width = ViewGroup.LayoutParams.WRAP_CONTENT
            lp.height = 0.coerceAtLeast(h - vPadding)
            seekBar.layoutParams = lp
            seekBar.measure(MeasureSpec.UNSPECIFIED, MeasureSpec.UNSPECIFIED)
            val seekBarMeasuredWidth: Int = seekBar.measuredWidth
            seekBar.measure(
                MeasureSpec.makeMeasureSpec(0.coerceAtLeast(w - hPadding), MeasureSpec.AT_MOST),
                MeasureSpec.makeMeasureSpec(0.coerceAtLeast(h - vPadding), MeasureSpec.EXACTLY)
            )
            lp.gravity = Gravity.TOP or Gravity.LEFT
            lp.leftMargin = (0.coerceAtLeast(w - hPadding) - seekBarMeasuredWidth) / 2
            seekBar.layoutParams = lp
        }
        super.onSizeChanged(w, h, oldw, oldh)
    }

    private fun onSizeChangedUseViewRotation(w: Int, h: Int, oldw: Int, oldh: Int) {
        val seekBar: TTVerticalSeekBar? = getChildSeekBar()
        if (seekBar != null) {
            val hPadding = paddingLeft + paddingRight
            val vPadding = paddingTop + paddingBottom
            seekBar.measure(
                MeasureSpec.makeMeasureSpec(0.coerceAtLeast(h - vPadding), MeasureSpec.EXACTLY),
                MeasureSpec.makeMeasureSpec(0.coerceAtLeast(w - hPadding), MeasureSpec.AT_MOST)
            )
        }
        applyViewRotation(w, h)
        super.onSizeChanged(w, h, oldw, oldh)
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val seekBar: TTVerticalSeekBar? = getChildSeekBar()
        val widthMode = MeasureSpec.getMode(widthMeasureSpec)
        val heightMode = MeasureSpec.getMode(heightMeasureSpec)
        val widthSize = MeasureSpec.getSize(widthMeasureSpec)
        val heightSize = MeasureSpec.getSize(heightMeasureSpec)
        if (seekBar != null && widthMode != MeasureSpec.EXACTLY) {
            val seekBarWidth: Int
            val seekBarHeight: Int
            val hPadding = paddingLeft + paddingRight
            val vPadding = paddingTop + paddingBottom
            val innerContentWidthMeasureSpec = MeasureSpec.makeMeasureSpec(0.coerceAtLeast(widthSize - hPadding), widthMode)
            val innerContentHeightMeasureSpec = MeasureSpec.makeMeasureSpec(0.coerceAtLeast(heightSize - vPadding), heightMode)
            if (useViewRotation()) {
                seekBar.measure(innerContentHeightMeasureSpec, innerContentWidthMeasureSpec)
                seekBarWidth = seekBar.measuredHeight
                seekBarHeight = seekBar.measuredWidth
            } else {
                seekBar.measure(innerContentWidthMeasureSpec, innerContentHeightMeasureSpec)
                seekBarWidth = seekBar.measuredWidth
                seekBarHeight = seekBar.measuredHeight
            }
            val measuredWidth = resolveSizeAndState(seekBarWidth + hPadding, widthMeasureSpec, 0)
            val measuredHeight = resolveSizeAndState(seekBarHeight + vPadding, heightMeasureSpec, 0)
            setMeasuredDimension(measuredWidth, measuredHeight)
        } else {
            super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        }
    }

    /*package*/
    fun applyViewRotation() {
        applyViewRotation(width, height)
    }

    private fun applyViewRotation(w: Int, h: Int) {
        val seekBar: TTVerticalSeekBar? = getChildSeekBar()
        if (seekBar != null) {
            val isLTR = ViewCompat.getLayoutDirection(this) == ViewCompat.LAYOUT_DIRECTION_LTR
            val rotationAngle: Int = seekBar.getRotationAngle()
            val seekBarMeasuredWidth: Int = seekBar.measuredWidth
            val seekBarMeasuredHeight: Int = seekBar.measuredHeight
            val hPadding = paddingLeft + paddingRight
            val vPadding = paddingTop + paddingBottom
            val hOffset = (0.coerceAtLeast(w - hPadding) - seekBarMeasuredHeight) * 0.5f
            val lp: ViewGroup.LayoutParams = seekBar.layoutParams
            lp.width = 0.coerceAtLeast(h - vPadding)
            lp.height = ViewGroup.LayoutParams.WRAP_CONTENT
            seekBar.layoutParams = lp
            seekBar.pivotX = (if (isLTR) 0 else 0.coerceAtLeast(h - vPadding)).toFloat()
            seekBar.pivotY = 0F
            when (rotationAngle) {
                TTVerticalSeekBar.ROTATION_ANGLE_CW_90 -> {
                    seekBar.rotation = 90F
                    if (isLTR) {
                        seekBar.translationX = seekBarMeasuredHeight + hOffset
                        seekBar.translationY = 0F
                    } else {
                        seekBar.translationX = -hOffset
                        seekBar.translationY = seekBarMeasuredWidth.toFloat()
                    }
                }
                TTVerticalSeekBar.ROTATION_ANGLE_CW_270 -> {
                    seekBar.rotation = 270F
                    if (isLTR) {
                        seekBar.translationX = hOffset
                        seekBar.translationY = seekBarMeasuredWidth.toFloat()
                    } else {
                        seekBar.translationX = -(seekBarMeasuredHeight + hOffset)
                        seekBar.translationY = 0F
                    }
                }
            }
        }
    }

    private fun getChildSeekBar(): TTVerticalSeekBar? {
        val child: View? = if (childCount > 0) getChildAt(0) else null
        return if (child is TTVerticalSeekBar) child as TTVerticalSeekBar? else null
    }

    private fun useViewRotation(): Boolean {
        val seekBar: TTVerticalSeekBar? = getChildSeekBar()
        return if (seekBar != null) {
            seekBar.useViewRotation()
        } else {
            false
        }
    }
}