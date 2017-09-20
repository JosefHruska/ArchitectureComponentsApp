package io.stepuplabs.settleup.util.extensions

import android.annotation.SuppressLint
import android.app.Activity
import android.app.Notification
import android.content.Context
import android.content.res.Configuration
import android.content.res.TypedArray
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.LayerDrawable
import android.support.annotation.DrawableRes
import android.support.annotation.LayoutRes
import android.support.annotation.StringRes
import android.support.annotation.StyleableRes
import android.support.design.widget.Snackbar
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v4.view.MotionEventCompat
import android.support.v7.widget.PopupMenu
import android.text.Editable
import android.text.TextWatcher
import android.util.AttributeSet
import android.util.TypedValue
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import cz.pepa.runapp.R
import cz.pepa.runapp.app
import cz.pepa.runapp.data.Goal
import cz.pepa.runapp.data.GoalId
import cz.pepa.runapp.extensions.isLargerOrEqualApi
import cz.pepa.runapp.ui.base.BaseActivity
import org.jetbrains.anko.*


/**
 * UI-related extensions.
 *
 * @author David Vávra (david@stepuplabs.io)
 */

fun View.snackbar(message: CharSequence, duration: Int = Snackbar.LENGTH_INDEFINITE, actionText: String? = null, action: (() -> Unit)? = null) {
    val snackbar = Snackbar.make(this, message, duration)
    // Snackbar is not two lines on Android 4, this workaround fixes it:
    val textView = snackbar.view.findViewById<TextView>(android.support.design.R.id.snackbar_text)
    textView.maxLines = 3
    if (actionText != null && action != null) {
        snackbar.setAction(actionText, { action() })
    }
    snackbar.show()
}

fun BaseActivity.showConfirmationDialog(@StringRes messageRes: Int, @StringRes titleRes: Int, @StringRes positiveButtonRes: Int? = null, @StringRes negativeButtonRes: Int? = null, positiveClicked: (() -> (Unit))? = null, negativeClicked: (() -> (Unit))? = null) {
    alert(messageRes.toText(), titleRes.toText()) {
        positiveButtonRes?.let { positiveButton(it.toText()) { positiveClicked?.invoke() } }
        negativeButtonRes?.let { negativeButton(it.toText()) { negativeClicked?.invoke() } }
    }.show()
}

//fun View.ownerOnlySnackBar(ownerName: String) {
//    this.snackbar(R.string.owner_only_warning.toText(ownerName), Snackbar.LENGTH_LONG)
//}
//
//fun View.errorSnackbar(error: Throwable) {
//    snackbar(R.string.something_went_wrong.toText(), actionText = R.string.report.toText(), action = { context.sendErrorEmail(error) })
//}

//fun BaseActivity.warning(@StringRes warningRes: Int) {
//    this.getContentView().snackbar(warningRes.toText(), Snackbar.LENGTH_LONG)
//}

fun View.popupMenuOnClick(menuRes: Int, onClick: (Int) -> Unit) {
    this.setOnClickListener {
        val popup = PopupMenu(this.context, this, Gravity.END)
        val inflater = popup.menuInflater
        inflater.inflate(menuRes, popup.menu)
        popup.setOnMenuItemClickListener {
            onClick(it.itemId)
            true
        }
        popup.show()
    }
}

fun notification(context: Context, func: NotificationCompat.Builder.() -> Unit): Notification {
    val builder = NotificationCompat.Builder(context)
    builder.func()
    return builder.build()
}


fun Boolean.toVisibility(): Int {
    return if (this) View.VISIBLE else View.GONE
}

fun View.makeInvisible() {
    this.visibility = View.INVISIBLE
}

fun View.show() {
    this.visibility = View.VISIBLE
}

fun View.hide() {
    this.visibility = View.GONE
}

fun EditText.setOnTextChangedListener(onTextChanged: (String) -> Unit) {
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // ignored
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // ignored
        }

        override fun afterTextChanged(editable: Editable) {
            onTextChanged(editable.toString())
        }
    })
}

fun EditText.monitorTextChangedByHuman(onTextChanged: (String) -> Unit) {
    this.setOnKeyListener { _, _, keyEvent ->
        if (keyEvent.action == KeyEvent.ACTION_UP) {
            onTextChanged(text.toString())
        }
        false
    }
    this.addTextChangedListener(object : TextWatcher {
        override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            // ignored
        }

        override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, count: Int) {
            if (count == 0) {
                // backspace detection. Workaround from here: http://stackoverflow.com/a/37418871/560358
                onTextChanged(text.toString())
            }
        }

        override fun afterTextChanged(editable: Editable) {
            // ignored
        }
    })
}

fun ImageView.setBackgroundDrawable(@DrawableRes drawableIdRes: Int) {
    this.setImageDrawable(this.getDrawableCompat(drawableIdRes))
}

fun TextView.makeUnderline() {
    this.paintFlags = this.paintFlags or Paint.UNDERLINE_TEXT_FLAG
}

// Set background color with ripple - should work for all APIs
fun View.setBackgroundColorWithRipple(backgroundColor: Int) {
    val rippleBackground = LayerDrawable(arrayOf(ColorDrawable(backgroundColor), getDrawableCompat(R.drawable.ripple_background)))
    this.background = rippleBackground
}

//// FinishButton has AppCompatButton wrapped to FrameLayout. We need to apply background to AppCompatButton.
//fun FinishButton.setBackgroundColorWithRipple(backgroundColor: Int) {
//    val rippleBackground = LayerDrawable(arrayOf(ColorDrawable(backgroundColor), getDrawableCompat(R.drawable.ripple_background)))
//    this.setButtonBackground(rippleBackground)
//}

fun getActionbarSize(): Int {
    val tv = TypedValue()
    if (app().theme.resolveAttribute(android.R.attr.actionBarSize, tv, true)) {
        return TypedValue.complexToDimensionPixelSize(tv.data, app().resources.displayMetrics)
    }
    return 0
}

fun Int.resToPx(): Int {
    return app().resources.getDimensionPixelSize(this)
}

fun Int.dpToPx(): Int {
    return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, this.toFloat(), app().displayMetrics).toInt()
}

/**
 * Converts color resource to int color.
 */
fun Int.toColor(): Int {
    return ContextCompat.getColor(app(), this)
}

/**
 * Converts hexacode to int color.
 */
fun String.toColor(): Int {
    return Color.parseColor(this)
}

/**
 * Converts string resource to text.
 */
fun Int.toText(parameter: String? = null): String {
    if (parameter == null) {
        return app().getString(this)
    } else {
        return app().getString(this, parameter)
    }
}

/**
 * Converts integer resource to integer.
 */
fun Int.toInteger(): Int {
    return app().resources.getInteger(this)
}

fun View.setOnTouchedListener(onTouchedListener: () -> Unit) {
    this.setOnTouchListener { _, motionEvent ->
        if (MotionEventCompat.getActionMasked(motionEvent) == MotionEvent.ACTION_DOWN) {
            onTouchedListener()
        }
        false
    }
}

fun View.getDrawableCompat(@DrawableRes drawableIdRes: Int): Drawable {
    return ContextCompat.getDrawable(this.context, drawableIdRes)
}

fun Activity.isLandscapeOnPhone(): Boolean {
    val configuration = resources.configuration
    return configuration.orientation == Configuration.ORIENTATION_LANDSCAPE && configuration.screenWidthDp < 720
}

/**
 * Source: http://stackoverflow.com/questions/6217378/place-cursor-at-the-end-of-text-in-edittext
 */
fun EditText.moveCursorToEndWhenFocused() {
    this.setOnFocusChangeListener { _, focused ->
        if (focused) {
            doAsync {
                uiThread {
                    this@moveCursorToEndWhenFocused.setSelection(this@moveCursorToEndWhenFocused.length())
                }
            }
        }
    }
}

fun EditText.setOnKeyboardDoneClicked(onDoneClicked: () -> Unit) {
    this.setOnEditorActionListener { _, actionId, _ ->
        if (actionId == EditorInfo.IME_ACTION_DONE) {
            onDoneClicked()
        }
        false
    }
}

fun View.setCustomAttributes(attributes: AttributeSet?, @StyleableRes styleableRes: IntArray, handleCustomAttributes: (TypedArray) -> Unit) {
    val attrs = this.context.theme.obtainStyledAttributes(
            attributes,
            styleableRes,
            0, 0)
    try {
        handleCustomAttributes(attrs)
    } finally {
        attrs.recycle()
    }
}

fun View.requestFocusWithDelay() {
    this.postDelayed({ this.requestFocus() }, 300)
}

@SuppressLint("NewApi")
fun TextView.setTextAppearanceCompat(textAppearance: Int) {
    // setTextAppearance doesn't need context for API <= 23 anymore -> It is deprecated.
    if (isLargerOrEqualApi(23)) {
        this.setTextAppearance(textAppearance)
    } else {
        @Suppress("DEPRECATION")
        this.setTextAppearance(context, textAppearance)
    }
}

fun Context.getScreenWidth(): Int {
    return this.resources.configuration.screenWidthDp.dpToPx()
}

fun Activity.setWindowBackgroundColor(color: Int) {
    this.window.setBackgroundDrawable(ColorDrawable(color))
}

fun ViewGroup.inflate(@LayoutRes layoutRes: Int) {
    this.context.layoutInflater.inflate(layoutRes, this, true)
}

fun Context.inflate(@LayoutRes layoutRes: Int, rootViewGroup: ViewGroup? = null): View {
    return layoutInflater.inflate(layoutRes, rootViewGroup)
}

fun Goal.getImageResource(): Int {
    return when (this.type) {
        GoalId.CALORIES -> R.drawable.ic_burn
        GoalId.ACTIVE_TIME -> R.drawable.ic_active_time
        GoalId.STEPS -> R.drawable.ic_steps
        GoalId.DISTANCE -> R.drawable.ic_distance
        GoalId.ACTIVE_TIME_SPECIFIC -> R.drawable.ic_active_time
        GoalId.WEIGHT -> R.drawable.ic_weight_kilogram
    }
}
