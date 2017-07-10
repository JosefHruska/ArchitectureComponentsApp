package cz.pepa.runapp.ui.base

import android.arch.lifecycle.ViewModel

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

abstract class BaseViewModel : ViewModel() {

    abstract fun onStart()

}