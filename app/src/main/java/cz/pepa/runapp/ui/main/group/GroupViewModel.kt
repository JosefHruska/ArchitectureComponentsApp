package cz.pepa.runapp.ui.main.group

import android.arch.lifecycle.MutableLiveData
import cz.pepa.runapp.data.DummyFitness
import cz.pepa.runapp.data.Goal
import cz.pepa.runapp.data.TodayItem
import cz.pepa.runapp.database.DatabaseRead
import cz.pepa.runapp.logic.Fit
import cz.pepa.runapp.ui.base.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import logError

/**
 * View model for [GroupTabFragment]
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

open class GroupViewModel<C: GroupTabController>: BaseViewModel<C>() {

    val mMembers = MutableLiveData<List<DummyFitness>>()
    val mToday = MutableLiveData<TodayItem>()
    val mGoal = MutableLiveData<List<Goal>>()

    override fun onViewAttached() {
        loadDummyFitness()
        loadToday()
    }

    fun loadToday() {
        DatabaseRead.today().observeOn(AndroidSchedulers.mainThread()).subscribe({
            mToday.value = it.toNullable()
        },{logError(Throwable(), "DUMMY")})
    }

    fun loadDummyFitness() {
        mMembers.value = Fit.getDummyFitnessData()
    }

}