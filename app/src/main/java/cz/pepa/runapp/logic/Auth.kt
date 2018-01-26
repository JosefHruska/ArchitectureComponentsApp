package cz.pepa.runapp.logic

import android.app.Activity
import android.content.Intent
import android.support.v4.app.FragmentActivity
import com.firebase.ui.auth.AuthUI
import com.firebase.ui.auth.IdpResponse
import com.google.firebase.auth.FirebaseAuth
import cz.pepa.runapp.R
import cz.pepa.runapp.database.Database
import cz.pepa.runapp.database.DatabaseWrite
import cz.pepa.runapp.ui.common.Ids
import io.stepuplabs.settleup.util.extensions.toText
import logError

/**
 * Functions handling authentication.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */
object Auth {

    const val PROFILE_PICTURE_RESOLUTION = 512

    private var mInviteLinkHash : String? = null

    fun isSignedIn(): Boolean {
        return FirebaseAuth.getInstance().currentUser != null
    }

    fun isSignedInAnonymously(): Boolean {
        return FirebaseAuth.getInstance().currentUser?.displayName == null
    }

    fun signInAnonymously(successCallback: () -> Unit) {
        FirebaseAuth.getInstance().signInAnonymously().addOnCompleteListener {
            successCallback()
        }
    }

    fun signIn(activity: FragmentActivity, inviteLinkHash: String? = null) {
        mInviteLinkHash = inviteLinkHash
        activity.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setLogo(R.drawable.ic_splash_app)
                        .setAvailableProviders(listOf(
                                AuthUI.IdpConfig.Builder(AuthUI.GOOGLE_PROVIDER).build()))
                        .build(),
                Ids.REQUEST_SIGN_IN)
    }

    fun handleSignInResult(requestCode: Int, resultCode: Int, data: Intent?, successCallback: () -> Unit, errorCallback: ()->Unit) {
        if (requestCode == Ids.REQUEST_SIGN_IN) {
            if (resultCode == Activity.RESULT_OK) {
                val idpResponse = IdpResponse.fromResultIntent(data)
                val authProvider = when (idpResponse?.providerType) {
                    AuthUI.GOOGLE_PROVIDER -> Ids.PROVIDER_GOOGLE
                    AuthUI.FACEBOOK_PROVIDER -> Ids.PROVIDER_FACEBOOK
                    else -> Ids.PROVIDER_EMAIL
                }
                getProfilePicture(authProvider) {
                    Database.sync()
                    DatabaseWrite.addCurrentUser(authProvider, it, mInviteLinkHash)
                    successCallback()
                }
            } else {
                errorCallback()
            }
        }
    }

    fun signOut(activity: FragmentActivity, successCallback: () -> Unit) {
        AuthUI.getInstance()
                .signOut(activity)
                .addOnCompleteListener({
                    successCallback()
                })
    }

    fun getUsername(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser?.displayName ?: R.string.anonymous.toText()
    }

    fun getUserId(): String {
        return FirebaseAuth.getInstance().currentUser?.uid ?: throw RuntimeException("User is not signed in")
    }

    fun getEmail(): String? {
        return FirebaseAuth.getInstance().currentUser?.email?.toLowerCase()
    }

    private fun getProfilePicture(authProvider: String, successCallback: (String?) -> Unit) {
        when (authProvider) {
            Ids.PROVIDER_FACEBOOK -> logError(Throwable("FAcebook")) //getFacebookPicture(successCallback)
            Ids.PROVIDER_GOOGLE -> getGooglePicture(successCallback)
            else -> successCallback(null) // Email doesn't support pictures
        }
    }

    private fun getGooglePicture(callback: (String?) -> Unit) {
        callback(FirebaseAuth.getInstance().currentUser?.photoUrl?.toString()?.replace("/s96-c/", "/s$PROFILE_PICTURE_RESOLUTION-c/"))
    }

    private fun getFacebookPicture(callback: (String?) -> Unit) {
//        val request = GraphRequest.newGraphPathRequest(AccessToken.getCurrentAccessToken(), "me/picture", GraphRequest.Callback {
//            val requestError = it.error
//            if (requestError != null) {
//                callback(null)
//                return@Callback
//            }
//            val url = it.jsonObject.getJSONObject("data").getString("url")
//            callback(url)
//        })
//        val parameters = Bundle()
//        parameters.putInt("width", PROFILE_PICTURE_RESOLUTION)
//        parameters.putInt("height", PROFILE_PICTURE_RESOLUTION)
//        parameters.putBoolean("redirect", false)
//        request.parameters = parameters
//        request.executeAsync()
    }
}

