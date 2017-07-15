package cz.pepa.runapp.data

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class TodayItem() : DatabaseModel() {

    var steps: Int = 0
    var distance: Float = 0F
    var calories: Float = 0F
}