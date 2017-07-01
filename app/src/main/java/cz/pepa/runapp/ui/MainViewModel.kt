package cz.pepa.runapp.ui

import android.arch.lifecycle.LiveData
import android.arch.lifecycle.MutableLiveData
import cz.pepa.runapp.data.DummyFittnes
import cz.pepa.runapp.ui.base.BaseViewModel

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class MainViewModel: BaseViewModel() {

    private val dummyFitnessData: LiveData<List<DummyFittnes>> = MutableLiveData<List<DummyFittnes>>()


}