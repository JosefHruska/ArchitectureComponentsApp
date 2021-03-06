package cz.pepa.runapp.ui.main

import android.app.Activity
import android.arch.lifecycle.Observer
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import cz.pepa.runapp.DebugHelper
import cz.pepa.runapp.R
import cz.pepa.runapp.api.GoogleFitReader.initGoogleFit
import cz.pepa.runapp.data.Tab
import cz.pepa.runapp.data.User
import cz.pepa.runapp.ui.base.BaseActivity
import cz.pepa.runapp.ui.common.Ids
import cz.pepa.runapp.utils.loadAvatar
import io.stepuplabs.settleup.util.extensions.popupMenuOnClick
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_drawer_header.*
import kotlinx.android.synthetic.main.include_drawer_header.view.*
import org.jetbrains.anko.clearTop
import org.jetbrains.anko.intentFor
import org.jetbrains.anko.singleTop

/**
 * Main activity
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class MainActivity: BaseActivity<MainViewModel, MainController>(), MainController {

    /**
     * Track whether an authorization activity is stacking over the current activity, i.e. when
     * a known auth error is being resolved, such as showing the account chooser or presenting a
     * consent dialog. This avoids common duplications as might happen on screen rotations, etc.
     */
    private val AUTH_PENDING = "auth_state_pending"
    private var authInProgress = false

    private lateinit var vHeader: View
    private lateinit var mViewPagerAdapter: MainViewPagerAdapter

    override fun getViewModel(): MainViewModel {
        return MainViewModel()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == Ids.PERMISSION_FINE_LOCATION) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                initGoogleFit(this)
            }
        }
    }

    override fun initUi() {
        setupDrawer()
        setupViewPager()
        setupGroups()
        setupUser()
        DebugHelper.start(this)
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING)
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
        toggle.syncState()
    }

    private fun setupViewPager() {
        vTabs.setupWithViewPager(vViewPager)
        Tabs.setListener( object : Tabs.Listener {
            override fun allTabsChanged() {
                mViewPagerAdapter.notifyDataSetChanged()
            }

            override fun topChanged(position: Int) {}
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

    private fun setupUser() {
        val userObserver = Observer<User> {
            it?.let {
                vHeader.vDrawerHeaderName.text = it.name
                vHeader.vDrawerHeaderEmail.text = it.email
                vHeader.vDrawerHeaderImage.loadAvatar(it) // User's avatar has no pointer.
            }
        }
        (mViewModel as MainViewModel).getUser().observe(this, userObserver)

    }

    companion object {
        fun start(activity: Activity, showMigrationSuccessDialog: Boolean = false) {
            val intent = activity.intentFor<MainActivity>().clearTop().singleTop()
            intent.putExtra("SHOW_MIGRATION_SUCCESS", showMigrationSuccessDialog)
            activity.startActivity(intent)
        }
    }
}

