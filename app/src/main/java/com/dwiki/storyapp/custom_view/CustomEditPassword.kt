package com.dwiki.storyapp.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import androidx.core.widget.doAfterTextChanged
import com.dwiki.storyapp.R

class CustomEditPassword:AppCompatEditText, View.OnTouchListener {
    private var hintColor:Int? = null
    private lateinit var hiddenPassword:Drawable


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
        showPassword()
        setBackgroundResource(R.drawable.border_edit_text)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        setHint(R.string.input_password)
        hintColor?.let { setHintTextColor(it) }
        textSize = 15f
    }

    private fun init(){
        hiddenPassword = ContextCompat.getDrawable(context,R.drawable.hide_passs_image) as Drawable
        hintColor = ContextCompat.getColor(context,R.color.grey)
        textChangeListener()
        setOnTouchListener(this)
    }

    private fun textChangeListener() {
        doAfterTextChanged { s ->
            if (s.toString().length < 6 && s?.isNotEmpty() == true){
                   error = "At Least 6 Character"
               }
        }
    }

    private fun showPassword(){
        setButtonDrawables(endOfTheText = hiddenPassword)
    }
    private fun  hidePassword(){
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText: Drawable? = null,
        endOfTheText: Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val eyeButtonStart: Float
            val eyeButtonEnd: Float
            var isEyeButtonClicked = false

            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                eyeButtonEnd = (hiddenPassword.intrinsicWidth + paddingStart).toFloat()
                if (event.x < eyeButtonEnd) isEyeButtonClicked = true
            } else {
                eyeButtonStart = (width - paddingEnd - hiddenPassword.intrinsicWidth).toFloat()
                if (event.x > eyeButtonStart) isEyeButtonClicked = true
            }

            if (isEyeButtonClicked) {
                return when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        hidePassword()
                        if (transformationMethod.equals(HideReturnsTransformationMethod.getInstance())) {
                            transformationMethod = PasswordTransformationMethod.getInstance()
                            hiddenPassword = ContextCompat.getDrawable(context, R.drawable.hide_passs_image) as Drawable
                           showPassword()
                        } else {
                            transformationMethod = HideReturnsTransformationMethod.getInstance()
                            hiddenPassword = ContextCompat.getDrawable(context, R.drawable.show_pass_image) as Drawable
                            showPassword()
                        }
                        true
                    }
                    else -> false
                }
            } else return false
        }
        return false
    }
}