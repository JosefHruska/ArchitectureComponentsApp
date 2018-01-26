package cz.pepa.runapp.ui.main

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProvider
import com.gojuno.koptional.Optional
import cz.pepa.runapp.data.*
import cz.pepa.runapp.database.DatabaseRead
import cz.pepa.runapp.ui.base.BaseViewModel
import io.reactivex.Flowable
import io.reactivex.android.schedulers.AndroidSchedulers

/**
 * View model for [MainActivity]
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class MainViewModel: BaseViewModel<MainController>() {

    private val mGroups = MutableLiveData<List<Group>>()
    private val mTabs = MutableLiveData<MutableList<Tab>>()
    private val mUser = MutableLiveData<User>()

    override fun onViewAttached() {
        loadGroups()
        loadUser()
    }

    private fun loadGroups() {
        val groups = DummyData.getGroups()
        mGroups.value = groups
        val tabs = mutableListOf( Tab("YOU", "OVERVIEW", 4777777 ))
        tabs.addAll(groups.map { Tab(it.name, it.id, 4777744) })
        mTabs.value = tabs
    }

    private fun loadUser() {
        DatabaseRead.user().observeOn(AndroidSchedulers.mainThread()).subscribe {
            mUser.value = it.toNullable()
        }
    }

    fun getTabs() : MutableLiveData<MutableList<Tab>> {
        return mTabs
    }

    fun getUser() : MutableLiveData<User> {
        return mUser
    }

//    class Factory: ViewModelFactory<MainViewModel>() {
//
//        override fun create(): MainViewModel {
//            return MainViewModel()
//        }
//    }



}