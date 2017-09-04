package cz.pepa.runapp.ui.main.overview

import android.content.Context
import android.support.annotation.ArrayRes
import android.util.AttributeSet
import android.widget.ArrayAdapter
import android.widget.LinearLayout
import cz.pepa.runapp.R
import io.stepuplabs.settleup.util.extensions.inflate
import kotlinx.android.synthetic.main.view_set_goal_card.view.*

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class SetGoalCard @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : LinearLayout(context, attrs, defStyleAttr) {


    init {
        inflate(R.layout.view_set_goal_card)
//        setAttributes(attrs)
    }

    private fun setItems(@ArrayRes itemsArrayRes: Int = R.array.spinner_set_goal_card) {
        val adapter = ArrayAdapter.createFromResource(this.context, itemsArrayRes, android.R.layout.simple_spinner_item)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        vUnitSpinner.adapter = adapter
    }
}