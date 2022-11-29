package com.example.storyapp.ui.customcomponent

import android.content.Context
import android.util.AttributeSet
import android.view.ContextThemeWrapper
import com.example.storyapp.R
import com.google.android.material.textfield.TextInputEditText

class CustomTextInputEditText : TextInputEditText {

    constructor(context: Context) : this(context, null)
    constructor(context: Context, attrs: AttributeSet? = null)
            : this(context, attrs, R.attr.TextInputEditTextStyle)
    constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int)
            : super(ContextThemeWrapper(context, R.style.TextInputEditTextTheme), attrs, defStyleAttr)
}