package cz.pepa.runapp.ui.main.goal

import cz.pepa.runapp.R
import cz.pepa.runapp.ui.base.BaseActivity
import cz.pepa.runapp.ui.base.BaseViewModel
import io.stepuplabs.settleup.util.extensions.toText
import kotlinx.android.synthetic.main.activity_add_goal.*
import kotlinx.android.synthetic.main.include_goal_type_card.*
import kotlinx.android.synthetic.main.include_today.view.*

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */


class AddGoalActivity: BaseActivity() {

    override fun getViewModel(): BaseViewModel {
        return AddGoalViewModel()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_add_goal
    }

    override fun initUi() {
        setupAverageCard()
        vSaveGoal.setOnClickListener{
            getModel().saveGoal()
            finish()
        }

    }

    private fun getModel() = mViewModel as AddGoalViewModel

    private fun setupAverageCard() {
        vAverageCard.vTitle.text = R.string.average.toText()

        vAverageCard.vFirst.title = R.string.week.toText()
        vAverageCard.vFirst.value = "1963"

        vAverageCard.vSecond.title = R.string.month.toText()
        vAverageCard.vSecond.value = "1551"

        vAverageCard.vThird.title = R.string.year.toText()
        vAverageCard.vThird.value = "1977"

        vAverageCard.vFourth.title = R.string.overall.toText()
        vAverageCard.vFourth.value = "1653"
    }

    private fun setupGoalType() {
        vActive.setOnClickListener { getModel().activeTypeClicked()}
        vDistance.setOnClickListener { }
        vCalories.setOnClickListener { }
        vSteps.setOnClickListener { }
    }
}