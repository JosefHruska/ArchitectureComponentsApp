package cz.pepa.runapp.logic

import android.arch.lifecycle.MutableLiveData
import com.gojuno.koptional.Optional
import cz.pepa.runapp.R
import cz.pepa.runapp.app
import cz.pepa.runapp.data.Goal
import cz.pepa.runapp.data.GoalData
import cz.pepa.runapp.data.TodayItem
import cz.pepa.runapp.database.DatabaseRead
import cz.pepa.runapp.database.combineLatest

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object GoalLogic {

    val goals = MutableLiveData<Optional<List<Goal>>>()


//    fun loadGoals() {
//        DatabaseRead.goals().observeOn(AndroidSchedulers.mainThread()).subscribe {
//            val goals = it.toSome()
//            formatGoals(goals)
//        }
//    }

    fun loadGoals() {
        combineLatest(DatabaseRead.goals(), DatabaseRead.lastMonth(), {
            goalsData, days ->
            goalsData.forEach {
                when (it.goalType) {
                    GoalData.GoalType.METRIC -> {
//                        formateMetricGoal(it, days)
                    }
                    GoalData.GoalType.FREQUENCY -> {
//                        formatFrequencyGoal(it, days)
                    }
                }
            }
        })

    }

//    fun formatGoals(goalsData: List<GoalData>) {
//
//        goalsData.forEach {
//            val formattedGoal: Goal = Goal()
//            when (it.goalType) {
//                GoalData.GoalType.METRIC -> {
//                    formateMetricGoal(it)
//                }
//            }
//        }
//
//
//    }

    fun formatMetricGoal(goalData: GoalData, days: List<TodayItem>) {
        val goal = Goal()
        val targetValue = goalData.metricObjective?.value ?: 0.0
        goal.targetValue = targetValue
        val goalSubject = when (goalData.metricObjective?.dataTypeName) {
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
        val goalName = when (goalData.metricObjective?.dataTypeName) {
            "com.google.step_count.delta" -> app().getString(R.string.goal_metric_steps, targetValue.toString(), goalTitlePeriod)
            "com.google.distance.delta" -> app().getString(R.string.goal_metric_distance, targetValue.toString(), goalTitlePeriod)
            "com.google.calories.expended" -> app().getString(R.string.goal_metric_calories, targetValue.toString(), goalTitlePeriod)
            else -> app().getString(R.string.goal_metric_calories, targetValue.toString(), goalTitlePeriod) // It may not happen
        }
        goal.name = goalName
        goal.percentage = progressPercentage
        goal.currentValue = progressPercentage * targetValue

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
            GoalSubject.DISTANCE -> daysOfWeek.sumByDouble { it.calories.toDouble() }
            GoalSubject.STEPS -> daysOfWeek.sumByDouble { it.calories.toDouble() }
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