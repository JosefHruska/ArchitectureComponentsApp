package cz.pepa.runapp.logic

import android.os.AsyncTask
import android.os.Bundle
import android.support.v4.app.FragmentActivity
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.request.DataReadRequest
import cz.pepa.runapp.app
import cz.pepa.runapp.data.DummyFittnes
import cz.pepa.runapp.data.TodayItem
import cz.pepa.runapp.database.DatabaseWrite
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

    /**
     * Create a [DataSet] to insert data into the History API, and
     * then create and execute a [DataReadRequest] to verify the insertion succeeded.
     * By using an [AsyncTask], we can schedule synchronous calls, so that we can query for
     * data after confirming that our insert was successful. Using asynchronous calls and callbacks
     * would not guarantee that the insertion had concluded before the read request was made.
     * An example of an asynchronous call using a callback can be found in the example
     * on deleting data below.
     */
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
}