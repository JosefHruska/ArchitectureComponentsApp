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
 * Logic related to goal objects
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object GoalLogic {

    val goals = MutableLiveData<Optional<MutableList<Goal>>>()

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

    fun calculateTargetValueFromPercentage(averageValues: List<Float>?, targetSelectorPercentage: Int): Float {
        val monthlyAverage = averageValues!![2]// TODO Fix !!
        val minimumGoalValue = 0.7 * monthlyAverage
        val maximumGoalValue = 2 * monthlyAverage
        val targetValueSpectrum = maximumGoalValue - minimumGoalValue
        val targetValue = (targetValueSpectrum * (0.01 * targetSelectorPercentage)) + minimumGoalValue
        return targetValue.toFloat()
    }

    fun calculateGoalRating(averageValues: List<Float>?, targetValue: Float): Rating {
        val monthlyAverage = averageValues!![2] // TODO Fix !!
        return if (targetValue <= monthlyAverage) {
            Rating.EASY
        } else if (monthlyAverage < targetValue && monthlyAverage * 1.5 >= targetValue) {
            Rating.GOOD
        } else if (monthlyAverage * 1.5 < targetValue && monthlyAverage * 1.8 >= targetValue) {
            Rating.DIFFICULT
        } else {
            Rating.INSANE
        }
    }

    private fun formatMetricGoal(goalData: GoalData, days: List<TodayItem>): Goal {
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

    private fun calculateGoalProgress(daysOfWeek: List<TodayItem>, goalSubject: GoalSubject, targetValue: Double): Double {
        val overallValue = when (goalSubject) {
            GoalSubject.CALORIES -> daysOfWeek.sumByDouble { it.calories.toDouble() }
            GoalSubject.DISTANCE -> daysOfWeek.sumByDouble { it.distance.toDouble() }
            GoalSubject.STEPS -> daysOfWeek.sumByDouble { it.steps.toDouble() }
        }
        val percentageProgess: Double = overallValue / (targetValue / 100)
        return percentageProgess
    }
}

enum class GoalSubject {
    CALORIES,
    DISTANCE,
    STEPS
}