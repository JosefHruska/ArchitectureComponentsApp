package cz.pepa.runapp.ui.base

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import cz.pepa.runapp.data.DummyFittnes

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

abstract class BaseViewModel : ViewModel() {

     val mMembers = MutableLiveData<List<DummyFittnes>>()


}