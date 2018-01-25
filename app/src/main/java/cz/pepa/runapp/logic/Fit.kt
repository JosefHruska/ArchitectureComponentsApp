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
import cz.pepa.runapp.data.GoalMetricObjective
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



    fun getDummyFitnessData(): List<DummyFittnes> {
        val one = DummyFittnes("Marcel", "Running", 600)
        val two = DummyFittnes("Kuba", "Running", 730)
        val three = DummyFittnes("Pepa", "Running", 350)

        val dummies = listOf<DummyFittnes>(one, two, three)

        return dummies
    }


}