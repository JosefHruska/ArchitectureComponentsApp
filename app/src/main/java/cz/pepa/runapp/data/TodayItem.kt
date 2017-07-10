package cz.pepa.runapp.data

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class TodayItem() : DatabaseModel() {

    var steps: Int? = null
    var distance: Float? = null
    var calories: Float? = null
}