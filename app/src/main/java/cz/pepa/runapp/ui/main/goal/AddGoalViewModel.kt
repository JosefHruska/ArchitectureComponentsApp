package cz.pepa.runapp.ui.main.goal

import cz.pepa.runapp.data.GoalData
import cz.pepa.runapp.data.GoalMetricObjective
import cz.pepa.runapp.logic.GoalLogic
import cz.pepa.runapp.ui.base.BaseViewModel
import io.stepuplabs.settleup.util.extensions.todayBegin
import io.stepuplabs.settleup.util.extensions.todayPlus100Years

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class AddGoalViewModel : BaseViewModel() {


    override fun onStart() {
    }

    fun saveGoal() {
        GoalLogic.addNewGoal(GoalData().apply { name = "First added goal"; goalType = GoalData.GoalType.METRIC; recurrence = GoalData.GoalRecurrence.DAILY; recurrencePeriod = 1; startTime = todayBegin(); endTime = todayPlus100Years(); GoalMetricObjective().apply { goalTypeName = "BOOBIES"; value = 69.0 } })

    }
}