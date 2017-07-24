package cz.pepa.runapp.logic

import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Goal
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.request.GoalsReadRequest
import cz.pepa.runapp.app
import cz.pepa.runapp.data.DummyFittnes
import cz.pepa.runapp.data.GoalData
import cz.pepa.runapp.data.TodayItem
import cz.pepa.runapp.database.DatabaseRead
import cz.pepa.runapp.database.DatabaseWrite
import io.reactivex.schedulers.Schedulers
import io.stepuplabs.settleup.util.extensions.*
import ld
import lw
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.uiThread
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit


/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object Fit {

    var mClient: GoogleApiClient? = null
    private val REQUEST_OAUTH = 1
    private val DATE_FORMAT = "yyyy.MM.dd HH:mm:ss"

    fun getDummyFitnessData(): List<DummyFittnes> {
        val one = DummyFittnes("Marcel", "Running", 600)
        val two = DummyFittnes("Kuba", "Running", 730)
        val three = DummyFittnes("Pepa", "Running", 350)

        val dummies = listOf<DummyFittnes>(one, two, three)

        return dummies
    }

    /**
     * Build a [GoogleApiClient] that will authenticate the user and allow the application
     * to connect to Fitness APIs. The scopes included should match the scopes your app needs
     * (see documentation for details). Authentication will occasionally fail intentionally,
     * and in those cases, there will be a known resolution, which the OnConnectionFailedListener()
     * can address. Examples of this include the user never having signed in before, or
     * having multiple accounts on the device and needing to specify which account to use, etc.
     */

    fun buildFitnessClient(activity: FragmentActivity) {
        // Create the Google API Client
        mClient = GoogleApiClient.Builder(app())
                .addApi(Fitness.HISTORY_API)
                .addApi(Fitness.SESSIONS_API)
                .addApi(Fitness.GOALS_API)
                .addScope(Scope(Scopes.FITNESS_ACTIVITY_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_BODY_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_NUTRITION_READ_WRITE))
                .addScope(Scope(Scopes.FITNESS_LOCATION_READ_WRITE))
                .addConnectionCallbacks(
                        object : GoogleApiClient.ConnectionCallbacks {
                            override fun onConnected(bundle: Bundle?) {
                                ld("Connected!!!")
                                // Now you can make calls to the Fitness APIs.  What to do?
                                // Look at some data!!
                                updateTodayData()
                                getGoal()
                                checkDaySync()
                            }

                            override fun onConnectionSuspended(i: Int) {
                                // If your connection to the sensor gets lost at some point,
                                // you'll be able to determine the reason and react to it here.
                                if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_NETWORK_LOST) {
                                    ld("Connection lost.  Cause: Network Lost.")
                                } else if (i == GoogleApiClient.ConnectionCallbacks.CAUSE_SERVICE_DISCONNECTED) {
                                    ld("Connection lost.  Reason: Service Disconnected")
                                }
                            }
                        }
                )
                .enableAutoManage(activity, 0) { result ->
                    ld("failed - reason : $result")
                }
                .build()
    }

    fun checkDaySync() {
        DatabaseRead.userInfo().observeOn(Schedulers.computation()).subscribe {
            if (!it.isNullOrNone()) {
                if (it.toSome().lastSync != todayBegin()) {
                    syncMissingDays(it.toSome().lastSync)
                }
            } else {
                syncMissingDays(lastMonth()) /* userInfo/user.id  is not created yet - probably*/
            }
        }
    }

    fun syncMissingDays(from: Long, to: Long = todayBegin()) {
        doAsync {
            val readRequest = getDaysData(from, to)
            val dataReadResult = Fitness.HistoryApi.readData(mClient, readRequest)
            dataReadResult.setResultCallback {
                result ->
                val days = mutableMapOf<Long, TodayItem>()
                result.buckets.forEach {
                    val day = TodayItem()
                    val stepField = it.dataSets[0].dataPoints.find { it.dataType.fields[0].name == "steps" }
                    val caloriesField = it.dataSets[0].dataPoints.find { it.dataType.fields[0].name == "calories" }
                    val distanceField = it.dataSets[0].dataPoints.find { it.dataType.fields[0].name == "distance" }
                    day.steps = stepField?.getValue(stepField.dataType.fields[0])?.asInt() ?: 0
                    day.calories = caloriesField?.getValue(caloriesField.dataType.fields[0])?.asFloat() ?: 0F
                    day.distance = distanceField?.getValue(distanceField.dataType.fields[0])?.asFloat() ?: 0F
                    day.dayStart = it.getStartTime(TimeUnit.MILLISECONDS)
                    val time = day.dayStart
                    days.put(time, day)
                    ld("Dejt is  + ${it.getStartTime(TimeUnit.MILLISECONDS).formatDate()}")
                }
                val lastSyncedDay = result.buckets.last().getStartTime(TimeUnit.MILLISECONDS)
                uiThread {
                    DatabaseWrite.updateMissingDays(days, lastSyncedDay)
                }
            }
        }
    }

    fun updateTodayData() {
        doAsync {
            val today = TodayItem()
            val readRequest = getTodayData()
            val dataReadResult = Fitness.HistoryApi.readData(mClient, readRequest)
            dataReadResult.setResultCallback {
                dataReadResult ->
                for (bucket in dataReadResult.buckets) {
                    bucket.dataSets.forEach {
                        it.dataPoints.forEach {
                            lw("data type : ${it.dataType}")
                            lw("data value : ${it.getValue(it.dataType.fields[0])}")
                            when (it.dataType.fields[0].name) {
                                "steps" -> today.steps = it.getValue(it.dataType.fields[0]).asInt()
                                "calories" -> today.calories = it.getValue(it.dataType.fields[0]).asFloat()
                                "distance" -> today.distance = it.getValue(it.dataType.fields[0]).asFloat()
                            }
                        }
                    }
                }
                lw("Created today item with ${today.steps} steps, ${today.calories} calories and ${today.distance} distance")
                uiThread {
                    DatabaseWrite.updateToday(today)
                }
            }

        }
    }

    fun updateGoal() {
        doAsync {
            val today = TodayItem()
            val readRequest = getTodayData()
            val dataReadResult = Fitness.HistoryApi.readData(mClient, readRequest)
            dataReadResult.setResultCallback {
                dataReadResult ->
                for (bucket in dataReadResult.buckets) {
                    bucket.dataSets.forEach {
                        it.dataPoints.forEach {
                            lw("data type : ${it.dataType}")
                            lw("data value : ${it.getValue(it.dataType.fields[0])}")
                            when (it.dataType.fields[0].name) {
                                "steps" -> today.steps = it.getValue(it.dataType.fields[0]).asInt()
                                "calories" -> today.calories = it.getValue(it.dataType.fields[0]).asFloat()
                                "distance" -> today.distance = it.getValue(it.dataType.fields[0]).asFloat()
                            }
                        }
                    }
                }
                lw("Created today item with ${today.steps} steps, ${today.calories} calories and ${today.distance} distance")
                uiThread {
                    DatabaseWrite.updateToday(today)
                }
            }

        }
    }


    /**
     * Return a [DataReadRequest] for all step count changes in the past week.
     */
    fun getTodayData(): DataReadRequest {
        // [START build_read_data_request]
        // Setting a start and end date using a range of 1 week before this moment.
        val cal = Calendar.getInstance()
        val now = Date()
        cal.time = now
        val endTime = cal.timeInMillis
        cal.add(Calendar.DAY_OF_MONTH, -1)
        val startTime = cal.timeInMillis

        val dateFormat = DateFormat.getDateInstance()
        lw("Range Start: " + dateFormat.format(startTime))
        lw("Range End: " + dateFormat.format(endTime))
        val ds = DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build()
        val readRequest = DataReadRequest.Builder()
                .enableServerQueries()
                .aggregate(ds, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();


        return readRequest

    }

    fun getDaysData(from: Long, to: Long): DataReadRequest {
        val endTime = to
        val startTime = from

        val dateFormat = DateFormat.getDateInstance()
        lw("Range Start: " + dateFormat.format(startTime))
        lw("Range End: " + dateFormat.format(endTime))
        val ds = DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build()
        val readRequest = DataReadRequest.Builder()
                .enableServerQueries()
                .aggregate(ds, DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build();


        return readRequest

    }

    fun getGoal() {
        doAsync {
            Fitness.GoalsApi.readCurrentGoals(
                    mClient,
                    GoalsReadRequest.Builder()
//                            .addDataType(DataType.TYPE_STEP_COUNT_DELTA)
//                            .addDataType(DataType.TYPE_CALORIES_EXPENDED)
//                            .addDataType(DataType.TYPE_DISTANCE_DELTA)
                            .addObjectiveType(Goal.OBJECTIVE_TYPE_FREQUENCY)
                            .build()).setResultCallback {
                val stepGoals = it.goals
                val goals = it.goals.map {
                    val goalData = GoalData()
                    val type: GoalData.GoalType
                    var durationObjective: Goal.DurationObjective? = null
                    var frequencyObjective: Goal.FrequencyObjective? = null
                    var metricObjective: Goal.MetricObjective? = null
                    val recurrenceUnit: GoalData.GoalRecurrence
                    when (it.objectiveType) {
                        Goal.OBJECTIVE_TYPE_FREQUENCY -> {
                            type = GoalData.GoalType.FREQUENCY
                            frequencyObjective = it.frequencyObjective
                        }
                        Goal.OBJECTIVE_TYPE_DURATION -> {
                            type = GoalData.GoalType.DURATION
                            durationObjective = it.durationObjective
                        }
                        else -> {
                            type = GoalData.GoalType.METRIC
                            metricObjective = it.metricObjective
                        }
                    }

                    when (it.recurrence.unit) {
                        1 -> recurrenceUnit = GoalData.GoalRecurrence.DAILY
                        2 -> recurrenceUnit = GoalData.GoalRecurrence.WEEKLY
                        else -> recurrenceUnit = GoalData.GoalRecurrence.MONTHLY
                    }

                    goalData.apply { it.activityName?.let { name = it }; goalType = type; recurrence = recurrenceUnit; recurrencePeriod = it.recurrence.count; startTime = it.getStartTime(Calendar.getInstance(), TimeUnit.MILLISECONDS); endTime = it.getEndTime(Calendar.getInstance(), TimeUnit.MILLISECONDS) }
                    durationObjective?.let { goalData.durationObjective = it }
                    frequencyObjective?.let { goalData.frequencyObjective = it }
                    metricObjective?.let { goalData.metricObjective = it }
                    goalData
                }
                uiThread {
                    ld("size ${stepGoals.size}")
                    ld("name ${stepGoals[0].activityName}")
                    ld("objective type ${stepGoals[0].objectiveType}")
                    ld("metric ${stepGoals[0].metricObjective}")
                    ld("recurence ${stepGoals[0].recurrence}")

                    DatabaseWrite.updateGoals(goals)
                }
            }

            Fitness.GoalsApi.readCurrentGoals(
                    mClient,
                    GoalsReadRequest.Builder()
                            .addDataType(DataType.TYPE_CALORIES_EXPENDED)
                            .build()).setResultCallback {
                val stepGoals = it.goals

            }
        }
    }
}