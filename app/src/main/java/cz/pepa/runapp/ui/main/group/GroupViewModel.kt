package cz.pepa.runapp.ui.main.group

import android.arch.lifecycle.MutableLiveData
import android.os.Bundle
import android.support.v4.content.Loader
import cz.pepa.runapp.data.DummyFittnes
import cz.pepa.runapp.data.Goal
import cz.pepa.runapp.data.TodayItem
import cz.pepa.runapp.database.DatabaseRead
import cz.pepa.runapp.logic.Fit
import cz.pepa.runapp.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import logError

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

open class GroupViewModel<C: GroupTabController>: BaseViewModel<C>() {

    val mMembers = MutableLiveData<List<DummyFittnes>>()
    val mToday = MutableLiveData<TodayItem>()
    val mGoal = MutableLiveData<List<Goal>>()


    override fun onStart() {
        loadDummyFitness()
        loadToday()

    }

    fun loadToday() {
        DatabaseRead.today().observeOn(AndroidSchedulers.mainThread()).subscribe({
            mToday.value = it.toNullable()
        },{logError(Throwable(), "FUCK")})
//        DatabaseRead.goals().observeOn(AndroidSchedulers.mainThread()).subscribe({
//            mGoal.value = it.toNullable()
//        },{logError(Throwable(), "FUCK")})
    }

    fun loadDummyFitness() {
        mMembers.value = Fit.getDummyFitnessData()
    }

}