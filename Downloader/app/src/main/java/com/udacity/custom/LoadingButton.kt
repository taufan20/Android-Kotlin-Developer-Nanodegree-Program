package com.udacity.custom

import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Typeface
import android.util.AttributeSet
import android.view.View
import android.view.animation.LinearInterpolator
import androidx.core.content.ContextCompat
import androidx.core.content.withStyledAttributes
import com.udacity.R
import kotlin.properties.Delegates

class LoadingButton @JvmOverloads constructor(
    context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private var widthSize = 0
    private var heightSize = 0
    private var buttonLabel: String = resources.getString(R.string.button_name)

    private var valueAnimator = ValueAnimator()
    private var progress: Float = 0f


    private var buttonPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context,R.color.colorPrimary)
    }

    private var buttonProgressPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        color = ContextCompat.getColor(context,R.color.colorPrimaryDark)
        alpha = 100
    }

    private var buttonTextPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        style = Paint.Style.FILL
        textSize = 55.0f
        textAlign = Paint.Align.CENTER
        color = Color.WHITE
    }

    private var arcPaint = Paint(Paint.ANTI_ALIAS_FLAG).apply {
        color = ContextCompat.getColor(context,R.color.colorAccent)
    }

    var buttonState: ButtonState by Delegates.observable<ButtonState>(ButtonState.Completed) { p, oldState, newState ->

        when (newState) {
            ButtonState.Clicked -> {
                progress = 0f
                invalidate()
                cancelAnimation()
            }
            ButtonState.Loading -> {
                buttonLabel = context.getString(R.string.button_loading)
                invalidate()
                startAnimation()
            }
            ButtonState.Completed -> {
                buttonLabel = resources.getString(R.string.button_name)
                progress = 0.0f
                invalidate()
                cancelAnimation()
            }
        }
        invalidate()
    }


    init {
        context.theme.obtainStyledAttributes(
            attrs,
            R.styleable.LoadingButton,
            0, 0).apply {

            try {
                buttonTextPaint.color = getInteger(R.styleable.LoadingButton_android_textColor, 0)
                buttonPaint.color = getInteger(R.styleable.LoadingButton_background, 0)
                buttonProgressPaint.color = getInteger(R.styleable.LoadingButton_progressBackground, 0)
                arcPaint.color = getInteger(R.styleable.LoadingButton_loadingColor, 0)
            } finally {
                recycle()
            }
        }

        buttonState = ButtonState.Completed
    }



    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        drawButton(canvas)
        drawProgressButton(canvas)
        drawText(canvas)
        drawArc(canvas)

    }

    private fun drawButton(canvas: Canvas) {
        canvas.drawRect(
            0F,
            height.toFloat(),
            width.toFloat(),
            0F,
            buttonPaint
        )
    }

    private fun drawText(canvas: Canvas) {
        canvas.drawText(
            buttonLabel,
            (widthSize / 2).toFloat(),
            (heightSize / 2).toFloat() + 20,
            buttonTextPaint
        )
    }

    private fun drawArc(canvas: Canvas) {
        val progressVal = progress * 360

        val left = widthSize - 260f
        val top = (heightSize / 2) - 50f
        val right = (widthSize - 160f)
        val bottom = (heightSize / 2) + 50f

        canvas.drawArc(
            left,
            top,
            right,
            bottom,
            0f,
            progressVal,
            true,
            arcPaint)
    }

    private fun drawProgressButton(canvas: Canvas) {
        val progressWidth = progress * widthSize

        canvas.drawRect(
            0F,
            height.toFloat(),
            progressWidth,
            0F,
            buttonProgressPaint
        )
    }

    private fun startAnimation() {
        valueAnimator = ValueAnimator.ofFloat(0f, 1f).apply {
            addUpdateListener {
                progress = animatedValue as Float
                invalidate()
            }
            interpolator = LinearInterpolator()
            repeatMode = ValueAnimator.RESTART
            repeatCount = ValueAnimator.INFINITE
            duration = 1500
        }

        valueAnimator.start()
    }

    private fun cancelAnimation() {
        valueAnimator.cancel()
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        val minw: Int = paddingLeft + paddingRight + suggestedMinimumWidth
        val w: Int = resolveSizeAndState(minw, widthMeasureSpec, 1)
        val h: Int = resolveSizeAndState(
            View.MeasureSpec.getSize(w),
            heightMeasureSpec,
            0
        )
        widthSize = w
        heightSize = h
        setMeasuredDimension(w, h)
    }

}