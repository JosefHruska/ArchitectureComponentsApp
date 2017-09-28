package cz.pepa.runapp.ui.main.overview

import android.arch.lifecycle.Observer
import com.gojuno.koptional.Optional
import cz.pepa.runapp.R
import cz.pepa.runapp.data.*
import cz.pepa.runapp.logic.GoalLogic
import cz.pepa.runapp.ui.base.BaseFragment
import cz.pepa.runapp.ui.base.BaseViewModel
import cz.pepa.runapp.ui.common.RecyclerAdapter
import cz.pepa.runapp.ui.main.group.GroupViewModel
import cz.pepa.runapp.utils.extensions.formatCalories
import cz.pepa.runapp.utils.extensions.formatDistance
import cz.pepa.runapp.utils.extensions.formatSteps
import io.stepuplabs.settleup.util.extensions.isNullOrNone
import io.stepuplabs.settleup.util.extensions.toSome
import kotlinx.android.synthetic.main.fragment_overview.*
import kotlinx.android.synthetic.main.fragment_overview.view.*
import kotlinx.android.synthetic.main.include_goals.view.*
import kotlinx.android.synthetic.main.include_today.*
import ld

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class OverviewFragment: BaseFragment() {

    lateinit var mYourGoalsAdapter: RecyclerAdapter<Goal>
    lateinit var mOtherGoalsAdapter: RecyclerAdapter<Goal>

    override fun getContentResId(): Int {
        return R.layout.fragment_overview
    }

    override fun getViewModel(): BaseViewModel {
        return GroupViewModel()
    }

    override fun initUi() {
        setupToday()
        setupGoals()
        subscribeDummyGoals()
       // subscribeGoals()
        hideProgress()
    }

    fun setupToday() {
        val todayObserver = Observer<TodayItem> {
            it?.let {
            vActive.value = "35 min"
            vSteps.value = it.steps.formatSteps()
            vCalories.value = it.calories.formatCalories()
            vDistance.value = it.distance.formatDistance()
            }
        }
        (mViewModel as GroupViewModel).mToday.observe(this, todayObserver)
    }

    fun subscribeGoals() {
        val goalObserver = Observer<Optional<MutableList<Goal>>> {
            if (it != null && !it.isNullOrNone()) {
                mYourGoalsAdapter.setData(it.toSome(), GoalBinder())
            }
        }
        ld("GOALS are :${GoalLogic.goals.value}")
        GoalLogic.goals.observe(this, goalObserver)
    }

    fun setupGoals() {
        vYourGoals.vGoalRecycler.isNestedScrollingEnabled = false
        vYourGoals.vYourGoalsTitle.text = getString(R.string.goals)
        mYourGoalsAdapter = RecyclerAdapter<Goal>(R.layout.item_goals)
        vYourGoals.vGoalRecycler.adapter = mYourGoalsAdapter
        mYourGoalsAdapter.notifyDataSetChanged()

        vOtherGoals.vGoalRecycler.isNestedScrollingEnabled = false
        vOtherGoals.vOtherGoalsTitle.text = getString(R.string.goals)
        mYourGoalsAdapter = RecyclerAdapter<Goal>(R.layout.item_goals)
        vOtherGoals.vGoalRecycler.adapter = mOtherGoalsAdapter
        mYourGoalsAdapter.notifyDataSetChanged()
    }

    fun subscribeDummyGoals() {
        mYourGoalsAdapter.setData(DummyData.getDummyGoals(), GoalBinder())
        mOtherGoalsAdapter.setData(DummyData.getDummyGoals(), GoalBinder())
    }



}