package com.coyni.pos.app.utils.CustomeTextView

import android.graphics.Color
import android.util.AttributeSet
import android.widget.TextView
import com.coyni.pos.app.R
import java.util.concurrent.Executors
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

/**
 * Used by TextView
 */
class GradientManager {
    private val textView: TextView

    /**
     * The colors used in gradient
     */
    private lateinit var colors: IntArray

    /**
     * The number of colors possibly displayed in a same time
     */
    private var simultaneousColors = 0

    /**
     * The angle of the gradient
     */
    private var angle = 0

    /**
     * The time separating the apparition of two colors in millisecond
     */
    private var speed = 0

    /**
     * How many gradients are calculated by second
     */
    private var maxFPS = 0

    /**
     * Time interval between each draw (millis)
     */
    private var drawTimeInterval = 0

    /**
     * Current running gradient runnable
     */
    private var runnable: GradientRunnable? = null

    /**
     * Current scheduled gradient future running
     */
    private var scheduledFuture: ScheduledFuture<*>? = null

    /**
     * The draw-gradient uptime
     */
    private var currentGradientProgress: Long = 0

    constructor(textView: TextView) {
        this.textView = textView
        initDefaultValues()
    }

    constructor(textView: TextView, attrs: AttributeSet) {
        this.textView = textView
        initFromAttrsValues(attrs)
    }

    /**
     * Initialize the variables of this object
     *
     * @param attrs The attributes of the TextView
     */
    private fun initFromAttrsValues(attrs: AttributeSet) {
        // Initialize an array containing id of attributes we want to have
        val set = intArrayOf(
            R.attr.colors,
            R.attr.simultaneousColors,
            R.attr.angle,
            R.attr.speed,
            R.attr.maxFPS
        )
        val typedArray = textView.context.obtainStyledAttributes(attrs, set)

        // Get colors array id
        val colorsArrayId = typedArray.getResourceId(0, ATTR_NOT_FOUND)

        // Get colors
        colors = if (colorsArrayId != ATTR_NOT_FOUND) {
            textView.resources.getIntArray(colorsArrayId)
        } else {
            textView.resources.getIntArray(R.array.default_gradient_colors)
        }

        // Get others attributes
        simultaneousColors = typedArray.getInt(1, ATTR_NOT_FOUND)
        angle = typedArray.getInt(2, ATTR_NOT_FOUND)
        speed = typedArray.getInt(3, ATTR_NOT_FOUND)
        maxFPS = typedArray.getInt(4, ATTR_NOT_FOUND)
        if (simultaneousColors == ATTR_NOT_FOUND) {
            simultaneousColors = 2
        }
        if (angle == ATTR_NOT_FOUND) {
            angle = 45
        }
        if (speed == ATTR_NOT_FOUND) {
            speed = 1000
        }
        if (maxFPS == ATTR_NOT_FOUND) {
            maxFPS = 24
        }
        drawTimeInterval = 1000 / maxFPS
        typedArray.recycle()
    }

    /**
     * Initialize the variables of this object with default values
     */
    private fun initDefaultValues() {
        colors = intArrayOf(Color.BLUE, Color.RED, Color.GREEN)
        simultaneousColors = 2
        angle = 45
        speed = 2000
        maxFPS = 24
        drawTimeInterval = 1000 / maxFPS
    }

    fun stopGradient() {
        synchronized(this) {
            if (scheduledFuture != null) {
                // Save gradient state (future possible restart)
                currentGradientProgress = runnable!!.currentProgress
                scheduledFuture!!.cancel(true)
                runnable = null
                scheduledFuture = null
            }
        }
    }

    /**
     * Create a thread which applies the gradient if not exist
     */
    fun startGradient() {
        synchronized(this) {
            if (scheduledFuture != null) {
                return
            }
            val wf = textView.width
            val hf = textView.height
            if (wf > 0 && hf > 0) {
                runnable = GradientRunnable(textView, colors, simultaneousColors, angle, speed)

                // Apply saved progress if there is
                runnable!!.currentProgress = currentGradientProgress
                val scheduledExecutor = Executors.newSingleThreadScheduledExecutor()
                scheduledFuture = scheduledExecutor.scheduleAtFixedRate(
                    runnable,
                    0,
                    drawTimeInterval.toLong(),
                    TimeUnit.MILLISECONDS
                )
            }
        }
    }

    companion object {
        private const val ATTR_NOT_FOUND = Int.MIN_VALUE
    }
}