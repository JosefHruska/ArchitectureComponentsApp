package cz.pepa.runapp.ui.main.overview

import android.arch.lifecycle.Observer
import cz.pepa.runapp.R
import cz.pepa.runapp.data.Goal
import cz.pepa.runapp.data.TodayItem
import cz.pepa.runapp.ui.base.BaseFragment
import cz.pepa.runapp.ui.base.BaseViewModel
import cz.pepa.runapp.ui.common.RecyclerAdapter
import cz.pepa.runapp.ui.main.group.GroupViewModel
import cz.pepa.runapp.utils.extensions.formatCalories
import cz.pepa.runapp.utils.extensions.formatDistance
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.fragment_overview.view.*
import kotlinx.android.synthetic.main.include_goals.view.*
import kotlinx.android.synthetic.main.include_today.*

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class OverviewFragment: BaseFragment() {

    lateinit var mGoalsAdapter: RecyclerAdapter<Goal>

    override fun getContentResId(): Int {
        return R.layout.fragment_overview
    }

    override fun getViewModel(): BaseViewModel {
        return GroupViewModel()
    }

    override fun initUi() {
        setupToday()
        setupGoals()
        subscribeGoals()
        hideProgress()
    }

    fun setupToday() {
        val todayObserver = Observer<TodayItem> {
            it?.let {
            vSteps.value = it.steps.toString()
            vCalories.value = it.calories.formatCalories()
            vDistance.value = it.distance.formatDistance()
            }
        }
        (mViewModel as GroupViewModel).mToday.observe(this, todayObserver)
    }

    fun subscribeGoals() {
        val goalObserver = Observer<List<Goal>> {
            it?.let {
                mGoalsAdapter.setData(it, GoalBinder())
            }
        }
        (mViewModel as GroupViewModel).mGoal.observe(this, goalObserver)
    }

    fun setupGoals() {
        vGoals.vGoalRecycler.isNestedScrollingEnabled = false
        vGoalCard.vGoalsTitle.text = getString(R.string.goals)
        mGoalsAdapter = RecyclerAdapter<Goal>(R.layout.item_goals)
        vGoals.vGoalRecycler.adapter = mGoalsAdapter
        mGoalsAdapter.notifyDataSetChanged()
    }
}