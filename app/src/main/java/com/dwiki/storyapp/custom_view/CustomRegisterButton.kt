package com.dwiki.storyapp.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.dwiki.storyapp.R

class CustomRegisterButton:AppCompatButton {

    private  var txtColor:Int = 1
    private lateinit var enable: Drawable
    private lateinit var disable:Drawable
    private lateinit var txtButton:String

    constructor(context: Context) : super(context) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs) {
        init()
    }
    constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int) : super(context, attrs, defStyleAttr) {
        init()
    }

    override fun onDraw(canvas: Canvas?) {
        super.onDraw(canvas)
        setTextColor(txtColor)
        background = if (isEnabled) enable else disable
        textSize = 14f
        gravity = Gravity.CENTER
        text = txtButton
    }

    private fun init() {
        txtColor = ContextCompat.getColor(context, R.color.white)
        txtButton = context.getString(R.string.register)
        enable = ContextCompat.getDrawable(context,R.drawable.button_enable) as Drawable
        disable = ContextCompat.getDrawable(context,R.drawable.button_disable) as Drawable
    }

}