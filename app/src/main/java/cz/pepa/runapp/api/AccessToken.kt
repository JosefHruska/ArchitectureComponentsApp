package cz.pepa.runapp.api

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class AccessToken {

    var accessToken: String? = null
    // OAuth requires uppercase Authorization HTTP header value for token type
    var tokenType: String? = null
        get() {
            if (!Character.isUpperCase(field!![0])) {
                tokenType = Character.toString(field!![0]).toUpperCase() + field!!.substring(1)
            }

            return field
        }
    private var expires_in: Int? = null
    var refreshToken: String = ""
    var scope: String? = null
    var clientID: String = ""
    var clientSecret: String = ""

    var expiry: Int
        get() = expires_in!!
        set(expires_in) {
            this.expires_in = expires_in
        }

}
