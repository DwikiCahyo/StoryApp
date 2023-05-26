package com.dwiki.storyapp.custom_view

import android.content.Context
import android.graphics.Canvas
import android.graphics.drawable.Drawable
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.view.MotionEvent
import android.view.View
import androidx.appcompat.widget.AppCompatEditText
import androidx.core.content.ContextCompat
import com.dwiki.storyapp.R

class CustomEditName:AppCompatEditText, View.OnTouchListener {

    private lateinit var clearTextButton: Drawable
    private var hintColor:Int? = null

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
        setBackgroundResource(R.drawable.border_edit_text)
        textAlignment = View.TEXT_ALIGNMENT_VIEW_START
        setHint(R.string.name_edt)
        hintColor?.let { setHintTextColor(it) }
        textSize = 15f
    }


    private fun init(){
        clearTextButton = ContextCompat.getDrawable(context, R.drawable.close_image) as Drawable
        hintColor = ContextCompat.getColor(context, R.color.grey)
        setOnTouchListener(this)
        textChangeListener()
    }

    private fun showClearButton(){
        setButtonDrawables(endOfTheText = clearTextButton)
    }
    private fun hideClearButton(){
        setButtonDrawables()
    }

    private fun setButtonDrawables(
        startOfTheText: Drawable? = null,
        topOfTheText:Drawable? = null,
        endOfTheText:Drawable? = null,
        bottomOfTheText: Drawable? = null
    ){
        setCompoundDrawablesWithIntrinsicBounds(
            startOfTheText,
            topOfTheText,
            endOfTheText,
            bottomOfTheText
        )
    }

    private fun textChangeListener() {
        addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {
                // Do nothing.
            }

            override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
                if (s.toString().isNotEmpty()) {
                    showClearButton()
                } else
                    hideClearButton()


            }

            override fun afterTextChanged(s: Editable) {
                if(s.toString().isEmpty()) error = "Name cant empty"
            }
        })
    }

    override fun onTouch(v: View?, event: MotionEvent): Boolean {
        if (compoundDrawables[2] != null) {
            val clearButtonStart: Float
            val clearButtonEnd: Float
            var isClearButtonClicked = false
            if (layoutDirection == View.LAYOUT_DIRECTION_RTL) {
                clearButtonEnd = (clearTextButton.intrinsicWidth + paddingStart).toFloat()
                if (  event.x < clearButtonEnd) isClearButtonClicked = true


            } else {
                clearButtonStart = (width - paddingEnd - clearTextButton.intrinsicWidth).toFloat()
                if (  event.x > clearButtonStart) isClearButtonClicked = true


            }
            if (isClearButtonClicked) {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        clearTextButton = ContextCompat.getDrawable(context, R.drawable.close_image) as Drawable
                        showClearButton()
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        clearTextButton = ContextCompat.getDrawable(context, R.drawable.close_image) as Drawable
                        when {
                            text != null -> text?.clear()
                        }
                        hideClearButton()
                        return true
                    }
                    else -> return false
                }
            } else return false
        }
        return false
    }

}