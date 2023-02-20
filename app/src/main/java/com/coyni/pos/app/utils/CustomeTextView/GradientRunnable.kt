package com.coyni.pos.app.utils.CustomeTextView

import android.animation.ArgbEvaluator
import android.graphics.LinearGradient
import android.graphics.Point
import android.graphics.Shader
import android.os.SystemClock
import android.util.Log
import android.widget.TextView
import java.util.*

class GradientRunnable internal constructor(
    private val textView: TextView,
    /**
     * The colors used in gradient
     */
    private val colors: IntArray, simultaneousColors: Int, angle: Int,
    /**
     * The time separating the apparition of two colors in millisecond
     */
    private val speed: Int
) : Runnable {
    /**
     * The angle of the gradient
     */
    private val angle = 0

    /**
     * The current progress of the gradient
     */
    var currentProgress: Long = 0

    /**
     * The last draw time
     */
    private var lastTime: Long = 0

    /**
     * Ordered array of drawn colors
     */
    private val currentColors: IntArray

    /**
     * Gradient positions
     */
    private val gradientsPositions: Array<Point?>

    /**
     * Current gradient color index
     */
    private var currentGradient = 0

    init {

//        final int wf = 50;
        val wf = textView.width
        val hf = textView.height
        Log.e("widthe", "$wf $hf")
        gradientsPositions = getGradientsPoints(wf, hf)
        currentColors = Arrays.copyOf(colors, simultaneousColors)
    }

    override fun run() {
        val currentTime = SystemClock.uptimeMillis()
        val delta = currentTime - lastTime
        currentProgress += delta
        var totalPercentage = currentProgress / speed.toFloat()
        totalPercentage = if (totalPercentage > 1) 1F else totalPercentage
        for (colorIndex in currentColors.indices) {
            currentColors[colorIndex] = ArgbEvaluator().evaluate(
                totalPercentage,
                colors[(currentGradient + colorIndex) % colors.size],
                colors[(currentGradient + (colorIndex + 1)) % colors.size]
            ) as Int
        }
        if (totalPercentage == 1f) {
            currentProgress = 0
            currentGradient = (currentGradient + 1) % colors.size
        }
        val shader: Shader = LinearGradient(
            gradientsPositions[1]!!.x.toFloat(),
            gradientsPositions[1]!!.y.toFloat(),
            gradientsPositions[0]!!.x.toFloat(),
            gradientsPositions[0]!!.y.toFloat(),
            currentColors,
            null,
            Shader.TileMode.CLAMP
        )
        textView.paint.shader = shader
        textView.postInvalidate()
        lastTime = currentTime
    }

    /**
     * Get the points used to create the Linear Gradient from the angle
     *
     * @param width  the textview width
     * @param height the textview height
     * @return An array containing the two points
     */
    private fun getGradientsPoints(width: Int, height: Int): Array<Point?> {
        // Angle from degree to radian
        val angleRadian = Math.toRadians(angle.toDouble())

        // We want a circle radius > Max dist ( circle center, rectangle point )

        // Get the circle center
        val circleCenter = Point(width / 2, height / 2)

        // Create a segment passing through the center of the circle
        val secantP1 = Point(
            (circleCenter.x - width * Math.cos(
                angleRadian
            )).toInt(), (circleCenter.y - width * Math.sin(angleRadian)).toInt()
        )
        val secantP2 = Point(
            (circleCenter.x + width * Math.cos(
                angleRadian
            )).toInt(), (circleCenter.y + width * Math.sin(angleRadian)).toInt()
        )
        val intersectPoints = arrayOfNulls<Point>(2)

        // Top segment of rectangle
        val topSegmentP1 = Point(0, 0)
        val topSegmentP2 = Point(width, 0)
        intersectPoints[0] =
            MathsUtils.getIntersectionPoint(secantP1, secantP2, topSegmentP1, topSegmentP2)
        if (intersectPoints[0] == null) {
            // Left segment
            val leftSegmentP1 = Point(0, 0)
            val leftSegmentP2 = Point(0, height)
            intersectPoints[0] =
                MathsUtils.getIntersectionPoint(secantP1, secantP2, leftSegmentP1, leftSegmentP2)
        }

        // Bottom segment
        val bottomSegmentP1 = Point(0, height)
        val bottomSegmentP2 = Point(width, height)
        intersectPoints[1] =
            MathsUtils.getIntersectionPoint(secantP1, secantP2, bottomSegmentP1, bottomSegmentP2)
        if (intersectPoints[1] == null) {
            // Right segment
            val rightSegmentP1 = Point(width, 0)
            val rightSegmentP2 = Point(width, height)
            intersectPoints[1] =
                MathsUtils.getIntersectionPoint(secantP1, secantP2, rightSegmentP1, rightSegmentP2)
        }
        return intersectPoints
    }
}