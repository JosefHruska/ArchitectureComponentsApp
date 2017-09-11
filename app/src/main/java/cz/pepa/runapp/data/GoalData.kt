package cz.pepa.runapp.data

import com.google.android.gms.fitness.data.Goal

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */
class GoalData() : DatabaseModel() {

    var name: String? = "undefined"
    var goalType: GoalType = GoalType.METRIC
     var recurrence: GoalRecurrence = GoalRecurrence.DAILY
    var recurrencePeriod: Int? = null
    var startTime: Long = 0L
    var endTime: Long = 0L
    var durationObjective: Goal.DurationObjective? = null
    var frequencyObjective: Goal.FrequencyObjective? = null
    var metricObjective: GoalMetricObjective? = null


    enum class GoalType {
        METRIC,
        FREQUENCY,
        DURATION
    }

    enum class GoalRecurrence {
        DAILY,
        WEEKLY,
        MONTHLY
    }

}