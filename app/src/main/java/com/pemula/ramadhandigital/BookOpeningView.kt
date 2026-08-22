package com.pemula.ramadhandigital

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.ValueAnimator
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.RectF
import android.util.AttributeSet
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator

class BookOpeningView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null,
    defStyleAttr: Int = 0
) : View(context, attrs, defStyleAttr) {

    private val paint = Paint(Paint.ANTI_ALIAS_FLAG)

    private val warnaHijau = Color.rgb(
        46, 125, 50
    )

    private val warnaHalaman = Color.rgb(
        255, 253, 245
    )

    private val warnaGaris = Color.rgb(
        190, 175, 140
    )

    private val warnaEmas = Color.rgb(
        220, 190, 100
    )

    private var progress = 0f

    private var sedangAnimasi = false


    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        val centerX = width / 2f
        val centerY = height / 2f

        // ==========================================
        // KITAB MEMENUHI LAYAR
        // ==========================================

        val bookWidth = width * 0.94f
        val bookHeight = height * 0.68f

        val left =
            centerX - bookWidth / 2f

        val right =
            centerX + bookWidth / 2f

        val top =
            centerY - bookHeight / 2f

        val bottom =
            centerY + bookHeight / 2f


        // ==========================================
        // BACKGROUND
        // ==========================================

        canvas.drawColor(
            Color.rgb(
                248,
                250,
                248
            )
        )


        // ==========================================
        // BAYANGAN KITAB
        // ==========================================

        paint.style =
            Paint.Style.FILL

        paint.color =
            Color.argb(
                45,
                0,
                0,
                0
            )

        canvas.drawOval(
            left + 30f,
            bottom - 5f,
            right - 30f,
            bottom + 45f,
            paint
        )


        // ==========================================
        // COVER KITAB
        // ==========================================

        paint.color =
            warnaHijau

        val cover =
            RectF(
                left,
                top,
                right,
                bottom
            )

        canvas.drawRoundRect(
            cover,
            35f,
            35f,
            paint
        )


        // ==========================================
        // HALAMAN KIRI
        // ==========================================

        gambarHalamanKiri(
            canvas,
            centerX,
            top + 12f,
            bottom - 12f,
            bookWidth / 2f,
            progress
        )


        // ==========================================
        // HALAMAN KANAN
        // ==========================================

        gambarHalamanKanan(
            canvas,
            centerX,
            top + 12f,
            bottom - 12f,
            bookWidth / 2f,
            progress
        )


        // ==========================================
        // TULANG TENGAH
        // ==========================================

        paint.color =
            warnaHijau

        paint.style =
            Paint.Style.FILL

        canvas.drawRect(
            centerX - 8f,
            top + 5f,
            centerX + 8f,
            bottom - 5f,
            paint
        )


        // ==========================================
        // ORNAMEN EMAS
        // ==========================================

        paint.color =
            warnaEmas

        canvas.drawCircle(
            centerX,
            centerY,
            10f,
            paint
        )
    }


    // =================================================
    // HALAMAN KIRI
    // =================================================

    private fun gambarHalamanKiri(
        canvas: Canvas,
        centerX: Float,
        top: Float,
        bottom: Float,
        halfWidth: Float,
        progress: Float
    ) {

        val currentWidth =
            halfWidth * progress

        if (currentWidth <= 0f) {
            return
        }

        val halaman =
            RectF(
                centerX - currentWidth,
                top,
                centerX,
                bottom
            )

        paint.style =
            Paint.Style.FILL

        paint.color =
            warnaHalaman

        canvas.drawRoundRect(
            halaman,
            12f,
            12f,
            paint
        )

        if (progress > 0.15f) {

            gambarGarisHalaman(
                canvas,
                halaman
            )
        }
    }


    // =================================================
    // HALAMAN KANAN
    // =================================================

    private fun gambarHalamanKanan(
        canvas: Canvas,
        centerX: Float,
        top: Float,
        bottom: Float,
        halfWidth: Float,
        progress: Float
    ) {

        val currentWidth =
            halfWidth * progress

        if (currentWidth <= 0f) {
            return
        }

        val halaman =
            RectF(
                centerX,
                top,
                centerX + currentWidth,
                bottom
            )

        paint.style =
            Paint.Style.FILL

        paint.color =
            warnaHalaman

        canvas.drawRoundRect(
            halaman,
            12f,
            12f,
            paint
        )

        if (progress > 0.15f) {

            gambarGarisHalaman(
                canvas,
                halaman
            )
        }
    }


    // =================================================
    // GARIS HALAMAN
    // =================================================

    private fun gambarGarisHalaman(
        canvas: Canvas,
        rect: RectF
    ) {

        if (rect.width() < 20f) {
            return
        }

        paint.style =
            Paint.Style.STROKE

        paint.strokeWidth =
            2f

        paint.color =
            warnaGaris

        val padding =
            rect.width() * 0.12f

        var y =
            rect.top + 45f

        while (
            y < rect.bottom - 35f
        ) {

            canvas.drawLine(
                rect.left + padding,
                y,
                rect.right - padding,
                y,
                paint
            )

            y += 22f
        }

        paint.style =
            Paint.Style.FILL
    }


    // =================================================
    // ANIMASI BUKA KITAB
    // =================================================

    fun startOpeningAnimation(
        onFinished: () -> Unit
    ) {

        if (sedangAnimasi) {
            return
        }

        sedangAnimasi = true

        progress = 0f

        invalidate()

        val animator =
            ValueAnimator.ofFloat(
                0f,
                1f
            )

        // Durasi 2 detik
        animator.duration = 2000L

        animator.interpolator =
            AccelerateDecelerateInterpolator()

        animator.addUpdateListener {

            progress =
                it.animatedValue as Float

            invalidate()
        }

        animator.addListener(
            object :
                AnimatorListenerAdapter() {

                override fun onAnimationEnd(
                    animation: Animator
                ) {

                    sedangAnimasi =
                        false

                    progress =
                        1f

                    invalidate()

                    onFinished()
                }

                override fun onAnimationCancel(
                    animation: Animator
                ) {

                    sedangAnimasi =
                        false
                }
            }
        )

        animator.start()
    }
}