package cz.pepa.runapp.logic

import android.arch.lifecycle.MutableLiveData
import android.text.Spanned
import com.gojuno.koptional.Optional
import cz.pepa.runapp.R
import cz.pepa.runapp.app
import cz.pepa.runapp.data.*
import cz.pepa.runapp.database.DatabaseRead
import cz.pepa.runapp.database.DatabaseWrite
import cz.pepa.runapp.database.combineLatest
import io.reactivex.android.schedulers.AndroidSchedulers
import io.stepuplabs.settleup.util.extensions.formatHtml
import io.stepuplabs.settleup.util.extensions.toBold
import io.stepuplabs.settleup.util.extensions.todayBegin
import ld
import java.util.concurrent.TimeUnit

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object GoalLogic {

    val goals = MutableLiveData<Optional<MutableList<Goal>>>()

//    fun loadGoals() {
//        DatabaseRead.goals().observeOn(AndroidSchedulers.mainThread()).subscribe {
//            val goals = it.toSome()
//            formatGoals(goals)
//        }
//    }

    fun loadGoals() {
        combineLatest(DatabaseRead.goals(), DatabaseRead.lastMonth(), {
            goalsData, days ->
            val goals = mutableListOf<Goal>()
            goalsData.forEach {
                when (it.goalType) {
                    GoalData.GoalType.METRIC -> {
                        goals.add(formatMetricGoal(it, days.reversed()))
                    }
                    GoalData.GoalType.FREQUENCY -> {
//                        formatFrequencyGoal(it, days)
                    }
                    GoalData.GoalType.DURATION -> {
//                        formatDurationGoal(it, days)
                    }
                }
            }

            goals
        }).observeOn(AndroidSchedulers.mainThread()).subscribe {
            GoalLogic.goals.value = it
            ld("WATAFAKA is : ${it.toNullable()?.size}")
        }

    }

    fun addNewGoal(goalData: GoalData) {
        DatabaseWrite.addGoal(goalData, goals.value?.toNullable()?.size ?: throw RuntimeException("WUT"))
    }

    fun removeGoal(goalOrder: Int = 4) {
        DatabaseWrite.deleteGoal(goalOrder)
    }

    fun formatDurationGoal(goalData: GoalData, days: List<TodayItem>) {
        val goal = Goal()
        val targetValue = (goalData.durationObjective?.getDuration(TimeUnit.MINUTES)?.toDouble() ?: 0.0)
        goal.targetValue = targetValue
        val goalSubject = when (goalData.durationObjective?.toString()) {
            "com.google.step_count.delta" -> GoalSubject.STEPS
            "com.google.distance.delta" -> GoalSubject.DISTANCE
            "com.google.calories.expended" -> GoalSubject.CALORIES
            else -> GoalSubject.CALORIES // It may not happen
        }
        var progressPercentage: Double? = null
        var goalTitlePeriod: String? = null
        when (goalData.recurrence) {
            GoalData.GoalRecurrence.DAILY -> {
                progressPercentage = calculateGoalProgress(listOf(days.find { it.dayStart == todayBegin() }!!), goalSubject, targetValue)
                goalTitlePeriod = app().getString(R.string.recurrence_day)
            }
            GoalData.GoalRecurrence.WEEKLY -> {
                progressPercentage = calculateGoalProgress(days.take(7), goalSubject, targetValue)
                goalTitlePeriod = app().getString(R.string.recurrence_week)
            }
            GoalData.GoalRecurrence.MONTHLY -> {
                progressPercentage = calculateGoalProgress(days.take(30), goalSubject, targetValue)
                goalTitlePeriod = app().getString(R.string.recurrence_month)
            }
        }
        val goalName: Spanned = when (goalData.metricObjective?.goalTypeName) {
            "com.google.step_count.delta" -> app().getString(R.string.goal_metric_steps, targetValue.toString().toBold(), goalTitlePeriod.toBold()).formatHtml()
            "com.google.distance.delta" -> app().getString(R.string.goal_metric_distance, targetValue.toString().toBold(), goalTitlePeriod.toBold()).formatHtml()
            "com.google.calories.expended" -> app().getString(R.string.goal_metric_calories, targetValue.toString().toBold(), goalTitlePeriod.toBold()).formatHtml()
            else -> app().getString(R.string.goal_metric_calories, targetValue.toString().toBold(), goalTitlePeriod.toBold()).formatHtml() // It may not happen
        }
        goal.name = goalName
        goal.percentage = progressPercentage
        goal.currentValue = progressPercentage * targetValue

    }

    fun formatMetricGoal(goalData: GoalData, days: List<TodayItem>): Goal {
        val goal = Goal()
        val targetValue = goalData.metricObjective?.value ?: 0.0
        goal.targetValue = targetValue
        val goalSubject = when (goalData.metricObjective?.goalTypeName) {
            "com.google.step_count.delta" -> GoalSubject.STEPS
            "com.google.distance.delta" -> GoalSubject.DISTANCE
            "com.google.calories.expended" -> GoalSubject.CALORIES
            else -> GoalSubject.CALORIES // It may not happen
        }
        var progressPercentage: Double? = null
        var goalTitlePeriod: String? = null
        when (goalData.recurrence) {
            GoalData.GoalRecurrence.DAILY -> {
                progressPercentage = calculateGoalProgress(days.take(1), goalSubject, targetValue)
                goalTitlePeriod = app().getString(R.string.recurrence_day)
            }
            GoalData.GoalRecurrence.WEEKLY -> {
                progressPercentage = calculateGoalProgress(days.take(7), goalSubject, targetValue)
                goalTitlePeriod = app().getString(R.string.recurrence_week)
            }
            GoalData.GoalRecurrence.MONTHLY -> {
                progressPercentage = calculateGoalProgress(days.take(30), goalSubject, targetValue)
                goalTitlePeriod = app().getString(R.string.recurrence_month)
            }
        }
        val goalName: Spanned = when (goalData.metricObjective?.goalTypeName) {
            "com.google.step_count.delta" -> app().getString(R.string.goal_metric_steps, targetValue.toString().toBold(), goalTitlePeriod.toBold()).formatHtml()
            "com.google.distance.delta" -> app().getString(R.string.goal_metric_distance, targetValue.toString().toBold(), goalTitlePeriod.toBold()).formatHtml()
            "com.google.calories.expended" -> app().getString(R.string.goal_metric_calories, targetValue.toString().toBold(), goalTitlePeriod.toBold()).formatHtml()
            else -> app().getString(R.string.goal_metric_calories, targetValue.toString().toBold(), goalTitlePeriod.toBold()).formatHtml()// It may not happen
        }
        goal.name = goalName
        goal.percentage = progressPercentage
        goal.currentValue = progressPercentage * targetValue
        return goal

    }

//    fun formatFrequencyGoal(goalData: GoalData, days: List<TodayItem>) {
//        val goal = Goal()
//        val targetValue = (goalData.frequencyObjective?.frequency ?: 0).toDouble()
//        goal.targetValue = targetValue
//        val goalSubject = when (goalData.frequencyObjective?.dataTypeName) {
//            "com.google.step_count.delta" -> GoalSubject.STEPS
//            "com.google.distance.delta" -> GoalSubject.DISTANCE
//            "com.google.calories.expended" -> GoalSubject.CALORIES
//            else -> GoalSubject.CALORIES // It may not happen
//        }
//        var progressPercentage: Double? = null
//        var goalTitlePeriod: String? = null
//        when (goalData.recurrence) {
//            GoalData.GoalRecurrence.DAILY -> {
//                progressPercentage = calculateGoalProgress(days.take(1), goalSubject, targetValue)
//                goalTitlePeriod = app().getString(R.string.recurrence_day)
//            }
//            GoalData.GoalRecurrence.WEEKLY -> {
//                progressPercentage = calculateGoalProgress(days.take(7), goalSubject, targetValue)
//                goalTitlePeriod = app().getString(R.string.recurrence_week)
//            }
//            GoalData.GoalRecurrence.MONTHLY -> {
//                progressPercentage = calculateGoalProgress(days.take(30), goalSubject, targetValue)
//                goalTitlePeriod = app().getString(R.string.recurrence_month)
//            }
//        }
//        val goalName = when (goalData.metricObjective?.dataTypeName) {
//            "com.google.step_count.delta" -> app().getString(R.string.goal_metric_steps, targetValue.toString(), goalTitlePeriod)
//            "com.google.distance.delta" -> app().getString(R.string.goal_metric_distance, targetValue.toString(), goalTitlePeriod)
//            "com.google.calories.expended" -> app().getString(R.string.goal_metric_calories, targetValue.toString(), goalTitlePeriod)
//            else -> app().getString(R.string.goal_metric_calories, targetValue.toString(), goalTitlePeriod) // It may not happen
//        }
//        goal.name = goalName
//        goal.percentage = progressPercentage
//        goal.currentValue = progressPercentage * targetValue
//
//    }


    fun calculateGoalProgress(daysOfWeek: List<TodayItem>, goalSubject: GoalSubject, targetValue: Double): Double {
        val overallValue = when (goalSubject) {
            GoalSubject.CALORIES -> daysOfWeek.sumByDouble { it.calories.toDouble() }
            GoalSubject.DISTANCE -> daysOfWeek.sumByDouble { it.distance.toDouble() }
            GoalSubject.STEPS -> daysOfWeek.sumByDouble { it.steps.toDouble() }
        }
        val percentageProgess: Double = overallValue / (targetValue / 100)
        return percentageProgess
    }

    fun formatSummaryGoalText(type: Type, value: Float, recurrence: Recurrence): String {
        val value = value.toString()
        val recurrenceText =
                when (recurrence) {
                    Recurrence.DAILY -> app().getString(R.string.recurrence_day)
                    Recurrence.WEEKLY -> app().getString(R.string.recurrence_week)
                    Recurrence.MONTHLY -> app().getString(R.string.recurrence_month)
                    else -> {"ERROR"}
                }
        return when (type) {
            Type.STEPS -> app().getString(R.string.add_goal_metric_steps, value, recurrenceText)
            Type.ACTIVE -> app().getString(R.string.add_goal_metric_active, value, recurrenceText)
            Type.CALORIES -> app().getString(R.string.add_goal_metric_calories, value, recurrenceText)
            Type.DISTANCE -> app().getString(R.string.add_goal_metric_distance, value, recurrenceText)
        }
    }

    fun formatGoalRating(goalRating: Rating): String {
        return when (goalRating) {
            Rating.EASY -> app().getString(R.string.rating_easy)
            Rating.GOOD -> app().getString(R.string.rating_good)
            Rating.DIFFICULT -> app().getString(R.string.rating_difficult)
            Rating.INSANE -> app().getString(R.string.rating_insane)
        }
    }

    fun calculateGoalRating(averageValues: List<Float>, newValue: Float): Rating {
        val monthlyAverage = averageValues[2]
       return when {
            monthlyAverage > newValue -> Rating.EASY
            (monthlyAverage * 1.1) <= newValue -> Rating.GOOD
            (monthlyAverage * 2) <= newValue -> Rating.DIFFICULT
            else -> Rating.INSANE
        }
    }

}


enum class GoalSubject {
    CALORIES,
    DISTANCE,
    STEPS
}