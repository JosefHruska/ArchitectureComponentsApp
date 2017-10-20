package cz.pepa.runapp.ui.base

import android.annotation.SuppressLint
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Color
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.Menu
import android.view.MenuItem
import android.support.v7.widget.Toolbar
import cz.pepa.runapp.R
import cz.pepa.runapp.logger.Log
import cz.pepa.runapp.logger.LogView
import cz.pepa.runapp.logger.LogWrapper
import cz.pepa.runapp.logger.MessageOnlyLogFilter
import cz.pepa.runapp.ui.main.MainController
import cz.pepa.runapp.ui.main.MainViewModel
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

    open fun getOptionsMenuRes() = -1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(getLayoutRes())
        setupViewModel()
//        initializeLogging()
        setupToolbar()
        initUi()
    }

    override fun onStart() {
        super.onStart()
    }

    open fun initUi() {
        // setup UI components here
    }

    private fun setupViewModel() {
        mViewModel = ViewModelProviders.of(this, ViewModelFactory()).get((getViewModel()::class.java))
        mViewModel.onViewModelReady(this as C)
    }

    open fun showCloseButton() = false

    @SuppressLint("PrivateResource")
    open fun setupToolbar() {
        setSupportActionBar(findViewById(R.id.vToolbar))
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (showCloseButton()) {
            supportActionBar?.setHomeAsUpIndicator(R.drawable.ic_close)
            supportActionBar?.setHomeActionContentDescription(R.string.discard)
        } else {
            supportActionBar?.setHomeActionContentDescription(R.string.abc_action_bar_up_description)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val menuRes = getOptionsMenuRes()
        if (menuRes != -1) {
            menuInflater.inflate(menuRes, menu)
        }
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            android.R.id.home -> {
                finish()
                return true
            }
        }
        return false
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