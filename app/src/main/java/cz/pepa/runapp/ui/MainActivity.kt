package cz.pepa.runapp.ui

import android.arch.lifecycle.ViewModel
import cz.pepa.runapp.R
import cz.pepa.runapp.ui.base.BaseActivity

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class MainActivity: BaseActivity() {


    override fun getViewModel(): ViewModel {
        return cz.pepa.runapp.MainViewModel()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun initUi() {
        super.initUi()
    }
}