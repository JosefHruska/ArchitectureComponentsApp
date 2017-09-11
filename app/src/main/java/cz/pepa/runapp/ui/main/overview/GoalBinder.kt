package cz.pepa.runapp.ui.main.overview

import android.support.constraint.ConstraintLayout
import android.view.View
import cz.pepa.runapp.data.Goal
import cz.pepa.runapp.ui.common.DataBinder
import kotlinx.android.synthetic.main.item_goals.view.*

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class GoalBinder : DataBinder<Goal>() {

    override fun bind(data: Goal, view: View) {
        view as ConstraintLayout
        view.vProgressFirst.progress = 90f
        view.vProgressFirst.secondaryProgress = 100f
        view.vProgressFirst.max = 100f
//
    }
}