package cz.pepa.runapp.api

import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import io.stepuplabs.settleup.util.extensions.twoYearsAgo
import io.stepuplabs.settleup.util.extensions.todayBegin
import io.stepuplabs.settleup.util.extensions.todayEnd
import java.util.concurrent.TimeUnit

/**
 * Contains all requests for fetching data from Fitness API.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class GoogleFitRequests {

    /**
    * Return a [DataReadRequest] for all step count changes in the past week.
    */
    fun getTodaySteps(): DataReadRequest {
        val endTime = todayEnd()
        val startTime = todayBegin()

        val readRequest = DataReadRequest.Builder()
                .enableServerQueries()
                .aggregate(getStepsDataSource(), DataType.AGGREGATE_STEP_COUNT_DELTA)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()

        return readRequest

    }

    /**
    * Return a [DataReadRequest] for all days between last 2 years and now.
    */
    fun getAllDays(): DataReadRequest {
        val endTime = todayEnd()
        val startTime = twoYearsAgo()

        return DataReadRequest.Builder()
                .enableServerQueries()
                .aggregate(getStepsDataSource(), DataType.AGGREGATE_STEP_COUNT_DELTA)
                .aggregate(DataType.TYPE_DISTANCE_DELTA, DataType.AGGREGATE_DISTANCE_DELTA)
                .aggregate(DataType.TYPE_CALORIES_EXPENDED, DataType.AGGREGATE_CALORIES_EXPENDED)
                .aggregate(DataType.TYPE_ACTIVITY_SEGMENT, DataType.AGGREGATE_ACTIVITY_SUMMARY)
                .bucketByTime(1, TimeUnit.DAYS)
                .setTimeRange(startTime, endTime, TimeUnit.MILLISECONDS)
                .build()
    }

    /**
     * This is specific data source which will return exactly same amount of steps as Google Fit App.
     */
    private fun getStepsDataSource(): DataSource {
        return DataSource.Builder()
                .setAppPackageName("com.google.android.gms")
                .setDataType(DataType.TYPE_STEP_COUNT_DELTA)
                .setType(DataSource.TYPE_DERIVED)
                .setStreamName("estimated_steps")
                .build()
    }
}