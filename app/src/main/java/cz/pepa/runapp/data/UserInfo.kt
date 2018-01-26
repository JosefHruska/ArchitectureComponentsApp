package cz.pepa.runapp.data

/**
 * Model representing info about user
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */
class UserInfo() : DatabaseModel() {

    var lastSync: Long = 0L
}