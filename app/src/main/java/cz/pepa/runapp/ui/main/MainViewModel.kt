package cz.pepa.runapp.ui.main

import android.arch.lifecycle.MutableLiveData
import cz.pepa.runapp.data.DummyData
import cz.pepa.runapp.data.Group
import cz.pepa.runapp.data.Tab
import cz.pepa.runapp.ui.base.BaseViewModel

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class MainViewModel: BaseViewModel() {

    private val mGroups = MutableLiveData<List<Group>>()
    private val mTabs = MutableLiveData<MutableList<Tab>>()

    override fun onStart() {
        loadGroups()
    }

    fun loadGroups() {
        val groups = DummyData.getGroups()
        mGroups.value = groups
        val tabs = mutableListOf( Tab("YOU", "OVERVIEW", 4777777 ))
        tabs.addAll(groups.map { Tab(it.name, it.id, 4777744) })
        mTabs.value = tabs
    }

    fun getTabs() : MutableLiveData<MutableList<Tab>> {
        return mTabs
    }

}