package cz.pepa.runapp.ui.main.group

import android.arch.lifecycle.Observer
import android.os.Bundle
import android.support.v4.app.Fragment
import cz.pepa.runapp.R
import cz.pepa.runapp.data.DummyFittnes
import cz.pepa.runapp.ui.base.BaseFragment
import cz.pepa.runapp.ui.base.BaseViewModel
import cz.pepa.runapp.ui.common.RecyclerAdapter
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.android.synthetic.main.include_members_card.view.*

/**
 * A fragment displaying one group.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class GroupTabFragment: BaseFragment() {

    private var mGroupDataListener: ((GroupTabData) -> Unit)? = null
    private var mGroupColor = 0
    lateinit var mMembersAdapter: RecyclerAdapter<DummyFittnes>

    override fun getContentResId(): Int {
        return R.layout.fragment_tab
    }

    override fun getViewModel(): BaseViewModel {
        return GroupViewModel()
    }

    override fun initUi() {
        hideProgress()
        setupMembersCard()
        subscribeMembers()
    }
//
//    private fun loadGroupData() {
//        load(DatabaseCombine.groupTabData(groupId)) {
//            mGroupData = it
//            val onGroupDataChanged = getView().getGroupDataListener()
//            if (onGroupDataChanged != null) {
//                onGroupDataChanged(it)
//            }
//            Tabs.setGroupCircles(it.circles)
//            if (it.totalPaid.isZero()) {
//                getView().hideTotalPaid()
//            } else {
//                getView().showTotalPaid(it.totalPaid, getGroupColor(), it.circles.currency)
//            }
//            if (it.debts.isEmpty()) {
//                getView().hideDebts()
//            } else {
//                getView().showDebts(it.debts)
//            }
//            contentLoaded()
//        }
//    }

    private fun subscribeMembers() {
        val membersObserver = Observer<List<DummyFittnes>> {
            if (it != null) {
                mMembersAdapter.setData(it, MembersBinder())
            }
        }

        (mViewModel as GroupViewModel).mMembers.observe(this,  membersObserver)

    }

    fun groupInstance(groupId: String): Fragment {
        val args = Bundle()
        args.putString("GROUP_ID", groupId)
        arguments = args

        return this
    }

    private fun setupMembersCard() {
        vMembers.vRecycler.isNestedScrollingEnabled = false
        vMembers.vTitle.text = getString(R.string.members)
        mMembersAdapter = RecyclerAdapter<DummyFittnes>(R.layout.item_member)
        vMembers.vRecycler.adapter = mMembersAdapter
    }
}

data class GroupTabData(val debts: List<DummyFittnes>, val groupId: String)