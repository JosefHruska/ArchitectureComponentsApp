package cz.pepa.runapp.ui.common

import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import cz.pepa.runapp.R
import io.stepuplabs.settleup.util.extensions.inflate
import io.stepuplabs.settleup.util.extensions.setCustomAttributes
import kotlinx.android.synthetic.main.view_titled_value.view.*

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class TitledValue @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        inflate(R.layout.view_titled_value)
        setAttributes(attrs)
    }

    inline var value: CharSequence
        get() = vValue.text
        set(v) {
             vValue.text = v
        }

    inline var title: CharSequence
        get() = vTitle.text
        set(v) {
            vTitle.text = v
        }

    private fun setAttributes(attrs: AttributeSet?) {
        setCustomAttributes(attrs, R.styleable.TitledValue, {
            it.getString(R.styleable.TitledValue_name)?.let { title = it }
            it.getString(R.styleable.TitledValue_value)?.let { value = it }
        })
    }

}