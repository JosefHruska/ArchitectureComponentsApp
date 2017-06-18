package cz.pepa.runapp.ui.base

import android.arch.lifecycle.LifecycleActivity
import android.arch.lifecycle.ViewModel
import android.arch.lifecycle.ViewModelProviders
import android.os.Bundle
import android.os.PersistableBundle

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

abstract class BaseActivity: LifecycleActivity() {

    private lateinit var mViewModel: ViewModel

    abstract fun getViewModel(): ViewModel

    abstract fun getLayoutRes(): Int

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        setupViewModel()
        setContentView(getLayoutRes())
        initUi()
    }

    open fun initUi() {
        // setup UI components here
    }


    private fun setupViewModel() {
       mViewModel = ViewModelProviders.of(this).get(getViewModel()::class.java)
    }
}