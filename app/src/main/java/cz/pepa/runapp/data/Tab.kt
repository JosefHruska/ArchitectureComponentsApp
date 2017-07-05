package cz.pepa.runapp.data

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

data class Tab(val title: String, val id: String, val color: Int) {

//    var groupTabs : CirclesResult? = null
//    var overviewTabs: OverviewCirclesResult? = null
////    var overviewStatistics: Statistics? = null

    fun isOverview(): Boolean {
        return id == OVERVIEW
    }

    companion object {
        val OVERVIEW = "OVERVIEW"
    }
}