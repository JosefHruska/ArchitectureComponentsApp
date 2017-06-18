package cz.pepa.runapp.ui.base

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import java.lang.reflect.Member

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

abstract class BaseViewModel : ViewModel() {

    private val mMembers = MutableLiveData<List<Member>>()

}