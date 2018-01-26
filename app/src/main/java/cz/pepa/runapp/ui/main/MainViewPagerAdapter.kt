package cz.pepa.runapp.ui.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.ViewGroup
import cz.pepa.runapp.ui.main.group.GroupTabController
import cz.pepa.runapp.ui.main.group.GroupTabFragment
import cz.pepa.runapp.ui.main.group.GroupViewModel
import cz.pepa.runapp.ui.main.overview.OverviewFragment
import eu.inloop.pager.UpdatableFragmentPagerAdapter

/**
 * Adapter handling view pager on main screen.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class MainViewPagerAdapter(fragmentManager: FragmentManager, val fragmentChanged: (Fragment) -> Unit) : UpdatableFragmentPagerAdapter(fragmentManager) {

    var currentFragment: Any? = null

    override fun getCount(): Int {
        return Tabs.size()
    }

    override fun setPrimaryItem(container: ViewGroup?, position: Int, fragment: Any?) {
        if (fragment is Fragment) {
            if (currentFragment != fragment) {
                fragmentChanged(fragment)
                currentFragment = fragment
            }
            super.setPrimaryItem(container, position, fragment)
        }
    }

    override fun getItem(position: Int): Fragment {
        val tab = Tabs.get()[position]
        if (tab.isOverview()) {
            return OverviewFragment()
        } else {
            return GroupTabFragment<GroupViewModel<GroupTabController>, GroupTabController>().groupInstance(tab.id)
        }
    }

    override fun getItemPosition(item: Any?): Int {
        item as Fragment
        // Makes notifyDataSetChanged actually working
        if (item is OverviewFragment) {
            return 0
        } else {
            val groupId = (item).arguments.getString("GROUP_ID")
            return Tabs.getPositionOfId(groupId)
        }
    }

    override fun getItemId(position: Int): Long {
        return Tabs.get()[position].id.hashCode().toLong()
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return Tabs.get()[position].title
    }

}