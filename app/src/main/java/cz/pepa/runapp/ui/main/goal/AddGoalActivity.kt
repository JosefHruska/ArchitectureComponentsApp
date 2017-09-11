package cz.pepa.runapp.ui.main.goal

import android.support.v7.app.AppCompatActivity
import cz.pepa.runapp.R
import cz.pepa.runapp.ui.base.BaseActivity
import cz.pepa.runapp.ui.base.BaseViewModel
import cz.pepa.runapp.ui.main.group.GroupViewModel
import kotlinx.android.synthetic.main.activity_add_goal.*

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
        vSaveGoal.setOnClickListener{
            (mViewModel as AddGoalViewModel).saveGoal()
            finish()
        }

    }
}