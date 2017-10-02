package cz.pepa.runapp.ui.base

import android.arch.lifecycle.AndroidViewModel
import android.support.v4.app.LoaderManager
import cz.pepa.runapp.app

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

abstract class BaseViewModel<C: BaseController> : AndroidViewModel(app()) {

    private var mController: C? = null

    fun onViewModelReady(controller: C) {
        mController = controller
    }

    abstract fun onStart()

}