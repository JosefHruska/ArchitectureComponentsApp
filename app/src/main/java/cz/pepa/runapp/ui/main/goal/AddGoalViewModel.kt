package cz.pepa.runapp.ui.main.goal

import cz.pepa.runapp.data.*
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

    val mGoal = NewGoal()


    override fun onStart() {
    }

    fun saveGoal() {
        GoalLogic.addNewGoal(GoalData().apply { name = "First added goal"; goalType = GoalData.GoalType.METRIC; recurrence = GoalData.GoalRecurrence.DAILY; recurrencePeriod = 1; startTime = todayBegin(); endTime = todayPlus100Years(); GoalMetricObjective().apply { goalTypeName = "BOOBIES"; value = 69.0 } })

    }

    fun caloriesTypeClicked() {
        mGoal.type = Type.CALORIES
    }

    fun distanceTypeClicked() {
        mGoal.type = Type.DISTANCE
    }

    fun stepsTypeClicked() {
        mGoal.type = Type.STEPS
    }

    fun activeTypeClicked() {
        mGoal.type = Type.ACTIVE
    }

    fun targetValueChanged(newText: String) {
        mGoal.target.value = newText.toFloat()
    }

    fun unitChanged(unit: FitnessUnit) {
        mGoal.target.unit = unit
    }

    fun reoccurenceChanged(reoccurence: FitnessUnit) {
        mGoal.reoccurence = reoccurence
    }



}

data class Target(var value: Float,var unit: FitnessUnit)