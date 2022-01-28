package com.montcomp.lendmoney.Utils

import android.content.Context
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Path
import android.view.View
import com.montcomp.lendmoney.R

class DesignBackground(context: Context?) : View(context) {

    override fun onDraw(canvas: Canvas?) {
        val paint = Paint()
        paint.color = context.resources.getColor(R.color.green_opacity)
        paint.style = Paint.Style.FILL
        paint.strokeWidth = 15F

        val paint2 = Paint()
        paint2.color = context.resources.getColor(R.color.green_opacity)
        paint2.style = Paint.Style.FILL
        paint2.strokeWidth = 15F

        val width = canvas?.width
        val heigth = canvas?.height

        val path = Path()
        path.lineTo(0F, (heigth!!*0.15).toFloat())
        path.quadTo((width!!*0.20).toFloat(),(heigth!!*0.20).toFloat(), (width!!*0.15).toFloat(), 0F)
        path.lineTo(0F, 0F)

        val path2 = Path()
        path2.lineTo(0F, (heigth!!*0.10).toFloat())
        path2.quadTo((width!!*0.50).toFloat(),(heigth!!*0.10).toFloat(), (width!!*0.40).toFloat(), 0F)
        path2.lineTo(0F, 0F)

        canvas.drawPath(path, paint)
        canvas.drawPath(path2, paint2)

    }
}