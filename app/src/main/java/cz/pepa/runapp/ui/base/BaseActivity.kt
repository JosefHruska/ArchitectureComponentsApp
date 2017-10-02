package cz.pepa.runapp.ui.base

import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cz.pepa.runapp.logger.Log
import cz.pepa.runapp.logger.LogView
import cz.pepa.runapp.logger.LogWrapper
import cz.pepa.runapp.logger.MessageOnlyLogFilter
import kotlinx.android.synthetic.main.activity_test_fit.*
import ld

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

abstract class BaseActivity<VM: BaseViewModel<C>, C: BaseController>: AppCompatActivity(), BaseController {

    protected lateinit var mViewModel: VM

    abstract fun getViewModel(): VM

    abstract fun getLayoutRes(): Int

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupViewModel()
        setContentView(getLayoutRes())
//        initializeLogging()
        initUi()
    }

    override fun onStart() {
        super.onStart()


    }

    open fun initUi() {
        // setup UI components here
    }


    private fun setupViewModel() {
        mViewModel = ViewModelProviders.of(this).get((getViewModel())::class.java)
        mViewModel.onViewModelReady(this as C)
        mViewModel.onStart()
    }


    /**
     * Initialize a custom log class that outputs both to in-app targets and logcat.
     */
    private fun initializeLogging() {
        // Wraps Android's native log framework.
        val logWrapper = LogWrapper()
        // Using Log, front-end to the logging chain, emulates android.util.log method signatures.
        Log.setLogNode(logWrapper)
        // Filter strips out everything except the message text.
        val msgFilter = MessageOnlyLogFilter()
        logWrapper.setNext(msgFilter)
        // On screen logging via a customized TextView.
        val logView = sample_logview as LogView

        // Fixing this lint error adds logic without benefit.

//        logView.setTextAppearance(this, R.style.Log)

        logView.setBackgroundColor(Color.WHITE)
        msgFilter.setNext(logView)
        ld( "Ready.")
    }
}