package com.example.taskcustomview

import android.content.Context
import android.graphics.*
import android.util.AttributeSet
import android.view.View
import java.util.*
import kotlin.math.cos
import kotlin.math.sin


class Clock : View {

    constructor(context: Context?) : super(context)
    constructor(context: Context?, attrs: AttributeSet?) : super(context, attrs)
    constructor(context: Context?, attrs: AttributeSet?, defStyleAttr: Int) : super(
        context, attrs, defStyleAttr
    )

    //    private val customTimezone: TimeZone = timez
    private var clockHeight = 0
    private var clockWidth = 0
    private var clockPadding = 0
    private var clockFontSize = 0
    private var clockRadius = 0
    private var bareMinimum = 0
    private val rect = Rect()
    private var clockDigitsInterval = 0
    private var paint: Paint? = null
    private var clockInitialized = false
    private val digits = (1..12).toList()
    private var clockHandTruncation = 0
    private var clockHourHandTruncation = 0

    enum class HandTypes {
        HOUR, MINUTE, SECOND
    }

    private fun init() {
        clockHeight = height
        clockWidth = width
        bareMinimum = Math.min(clockHeight, clockWidth)
        clockPadding = clockDigitsInterval + bareMinimum
        clockRadius = bareMinimum / 2 - clockPadding
        clockFontSize = bareMinimum / 8
        clockHandTruncation = bareMinimum / 30
        clockHourHandTruncation = bareMinimum / 100
        paint = Paint()
        clockInitialized = true

    }


    private fun drawHand(
        canvas: Canvas, handType: HandTypes, location: Double

    ) {
        val angle = Math.PI * location / 30 - Math.PI / 2
        var handRadius = 0
        var strokeWidth = 0f
        when {

            (handType == HandTypes.HOUR) -> {
                handRadius =
                    -(clockRadius / 1.5 - clockHandTruncation - clockHourHandTruncation).toInt()
                strokeWidth = (bareMinimum / 40).toFloat()
            }
            handType == HandTypes.MINUTE -> {
                handRadius = -(clockRadius - clockHandTruncation).toInt()
                strokeWidth = (bareMinimum / 90).toFloat()
            }
            handType == HandTypes.SECOND -> {
                handRadius = -(clockRadius - clockHandTruncation - clockHandTruncation).toInt()
                strokeWidth = (bareMinimum / 200).toFloat()
            }
        }
        paint?.let {
            it.strokeWidth = strokeWidth
            canvas.drawLine(
                (clockWidth / 2 - cos(angle) * handRadius / 5).toFloat(),
                (clockHeight / 2 - sin(angle) * handRadius / 5).toFloat(),
                (clockWidth / 2 + cos(angle) * handRadius / 2).toFloat(),
                (clockHeight / 2 + sin(angle) * handRadius / 2).toFloat(),
                it
            )
        }
    }

    private fun drawShadow(canvas: Canvas, handType: HandTypes, location: Double) {
        val angle = Math.PI * location / 30 - Math.PI / 2
        var handRadius = 0
        var strokeWidth = 0f
        val shadowInterval = when {
            (handType == HandTypes.HOUR) -> {
                (bareMinimum / 100)
            }
            (handType == HandTypes.MINUTE) -> {
                (bareMinimum / 90)
            }
            (handType == HandTypes.SECOND) -> {
                (bareMinimum / 50)
            }
            else -> {
                0
            }
        }
        when {

            (handType == HandTypes.HOUR) -> {
                handRadius =
                    -(clockRadius / 1.5 - clockHandTruncation - clockHourHandTruncation).toInt()
                strokeWidth = (bareMinimum / 30).toFloat()
            }
            handType == HandTypes.MINUTE -> {
                handRadius = -(clockRadius - clockHandTruncation).toInt()
                strokeWidth = (bareMinimum / 80).toFloat()
            }
            handType == HandTypes.SECOND -> {
                handRadius = -(clockRadius - clockHandTruncation - clockHandTruncation).toInt()
                strokeWidth = (bareMinimum / 180).toFloat()
            }
        }
        paint?.let {
            it.strokeWidth = strokeWidth
            it.color = Color.argb(60, 0, 0, 0)
            canvas.drawLine(
                (clockWidth / 2 - cos(angle) * handRadius / 5 + shadowInterval).toFloat(),
                (clockHeight / 2 - sin(angle) * handRadius / 5 + shadowInterval).toFloat(),
                (clockWidth / 2 + cos(angle) * handRadius / 2 + shadowInterval).toFloat(),
                (clockHeight / 2 + sin(angle) * handRadius / 2 + shadowInterval).toFloat(),
                it
            )
        }

    }

    private fun drawHands(canvas: Canvas) {
        paint?.reset()
        val calendar = Calendar.getInstance()
//        calendar.timeZone = this.customTimezone

        var hour = calendar[Calendar.HOUR_OF_DAY].toFloat()
        hour = if (hour > 12) {
            hour - 12
        } else hour

        drawHand(
            canvas,
            HandTypes.HOUR,
            ((hour + calendar[Calendar.MINUTE].toDouble() / 60) * 5f).toDouble()
        )

        drawHand(
            canvas,
            HandTypes.MINUTE,
            calendar[Calendar.MINUTE].toDouble(),
        )
        drawHand(
            canvas,
            HandTypes.SECOND, calendar[Calendar.SECOND].toDouble(),
        )
        drawShadow(
            canvas,
            HandTypes.HOUR,
            ((hour + calendar[Calendar.MINUTE].toDouble() / 60) * 5f).toDouble()
        )
        drawShadow(canvas, HandTypes.MINUTE, calendar[Calendar.MINUTE].toDouble())
        drawShadow(canvas, HandTypes.SECOND, calendar[Calendar.SECOND].toDouble())

    }

    private fun drawNumeral(canvas: Canvas) {
        paint?.let {
            it.reset()
            it.textSize = clockFontSize.toFloat()

            for (number in digits) {
                val numString = number.toString()
                it.getTextBounds(numString, 0, numString.length, rect)
                val angle = Math.PI / 6 * (number + 3)

                val x =
                    (width / 2 + cos(angle) * (clockRadius / 2.1 - bareMinimum / 10) - rect.width() / 2).toInt()
                val y =
                    (height / 2 + sin(angle) * (clockRadius / 2.1 - bareMinimum / 10) + rect.height() / 2).toInt()
                canvas.drawText(numString, x.toFloat(), y.toFloat(), it)
            }
        }

    }


    private fun drawCenter(canvas: Canvas) {
        paint?.reset()


        paint?.let {
            paint?.let {
                it.reset()
                it.color = Color.WHITE

                it.style = Paint.Style.FILL
                it.isAntiAlias = true
                canvas.drawCircle(
                    (clockWidth / 2).toFloat(),
                    (clockHeight / 2).toFloat(),
                    (clockRadius + clockPadding - (bareMinimum / 20).toFloat()),
                    it
                )

            }
            it.color = Color.BLACK
            it.style = Paint.Style.FILL
            canvas.drawCircle(
                (clockWidth / 2).toFloat(),
                (clockHeight / 2).toFloat(),
                (bareMinimum / 93).toFloat(),
                it
            )
        }
    }


    private fun drawDotsCircle(canvas: Canvas) {
        paint?.reset()
        var dotRadius: Float
        for (i in 1..60) {
            val angle = Math.PI / 30 * (i - 1)
            val x = (clockWidth / 2 + cos(angle) * clockRadius / 1.2).toInt()
            val y = (clockHeight / 2 + sin(angle) * clockRadius / 1.2).toInt()
            dotRadius = if ((i - 1) % 5 == 0) {
                (bareMinimum / 80).toFloat()
            } else {
                (bareMinimum / 200).toFloat()
            }
            paint?.let { canvas.drawCircle(x.toFloat(), y.toFloat(), dotRadius, it) }
        }
    }

    private fun drawCircle(canvas: Canvas) {
        paint?.let {
            it.reset()
            it.color = Color.BLACK
            it.strokeWidth = (bareMinimum / 30).toFloat()
            it.style = Paint.Style.STROKE
            it.isAntiAlias = true
            canvas.drawCircle(
                (clockWidth / 2).toFloat(),
                (clockHeight / 2).toFloat(),
                (clockRadius + clockPadding - (bareMinimum / 25).toFloat()),
                it
            )
            it.strokeWidth = 5f
        }
    }

    //finally
    override fun onDraw(canvas: Canvas) {
        if (!clockInitialized) init()
        canvas.drawColor(Color.argb(0,0,0,0))
        drawCenter(canvas)
        drawCircle(canvas)
        drawNumeral(canvas)
        drawDotsCircle(canvas)
        drawHands(canvas)
        postInvalidateDelayed(50)

    }
}
