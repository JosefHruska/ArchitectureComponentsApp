package cz.pepa.runapp.ui.main.goal

import cz.pepa.runapp.R
import cz.pepa.runapp.data.*
import cz.pepa.runapp.logic.GoalLogic
import cz.pepa.runapp.logic.GoalLogic.calculateGoalRating
import cz.pepa.runapp.ui.base.BaseViewModel
import io.stepuplabs.settleup.util.extensions.removeDecimalValue
import io.stepuplabs.settleup.util.extensions.todayBegin
import io.stepuplabs.settleup.util.extensions.todayPlus100Years

/**
 * View model for [AddGoalActivity]
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class AddGoalViewModel : BaseViewModel<AddGoalController>() {

    lateinit var mAverageResults: MutableMap<Type, List<Float>>

    var mCurrentPerecentage: Int = 50
    val mGoal = NewGoal()


    override fun onViewModelCreated() {
        loadAverageData()
    }

    override fun onViewAttached() {
        super.onViewAttached()
        updateContent()
    }

    fun saveGoal() {
        GoalLogic.addNewGoal(GoalData().apply { name = "First added goal"; goalType = GoalData.GoalType.METRIC; recurrence = GoalData.GoalRecurrence.DAILY; recurrencePeriod = 1; startTime = todayBegin(); endTime = todayPlus100Years(); GoalMetricObjective().apply { goalTypeName = "BOOBIES"; value = 69.0 } })
    }

    private fun loadAverageData() {
        mAverageResults = DummyData.getAverageResults()
    }

    fun caloriesTypeClicked() {
        goalTypeChanged(Type.CALORIES)
    }

    fun distanceTypeClicked() {
        goalTypeChanged(Type.DISTANCE)
    }

    fun stepsTypeClicked() {
        goalTypeChanged(Type.STEPS)
    }

    fun activeTypeClicked() {
        goalTypeChanged(Type.ACTIVE)
    }

    fun targetValueChanged(percentage: Int) {
        if (percentage != mCurrentPerecentage) {
            mCurrentPerecentage = percentage
            mGoal.target.value = GoalLogic.calculateTargetValueFromPercentage(mAverageResults[mGoal.type], percentage)
            updateContent()
        }
    }

    fun unitChanged(unit: FitnessUnit) {
        mGoal.target.unit = unit
        updateContent()
    }

    fun recurrenceChanged(itemId: Int) {
        val recurrence = when (itemId) {
            R.id.weekly -> Recurrence.WEEKLY
            R.id.daily -> Recurrence.DAILY
            R.id.monthly -> Recurrence.MONTHLY
            else -> {
                Recurrence.DAILY
            }
        }
        mGoal.recurrence = recurrence
        updateContent()
    }

    private fun goalTypeChanged(goalType: Type) {
        mGoal.type = goalType
        getController().resetTargetValueSelector()
        updateContent()
    }

    private fun updateContent() {
        getController().seTargetValueSelectorPercentage(mCurrentPerecentage)
        getController().setSelectedType(mGoal.type)
        mAverageResults[mGoal.type]?.let { getController().setAverageValues(it) }
        getController().setupSummaryText(GoalLogic.formatSummaryGoalText(mGoal.type, mGoal.target.value.removeDecimalValue(), mGoal.recurrence))
        getController().setGoalRating(calculateGoalRating(mAverageResults[mGoal.type], mGoal.target.value).toString())
    }
}

data class Target(var value: Float,var unit: FitnessUnit)