package com.adz.mlimivisual.views

import android.content.Context
import android.graphics.BlendMode
import android.graphics.BlendModeColorFilter
import android.graphics.PorterDuff
import android.os.Build
import android.util.AttributeSet
import android.widget.ProgressBar
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import com.adz.mlimivisual.R

@Suppress("DEPRECATION")
class CustomProgress : ProgressBar {
    constructor(context: Context) : super(context) {
        setTintColor(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?
    ) : super(context, attrs) {
        setTintColor(context)
    }

    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int
    ) : super(context, attrs, defStyleAttr) {
        setTintColor(context)
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    constructor(
        context: Context,
        attrs: AttributeSet?,
        defStyleAttr: Int,
        defStyleRes: Int
    ) : super(context, attrs, defStyleAttr, defStyleRes) {
        setTintColor(context)
    }

    private fun setTintColor(context: Context) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            this.indeterminateDrawable.colorFilter = BlendModeColorFilter(
                getColor(
                    context, R.color.colorTheme
                ), BlendMode.SRC_ATOP
            )
        } else {
            this.indeterminateDrawable.setColorFilter(
                getColor(
                    context, R.color.colorTheme
                ), PorterDuff.Mode.MULTIPLY
            )
        }
    }
    fun getColor(context: Context, color: Int): Int {
        return ContextCompat.getColor(context, color)
    }
}