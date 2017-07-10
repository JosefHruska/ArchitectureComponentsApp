package cz.pepa.runapp.ui.main

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import cz.pepa.runapp.R
import cz.pepa.runapp.data.Tab
import cz.pepa.runapp.extensions.askForPermission
import cz.pepa.runapp.extensions.isPermissionGranted
import cz.pepa.runapp.logger.Log
import cz.pepa.runapp.logic.Fit
import cz.pepa.runapp.ui.base.BaseActivity
import cz.pepa.runapp.ui.base.BaseViewModel
import cz.pepa.runapp.ui.common.Ids
import io.stepuplabs.settleup.util.extensions.popupMenuOnClick
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_drawer_header.*
import kotlinx.android.synthetic.main.include_drawer_header.view.*
import org.jetbrains.anko.*

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class MainActivity: BaseActivity() {

    /**
     * Track whether an authorization activity is stacking over the current activity, i.e. when
     * a known auth error is being resolved, such as showing the account chooser or presenting a
     * consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private val AUTH_PENDING = "auth_state_pending"
    private var authInProgress = false

    private lateinit var vHeader: View
    private lateinit var mViewPagerAdapter: MainViewPagerAdapter


    override fun getViewModel(): BaseViewModel {
        return MainViewModel()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Ids.PERMISSION_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Fit.buildFitnessClient(this)
            } else {

            }
        }
    }

    override fun initUi() {
        setupDrawer()
        setupViewPager()
        setupGroups()
        initGoogleFit()
    }


    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING)
        }


    }

    private fun initGoogleFit() {
        if (!isPermissionGranted(android.Manifest.permission.ACCESS_FINE_LOCATION)) {
            askForPermission(android.Manifest.permission.ACCESS_FINE_LOCATION, Ids.PERMISSION_FINE_LOCATION)
        } else {
            Fit.buildFitnessClient(this)
        }
    }

    private fun setupDrawer() {
        vHeader = vNavigationDrawer.getHeaderView(0)
        val toggle: ActionBarDrawerToggle = object : ActionBarDrawerToggle(this, vDrawer, vToolbar, R.string.desc_open_drawer, R.string.desc_close_drawer) {
            override fun onDrawerOpened(drawerView: View?) {
                super.onDrawerOpened(drawerView)
                if (!vDrawerPopup.hasOnClickListeners()) {
                    vDrawerPopup.popupMenuOnClick(R.menu.drawer_sign_out, {
                        vDrawer.closeDrawers()
//                        signOut()
                    })
                }
            }
        }
        vNavigationDrawer.setNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.overview -> {
                }
                R.id.my_groups -> {
                }
                R.id.new_group -> {
                }
                R.id.join_nearby_group -> {

                }
                R.id.feedback -> {

                }
                R.id.about -> {
                }
            }
            vDrawer.closeDrawers()
            true
        }
        vDrawer.addDrawerListener(toggle)
        vHeader.vPlans.setOnClickListener {
//            startActivity<PlansActivity>()
            vDrawer.closeDrawers()
        }
        toggle.syncState()
    }

    private fun setupViewPager() {
        vTabs.setupWithViewPager(vViewPager)
        Tabs.setListener( object : Tabs.Listener {
            override fun allTabsChanged() {
                mViewPagerAdapter.notifyDataSetChanged()
            }

            override fun topChanged(position: Int) {
                Log.e("FUCK", "YEAH")
            }
        })
        mViewPagerAdapter = MainViewPagerAdapter(supportFragmentManager) {}
        vViewPager.adapter = mViewPagerAdapter
    }


    private fun setupGroups() {
        val groupsObserver = Observer<MutableList<Tab>> {
            it?.let { Tabs.setTabs(it) }
        }
        (mViewModel as MainViewModel).getTabs().observe(this, groupsObserver)
    }

    companion object {
        fun start(activity: Activity, showMigrationSuccessDialog: Boolean = false) {
            val intent = activity.intentFor<MainActivity>().clearTop().singleTop()
            intent.putExtra("SHOW_MIGRATION_SUCCESS", showMigrationSuccessDialog)
            activity.startActivity(intent)
        }

        fun startAndCloseOtherActivities(activity: Activity) {
            activity.startActivity(activity.intentFor<MainActivity>().newTask().clearTask()) // This will call finish() on all other running activities.
        }
    }
}

