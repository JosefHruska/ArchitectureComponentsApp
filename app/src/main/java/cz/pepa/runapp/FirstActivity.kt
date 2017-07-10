package cz.pepa.runapp

import Offline
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import cz.pepa.runapp.database.Database
import cz.pepa.runapp.extensions.isDeviceOnline
import cz.pepa.runapp.logic.Auth
import cz.pepa.runapp.ui.main.MainActivity
import org.jetbrains.anko.doAsync
import org.jetbrains.anko.toast

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class FirstActivity: AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        doAsync {
            Database.connect() // Database might be disconnected after notification sync
            Offline.monitorConnectedState()
        }
        if (Auth.isSignedIn()) {
//            handleOpenedFromNotification(intent)
            goTo(Screen.MAIN)
        } else {
            if (isDeviceOnline()) {
                Auth.signIn(this)
            } else {
                goTo(Screen.OFFLINE_ERROR)
            }
        }
    }

    private fun goTo(screen: Screen) {
//        if (app().isInitialized() && !mMinTimePassed) {
//            mLaunchAfterMinTime = screen
//        } else {
            when (screen) {
                Screen.MAIN -> MainActivity.start(this)
//                Screen.ONBOARDING -> startActivity<OnboardingActivity>()
//                Screen.JOIN_GROUP -> {
//                    val result = checkNotNull(mResult, { "Result is empty" })
//                    JoinGroupActivity.start(this, result.groupId, result.hash)
//                }
                Screen.OFFLINE_ERROR -> toast(R.string.cant_sign_in_offline)
//                Screen.UPDATE -> startActivity<UpdateActivity>()
            }
            finish()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Auth.handleSignInResult(requestCode, resultCode, data, {
            goTo(Screen.MAIN)
        }, {
            toast(R.string.sign_in_failed)
            finish()
        })

    }

    private enum class Screen {MAIN, ONBOARDING, JOIN_GROUP, OFFLINE_ERROR, UPDATE }
}