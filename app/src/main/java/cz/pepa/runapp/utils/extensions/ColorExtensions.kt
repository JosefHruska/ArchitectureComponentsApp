package cz.pepa.runapp.extensions

import android.app.Activity
import android.content.res.ColorStateList
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.LayerDrawable
import android.support.annotation.ColorRes
import android.support.v4.graphics.ColorUtils
import android.support.v7.widget.AppCompatCheckBox
import android.support.v7.widget.AppCompatEditText
import android.support.v7.widget.SwitchCompat
import android.view.View
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.TextView
import cz.pepa.runapp.R
import io.stepuplabs.settleup.util.extensions.toColor

/**
 * Utils related to changing color of views.
 *
 * @author David Vávra (david@stepuplabs.io)
 */
fun View.setBackgroundDrawableColor(color: Int) {
    this.background.setColorFilter(color, PorterDuff.Mode.SRC_IN)
}

fun TextView.setTextColorRes(@ColorRes colorRes: Int) {
    this.setTextColor(colorRes.toColor())
}

fun ProgressBar.setColor(color: Int) {
    if (isLargerOrEqualApi(21)) {
        this.indeterminateDrawable.setTint(color)
    }
}

fun AppCompatEditText.setColor(color: Int) {
    val states = arrayOf(intArrayOf(-android.R.attr.state_focused),
            intArrayOf(android.R.attr.state_focused)
    )
    val colors = intArrayOf(R.color.gray_4.toColor(), color)
    this.supportBackgroundTintList = ColorStateList(states, colors)
}

fun Activity.setStatusBarColor(color: Int) {
    if (isLargerOrEqualApi(21)) {
        window.statusBarColor = color
    }
}

fun ImageView.setColorFirstLayer(color: Int) {
    if (this.drawable !is LayerDrawable) {
        throw IllegalArgumentException("ImageView is not LayerDrawable")
    }
    val drawable = this.drawable as LayerDrawable
    drawable.getDrawable(0).setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}

fun AppCompatCheckBox.setColor(stateCheckedColor: Int, stateUncheckedColor: Int = R.color.gray_4.toColor()) {
    val states = arrayOf(intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
    )
    val colors = intArrayOf(stateUncheckedColor, stateCheckedColor)
    this.supportButtonTintList = ColorStateList(states, colors)
    this.invalidate()
}

fun ImageView.setFirstLayerColor(color: Int) {
    (this.drawable as LayerDrawable).getDrawable(0).setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}

fun ImageView.setSecondLayerColor(color: Int) {
    (this.drawable as LayerDrawable).getDrawable(1).setColorFilter(color, PorterDuff.Mode.SRC_ATOP)
}

fun SwitchCompat.setColor(color: Int) {
    val states = arrayOf(intArrayOf(-android.R.attr.state_checked),
            intArrayOf(android.R.attr.state_checked)
    )
    val trackColors = intArrayOf(R.color.gray_3.toColor(), ColorUtils.setAlphaComponent(color, 128))
    val thumbColors = intArrayOf(R.color.white.toColor(), color)
    this.trackTintList = ColorStateList(states, trackColors)
    this.thumbTintList = ColorStateList(states, thumbColors)
}

// Workaround for FrameLayouts on API 16
// setBackgroundColor / android:background = @color/random_color doesn't work for API 16 - it changes color of other views on the screen
fun View.setBackgroundColorCompat(color: Int) {
    val colorDrawable = ColorDrawable(color)
    this.background = colorDrawable
}

// It has to be declared this way because it has the same class name as com.github.clans.fab.FloatingActionButton.
fun android.support.design.widget.FloatingActionButton.setButtonColors(buttonColor: Int, iconColor: Int) {
    this.backgroundTintList = ColorStateList.valueOf(buttonColor)
    this.setColorFilter(iconColor)
}