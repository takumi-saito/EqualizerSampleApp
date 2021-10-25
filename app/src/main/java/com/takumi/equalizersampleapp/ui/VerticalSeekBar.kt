package com.takumi.equalizersampleapp.ui

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Rect
import android.graphics.drawable.Drawable;
import android.util.AttributeSet
import android.util.Log
import android.view.MotionEvent
import com.takumi.equalizersampleapp.R

class VerticalSeekBar : androidx.appcompat.widget.AppCompatSeekBar {
    /**
     * Use this instead of the isFromUser returned in the [android.widget.SeekBar.OnSeekBarChangeListener.onProgressChanged]
     * @return whether the touche was initiated from the user
     */
    var isFromUser = false
        private set
    private var onlySlideOnThumb = false

    /**
     * Custom thumb to ensure the thumb is drawn on the vertical seek bar. Compatible with Android M.
     * see http://stackoverflow.com/questions/33112277/android-6-0-marshmallow-stops-showing-vertical-seekbar-thumb/36094973
     *
     * @param customThumb
     */
    var customThumb: Drawable? = null
        set(customThumb) {
            field = customThumb
            invalidate()
        }
    protected var initialCustomThumb: Drawable? = null
    private var wasDownOnThumb = false

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context,
        attrs,
        defStyle) {
        init(context, attrs)
    }

    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init(context, attrs)
    }

    private fun init(context: Context, attrs: AttributeSet) {
        val customAttr: TypedArray = context.getTheme().obtainStyledAttributes(
            attrs,
            R.styleable.VerticalSeekBar,
            0, 0)
        val onlyThumb = customAttr.getBoolean(R.styleable.VerticalSeekBar_onlyThumb, false)
        initialCustomThumb = customAttr.getDrawable(R.styleable.VerticalSeekBar_customThumb)
        isOnlyThumb = onlyThumb
        customThumb = initialCustomThumb
        thumb = resources.getDrawable(R.color.transparent)
        setPadding(0, 0, 0, 0)
    }

    override fun onSizeChanged(w: Int, h: Int, oldw: Int, oldh: Int) {
        super.onSizeChanged(h, w, oldh, oldw)
    }

    @Synchronized
    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(heightMeasureSpec, widthMeasureSpec)
        setMeasuredDimension(measuredHeight, measuredWidth)
    }

    override fun onDraw(c: Canvas) {
        c.rotate(-90F)
        c.translate((-height).toFloat(), 0F)
        super.onDraw(c)
        drawThumb(c)
    }

    private fun drawThumb(canvas: Canvas) {
        val customThumb = customThumb
        if (customThumb != null) {
            var available = height - paddingTop - paddingBottom
            val thumbHeight = customThumb.intrinsicHeight
            available -= thumbHeight
            // The extra space for the thumb to move on the track
            available += thumbOffset * 2
            val left = (scale * available + 0.5f).toInt()
            val top: Int = 0
            val bottom: Int = customThumb.intrinsicWidth
            val right = left + customThumb.intrinsicHeight
            customThumb.setBounds(left, top, right, bottom)
            Log.d(this::class.java.simpleName, "drawThumb:$left, $top, $right, $bottom")
            canvas.save()
            canvas.translate(0F, ((width - customThumb.intrinsicWidth) / 2).toFloat())
            customThumb.draw(canvas)
            canvas.restore()
        }
    }

    private val scale: Float
        private get() {
            val max = max
            return if (max > 0) progress / max.toFloat() else 0F
        }

    /**
     * Sets the progress for the SeekBar programmatically.
     *
     * @param progress
     */
    override fun setProgress(progress: Int) {
        setProgressFromUser(progress, false)
    }

    /**
     * Sets whether the progress was initiated from the user or programmatically.
     *
     * @param progress
     * @param fromUser
     */
    fun setProgressFromUser(progress: Int, fromUser: Boolean) {
        isFromUser = fromUser
        super.setProgress(progress)
        onSizeChanged(width, height, 0, 0)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        if (!isEnabled) {
            return false
        }
        when (event.action) {
            MotionEvent.ACTION_DOWN -> if (onlySlideOnThumb) {
                wasDownOnThumb = if (isOnThumb(event)) {
                    updateProgress(event.y)
                    true
                } else {
                    return false
                }
            } else {
                wasDownOnThumb = true
                updateProgress(event.y)
            }
            MotionEvent.ACTION_MOVE -> if (wasDownOnThumb) {
                updateProgress(event.y)
            }
            MotionEvent.ACTION_UP, MotionEvent.ACTION_CANCEL -> wasDownOnThumb = false
        }
        return true
    }

    private fun updateProgress(y: Float) {
        val i = max - (max * y / height).toInt()
        setProgressFromUser(i, true)
    }

    private fun isOnThumb(event: MotionEvent): Boolean {
        val thumb = customThumb
        val thumbBounds: Rect = thumb!!.bounds
        return (inBetween(event.x, thumbBounds.top.toFloat(), thumbBounds.bottom.toFloat())
            && inBetween(event.y, (height - thumbBounds.left).toFloat(),
            (height - thumbBounds.right).toFloat()))
    }

    /**
     * Whether the slider should only move when the touch was initiated on the thumb.
     *
     * @param onlySlideOnThumb
     */
    var isOnlyThumb: Boolean
        get() = onlySlideOnThumb
        set(onlySlideOnThumb) {
            this.onlySlideOnThumb = onlySlideOnThumb
            invalidate()
        }

    companion object {
        private fun inBetween(loc: Float, bound1: Float, bound2: Float): Boolean {
            return loc in bound1..bound2 || loc in bound2..bound1
        }
    }
}