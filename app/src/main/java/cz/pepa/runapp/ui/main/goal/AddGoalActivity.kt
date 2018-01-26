package cz.pepa.runapp.ui.main.goal

import android.view.MenuItem
import com.marcinmoskala.arcseekbar.ProgressListener
import cz.pepa.runapp.R
import cz.pepa.runapp.data.Type
import cz.pepa.runapp.extensions.setBackgroundDrawableColor
import cz.pepa.runapp.ui.base.BaseActivity
import io.stepuplabs.settleup.util.extensions.*
import kotlinx.android.synthetic.main.activity_add_goal.*
import kotlinx.android.synthetic.main.include_goal_summary.*
import kotlinx.android.synthetic.main.include_goal_type_card.*
import kotlinx.android.synthetic.main.include_target_value.*
import kotlinx.android.synthetic.main.include_today.view.*

/**
 * Allows to add new personal goal
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class AddGoalActivity: BaseActivity<AddGoalViewModel, AddGoalController>(), AddGoalController {

    override fun getViewModel(): AddGoalViewModel {
        return AddGoalViewModel()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_add_goal
    }

    override fun showCloseButton() = true

    override fun initUi() {
        setupAverageCard()
        setupGoalType()
        title = R.string.new_goal.toText()
        vSaveGoal.setOnClickListener{
            getModel().saveGoal()
            finish()
        }
        vSaveGoal.setBackgroundColorWithRipple(R.color.primary.toColor())


    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
    }

    override fun setSelectedType(selectedType: Type) {
        when (selectedType) {
            Type.ACTIVE -> {
                vActive.setBackgroundDrawableColor(R.color.primary.toColor())

                vDistance.setBackgroundDrawableColor(R.color.gray_3.toColor())
                vSteps.setBackgroundDrawableColor(R.color.gray_3.toColor())
                vCalories.setBackgroundDrawableColor(R.color.gray_3.toColor())
            }
            Type.DISTANCE -> {
                vDistance.setBackgroundDrawableColor(R.color.primary.toColor())
                vCalories.setBackgroundDrawableColor(R.color.gray_3.toColor())
                vActive.setBackgroundDrawableColor(R.color.gray_3.toColor())
                vSteps.setBackgroundDrawableColor(R.color.gray_3.toColor())
            }
            Type.CALORIES -> {
                vCalories.setBackgroundDrawableColor(R.color.primary.toColor())

                vSteps.setBackgroundDrawableColor(R.color.gray_3.toColor())
                vActive.setBackgroundDrawableColor(R.color.gray_3.toColor())
                vDistance.setBackgroundDrawableColor(R.color.gray_3.toColor())
            }
            Type.STEPS -> {
                vSteps.setBackgroundDrawableColor(R.color.primary.toColor())

                vDistance.setBackgroundDrawableColor(R.color.gray_3.toColor())
                vCalories.setBackgroundDrawableColor(R.color.gray_3.toColor())
                vActive.setBackgroundDrawableColor(R.color.gray_3.toColor())
            }
        }
    }

    override fun setGoalRating(rating: String) {
        vRating.text = rating
    }

    override fun seTargetValueSelectorPercentage(percentage: Int) {
        vTargetValueSelector.progress = percentage
        vTargetValueSelector.onProgressChangedListener = ProgressListener { getModel().targetValueChanged(it) }
    }

    override fun setAverageValues(values: List<Float>) {
        vAverageCard.vFirst.value = values[0].toString()
        vAverageCard.vSecond.value = values[1].toString()
        vAverageCard.vThird.value = values[2].toString()
        vAverageCard.vFourth.value = values[3].toString()
    }

    override fun resetTargetValueSelector() {
        vTargetValueSelector.progress = 50 // Default value is 50%
    }

    private fun getModel() = mViewModel

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
        vDistance.setOnClickListener { getModel().distanceTypeClicked() }
        vCalories.setOnClickListener { getModel().caloriesTypeClicked()}
        vSteps.setOnClickListener {getModel().stepsTypeClicked() }
    }

    override fun setupSummaryText(text: String) {
        vSummaryText.setOnClickListener(text, text.indexOf(" a "), text.length, {vSummaryText.popupMenuOnClick(R.menu.recurrence, {getModel().recurrenceChanged(it)})})
    }
}