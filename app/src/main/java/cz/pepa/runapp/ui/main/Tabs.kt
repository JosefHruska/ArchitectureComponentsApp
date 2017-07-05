package cz.pepa.runapp.ui.main

import android.support.v4.view.PagerAdapter
import cz.pepa.runapp.data.Tab

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object Tabs {
    private var mTabs = mutableListOf<Tab>()
    private var mListener: Listener? = null

    fun setTabs(tabs: MutableList<Tab>) {
        if (tabs != mTabs) {
            mTabs = tabs
            mListener?.allTabsChanged()
        }
    }

    fun setListener(listener: Listener) {
        mListener = listener
    }

//    fun setGroupCircles(groupTabs: CirclesResult) {
//        val position = mTabs.indexOfFirst { it.id == groupTabs.groupId }
//        if (position != -1 && mTabs[position].groupTabs != groupTabs) {
//            mTabs[position].groupTabs = groupTabs
//            mListener?.topChanged(position)
//        }
//    }
//
//    fun setOverviewCircles(overviewCircles: OverviewCirclesResult) {
//        if (mTabs[0].overviewTabs != overviewCircles) {
//            mTabs[0].overviewTabs = overviewCircles
////            mTabs[0].overviewStatistics = null
//            mListener?.topChanged(0)
//        }
//    }


    fun size(): Int {
        return mTabs.size
    }

    fun get(): List<Tab> {
        return mTabs
    }

    fun getPositionOfId(tabId: String): Int {
        val index = mTabs.indexOfFirst { it.id == tabId }
        if (index == -1) {
            return PagerAdapter.POSITION_NONE
        }
        return index
    }

    interface Listener {
        fun allTabsChanged()
        fun topChanged(position: Int)
    }
}