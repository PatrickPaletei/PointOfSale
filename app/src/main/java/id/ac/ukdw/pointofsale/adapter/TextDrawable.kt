package id.ac.ukdw.pointofsale.adapter

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable

class TextDrawable(private val text: String) : Drawable() {
    private val paint: Paint = Paint()

    init {
        paint.color = Color.BLACK
        paint.textSize = 48f
        paint.isAntiAlias = true
        paint.textAlign = Paint.Align.CENTER
    }

    override fun draw(canvas: Canvas) {
        val bounds = bounds
        val x = bounds.width() / 2f
        val y = bounds.height() / 2f - (paint.descent() + paint.ascent()) / 2f

        canvas.drawText(text, x, y, paint)
    }

    override fun setAlpha(alpha: Int) {
        paint.alpha = alpha
    }

    override fun setColorFilter(colorFilter: android.graphics.ColorFilter?) {
        paint.colorFilter = colorFilter
    }

    override fun getOpacity(): Int {
        return paint.alpha
    }
}
