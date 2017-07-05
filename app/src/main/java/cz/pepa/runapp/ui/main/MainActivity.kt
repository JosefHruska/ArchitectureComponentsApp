package cz.pepa.runapp.ui.main

import android.os.Bundle
import android.os.PersistableBundle
import android.support.v7.app.ActionBarDrawerToggle
import android.view.View
import cz.pepa.runapp.R
import cz.pepa.runapp.logic.Fit
import cz.pepa.runapp.ui.base.BaseActivity
import cz.pepa.runapp.ui.base.BaseViewModel
import io.stepuplabs.settleup.util.extensions.popupMenuOnClick
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.include_drawer_header.*
import kotlinx.android.synthetic.main.include_drawer_header.view.*

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


    override fun getViewModel(): BaseViewModel {
        return MainViewModel()
    }

    override fun getLayoutRes(): Int {
        return R.layout.activity_main
    }

    override fun initUi() {
        setupDrawer()
        setupViewPager()
    }

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        super.onCreate(savedInstanceState, persistentState)
        if (savedInstanceState != null) {
            authInProgress = savedInstanceState.getBoolean(AUTH_PENDING)
        }
        Fit.buildFitnessClient(this)
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
    }
}

