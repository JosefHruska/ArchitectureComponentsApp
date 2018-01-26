package cz.pepa.runapp.ui.main.group

import android.arch.lifecycle.Observer
import android.os.Bundle
import cz.pepa.runapp.R
import cz.pepa.runapp.data.DummyFitness
import cz.pepa.runapp.ui.base.BaseFragment
import cz.pepa.runapp.ui.common.RecyclerAdapter
import kotlinx.android.synthetic.main.fragment_tab.*
import kotlinx.android.synthetic.main.include_members_card.view.*

/**
 * A fragment displaying one group.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

 open class GroupTabFragment<VM: GroupViewModel<C>, C: GroupTabController>(): BaseFragment<VM, C>(), GroupTabController {

    private var mGroupDataListener: ((GroupTabData) -> Unit)? = null
    private var mGroupColor = 0
    lateinit var mMembersAdapter: RecyclerAdapter<DummyFitness>

    override fun getContentResId(): Int {
        return R.layout.fragment_tab
    }

    override fun getViewModel(): VM {
        return GroupViewModel<GroupTabController>() as VM
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
        val membersObserver = Observer<List<DummyFitness>> {
            if (it != null) {
                mMembersAdapter.setData(it, MembersBinder())
            }
        }

        (mViewModel).mMembers.observe(this,  membersObserver)

    }

    fun groupInstance(groupId: String): BaseFragment<VM, C> {
        val args = Bundle()
        args.putString("GROUP_ID", groupId)
        arguments = args

        return this
    }

    private fun setupMembersCard() {
        vMembers.vRecycler.isNestedScrollingEnabled = false
        vMembers.vTitle.text = getString(R.string.members)
        mMembersAdapter = RecyclerAdapter<DummyFitness>(R.layout.item_member)
        vMembers.vRecycler.adapter = mMembersAdapter
    }
}

data class GroupTabData(val debts: List<DummyFitness>, val groupId: String)