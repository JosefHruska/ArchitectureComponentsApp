package cz.pepa.runapp.ui.main.overview

import android.view.View
import com.app.progresviews.ProgressLine
import cz.pepa.runapp.data.Goal
import cz.pepa.runapp.ui.common.DataBinder
import formatPercentage

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class GoalBinder : DataBinder<Goal>() {

    override fun bind(data: Goal, view: View) {
        view as ProgressLine
        view.setmPercentage(data.progress.formatPercentage())
        view.setmDefText(data.name)
        view.setmValueText(data.value.toInt())
        view.color
    }
}