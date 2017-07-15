package cz.pepa.runapp.ui.main.group

import android.arch.lifecycle.MutableLiveData
import cz.pepa.runapp.data.DummyFittnes
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

class GroupViewModel: BaseViewModel() {

    val mMembers = MutableLiveData<List<DummyFittnes>>()
    val mToday = MutableLiveData<TodayItem>()

    override fun onStart() {
        loadDummyFitness()
        loadToday()
    }

    fun loadToday() {
        DatabaseRead.today(mToday).observeOn(AndroidSchedulers.mainThread()).subscribe({
            mToday.value = it
        },{logError(Throwable(), "FUCK")})
    }

    fun loadDummyFitness() {
        mMembers.value = Fit.getDummyFitnessData()
    }

}