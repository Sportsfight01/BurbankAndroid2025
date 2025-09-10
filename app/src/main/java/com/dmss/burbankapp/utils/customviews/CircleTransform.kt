package com.dmss.burbankapp.utils.customviews

import android.graphics.*
import com.squareup.picasso.Transformation;

class CircleTransform : Transformation {
    override fun transform(source: Bitmap): Bitmap {
        val size: Int = Math.min(source.getWidth(), source.getHeight())
        val x: Int = (source.getWidth() - size) / 2
        val y: Int = (source.getHeight() - size) / 2
        val squaredBitmap: Bitmap = Bitmap.createBitmap(source, x, y, size, size)
        if (squaredBitmap !== source) {
            source.recycle()
        }
        val bitmap: Bitmap = Bitmap.createBitmap(size, size, source.getConfig())
        val canvas = Canvas(bitmap)
        val paint = Paint()
        val shader = BitmapShader(
            squaredBitmap,
            Shader.TileMode.CLAMP, Shader.TileMode.CLAMP
        )
        paint.setShader(shader)
        paint.setAntiAlias(true)
        val r = size / 2f
        canvas.drawCircle(r, r, r, paint)
        squaredBitmap.recycle()
        return bitmap
    }

    override fun key(): String {
        return "circle"
    }
}