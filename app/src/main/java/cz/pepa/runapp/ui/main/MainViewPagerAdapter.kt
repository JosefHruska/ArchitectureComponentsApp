package cz.pepa.runapp.ui.main

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.view.ViewGroup
import cz.pepa.runapp.ui.base.BaseFragment
import cz.pepa.runapp.ui.main.group.GroupTabFragment
import cz.pepa.runapp.ui.main.overview.OverviewFragment
import eu.inloop.pager.UpdatableFragmentPagerAdapter

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */


class MainViewPagerAdapter(fragmentManager: FragmentManager, val fragmentChanged: (BaseFragment) -> Unit) : UpdatableFragmentPagerAdapter(fragmentManager) {

    var currentFragment: Any? = null

    override fun getCount(): Int {
        return Tabs.size()
    }

    override fun setPrimaryItem(container: ViewGroup?, position: Int, fragment: Any?) {
        if (currentFragment != fragment) {
            if (fragment is GroupTabFragment) {
                fragmentChanged(fragment)
            }
            currentFragment = fragment
        }
        super.setPrimaryItem(container, position, fragment)
    }

    override fun getItem(position: Int): Fragment {
        val tab = Tabs.get()[position]
        if (tab.isOverview()) {
            return OverviewFragment()
        } else {
            return GroupTabFragment().groupInstance(tab.id)
        }
    }

    override fun getItemPosition(item: Any?): Int {
        // Makes notifyDataSetChanged actually working
        if (item is OverviewFragment) {
            return 0
        } else {
            val groupId = (item as GroupTabFragment).arguments.getString("GROUP_ID")
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