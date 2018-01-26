package cz.pepa.runapp.data

/**
 * Class representing one tab of ViewPager
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

data class Tab(val title: String, val id: String, val color: Int) {

    fun isOverview(): Boolean {
        return id == OVERVIEW
    }

    companion object {
        val OVERVIEW = "OVERVIEW"
    }
}