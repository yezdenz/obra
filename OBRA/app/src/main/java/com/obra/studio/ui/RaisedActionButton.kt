package com.obra.studio.ui

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import com.google.android.material.button.MaterialButton

/** A solid offset shadow inside the bottom inset, preserving Material's face and ripple. */
class RaisedActionButton(context: Context) : MaterialButton(context) {
    private val depth=4f*resources.displayMetrics.density
    private val shadow=Paint(Paint.ANTI_ALIAS_FLAG).apply { color=Color.BLACK }

    init {
        elevation=0f;stateListAnimator=null
        insetBottom=maxOf(insetBottom,kotlin.math.ceil(depth+2f*resources.displayMetrics.density).toInt())
    }

    override fun draw(canvas: Canvas) {
        shadow.alpha=if(isEnabled)255 else 80
        val radius=cornerRadius.toFloat()
        canvas.drawRoundRect(insetLeft.toFloat(),insetTop+depth,
            (width-insetRight).toFloat(),height-insetBottom+depth,radius,radius,shadow)
        super.draw(canvas)
    }
}
