package com.dwiki.storyapp.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import androidx.appcompat.widget.AppCompatButton
import androidx.core.content.ContextCompat
import com.dwiki.storyapp.R

class CustomButtonLogin:AppCompatButton {
    private  var txtColor:Int = 1
    private lateinit var enabled: Drawable
    private lateinit var disabled: Drawable
    private lateinit var txtSignIn:String

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
        background = if (isEnabled) enabled else disabled
        textSize = 14f
        gravity = Gravity.CENTER
        text = txtSignIn

    }

    private fun init(){
        txtColor = ContextCompat.getColor(context, R.color.white)
        txtSignIn = context.getString(R.string.sign_in)
        enabled = ContextCompat.getDrawable(context,R.drawable.button_enable) as Drawable
        disabled = ContextCompat.getDrawable(context,R.drawable.button_disable) as Drawable
    }
}