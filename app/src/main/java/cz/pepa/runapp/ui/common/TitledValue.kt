package cz.pepa.runapp.ui.common


import android.content.Context
import android.util.AttributeSet
import android.widget.LinearLayout
import cz.pepa.runapp.R
import io.stepuplabs.settleup.util.extensions.inflate

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class TitledValue @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {

    init {
        inflate(R.layout.view_titled_value)
    }

}