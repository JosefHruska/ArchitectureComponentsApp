package cz.pepa.runapp.ui.main.overview

import android.support.constraint.ConstraintLayout
import android.view.View
import cz.pepa.runapp.data.Goal
import cz.pepa.runapp.utils.extensions.formatReward
import kotlinx.android.synthetic.main.item_goals.view.*

/**
 * Binder for given goals
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

open class GivenGoalBinder() : GoalBinder() {

    override fun bind(data: Goal, view: View) {
        view as ConstraintLayout
        view.vWeekChecker.setMatchedDays(data.matchedDays)

        view.vFirstProgress.calculateAndSetProgress(data.currentValue.toFloat(), data.targetValue.toFloat())
        view.vFirstProgress.setProgressText(data.currentValue.toString())
//        view.vProgressFirst.secondaryProgress = data.currentValue.toFloat()
//        view.vProgressFirst.max = data.targetValue.toFloat()
//        view.vProgressFirst.progressText = data.currentValue.toString()

//        view.vProgressSecond.progress = data.averageValue.toFloat()
//        view.vProgressSecond.max = data.targetValue.toFloat()
//        view.vGoalTitle.text = data.name
//        view.vProgressSecond.progressText = data.averageValue.toString()

//        view.vGoalIcon.loadAvatar(Member().apply { data.createdById })
        view.vReward.text = data.reward.formatReward()
    }
}