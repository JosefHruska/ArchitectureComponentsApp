package cz.pepa.runapp.ui.main.overview

import android.view.View
import cz.pepa.runapp.R
import cz.pepa.runapp.data.Goal
import cz.pepa.runapp.ui.common.DataBinder
import cz.pepa.runapp.ui.common.ProgressLine
import formatPercentage
import io.stepuplabs.settleup.util.extensions.toColor
import org.jetbrains.anko.doAsync

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class GoalBinder : DataBinder<Goal>() {

    override fun bind(data: Goal, view: View) {
        view as ProgressLine
        doAsync {  }
        view.setPercentage(data.percentage.formatPercentage())
        view.setName(data.name)
        view.setValueText(data.targetValue.toInt())
        view.setProgressColor(R.color.blue_paypal.toColor())
    }
}