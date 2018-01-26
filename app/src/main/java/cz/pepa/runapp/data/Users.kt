package cz.pepa.runapp.data

import com.google.firebase.database.IgnoreExtraProperties

/**
 * Model representing User.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */
@IgnoreExtraProperties
class User : DatabaseModel() {

    lateinit var authProvider: String
    lateinit var currentTabId: String
    var email: String? = null
    lateinit var name: String
    var photoUrl: String? = null
    var inviteLinkHash: String? = null

    override fun toString(): String {
        return "User(activeGroup='$currentTabId', email='$email', name='$name', photoUrl='$photoUrl')"
    }

    companion object {
        val PROVIDER_GOOGLE = "google"
        val PROVIDER_FACEBOOK = "facebook"
        val PROVIDER_EMAIL = "email"
    }
}
