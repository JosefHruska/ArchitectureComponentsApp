package cz.pepa.runapp.ui

import android.graphics.Color
import android.os.AsyncTask
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import com.google.android.gms.common.Scopes
import com.google.android.gms.common.api.GoogleApiClient
import com.google.android.gms.common.api.Scope
import com.google.android.gms.fitness.Fitness
import com.google.android.gms.fitness.data.DataSet
import com.google.android.gms.fitness.data.DataSource
import com.google.android.gms.fitness.data.DataType
import com.google.android.gms.fitness.data.Field
import com.google.android.gms.fitness.request.DataDeleteRequest
import com.google.android.gms.fitness.request.DataReadRequest
import com.google.android.gms.fitness.result.DataReadResult
import cz.pepa.runapp.R
import cz.pepa.runapp.logger.Log
import cz.pepa.runapp.logger.LogView
import cz.pepa.runapp.logger.LogWrapper
import cz.pepa.runapp.logger.MessageOnlyLogFilter
import cz.pepa.runapp.logic.Fit
import cz.pepa.runapp.ui.base.BaseActivity
import cz.pepa.runapp.ui.base.BaseViewModel
import io.stepuplabs.settleup.util.ld
import kotlinx.android.synthetic.main.activity_test_fit.*
import java.text.DateFormat
import java.util.*
import java.util.concurrent.TimeUnit

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class MainActivity: BaseActivity() {


    override fun getViewModel(): BaseViewModel {
        return MainViewModel()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun initUi() {

    }





    /**
     * Track whether an authorization activity is stacking over the current activity, i.e. when
     * a known auth error is being resolved, such as showing the account chooser or presenting a
     * consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private val AUTH_PENDING = "auth_state_pending"
    private var authInProgress = false



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_fit)

        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING)
        }

        Fit.buildFitnessClient()
    }



    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        // Inflate the menu; this adds items to the action bar if it is present.
//        menuInflater.inflate(R.menu.main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
//        val id = item.itemId
//        if (id == R.id.action_delete_data) {
//            deleteData()
//            return true
//        } else if (id == R.id.action_update_data) {
//            val intent = Intent(this@MainActivity, MainActivity2::class.java)
//            this@MainActivity.startActivity(intent)
//        }
        return super.onOptionsItemSelected(item)
    }

}

