package cz.pepa.runapp.ui.main

import android.arch.lifecycle.Observer
import cz.pepa.runapp.R
import cz.pepa.runapp.data.TodayItem
import cz.pepa.runapp.ui.base.BaseFragment
import cz.pepa.runapp.ui.base.BaseViewModel
import cz.pepa.runapp.ui.main.group.GroupViewModel
import kotlinx.android.synthetic.main.include_today.*

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class OverviewFragment: BaseFragment() {

    override fun getContentResId(): Int {
        return R.layout.fragment_overview
    }

    override fun getViewModel(): BaseViewModel {
        return GroupViewModel()
    }

    override fun initUi() {
        setupToday()
        hideProgress()
    }

    fun setupToday() {
        val todayObserver = Observer<TodayItem> {
            it?.let {
            vSteps.value = it.steps.toString()
            vCalories.value = it.calories.toString()
            vDistance.value = it.distance.toString()
            }
        }

        (mViewModel as GroupViewModel).mToday.observe(this, todayObserver)
    }
}