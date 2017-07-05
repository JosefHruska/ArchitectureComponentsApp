package cz.pepa.runapp.ui.base

import android.arch.lifecycle.LifecycleFragment
import android.os.Bundle
import android.support.annotation.LayoutRes
import android.support.v4.app.Fragment
import android.support.v4.app.FragmentActivity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.FrameLayout
import cz.pepa.runapp.R
import io.stepuplabs.settleup.util.extensions.hide
import io.stepuplabs.settleup.util.extensions.show
import kotlinx.android.synthetic.main.fragment_base.*
import org.jetbrains.anko.inputMethodManager

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

abstract class BaseFragment(): LifecycleFragment() {

    abstract fun getContentResId(): Int

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        val layout: View = inflater.inflate(R.layout.fragment_base, container, false)
        inflater.inflate(getContentResId(), layout.findViewById<FrameLayout>(R.id.vContent), true)
        return layout
    }

    override fun onViewCreated(view: View?, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initUi()
    }

    open fun initUi() {

    }

    fun showContent() {
        vContent.show()
    }

    fun hideContent() {
        vContent.hide()
    }

     fun showProgress() {
        vProgress.show()
    }

     fun hideProgress() {
        vProgress.hide()
    }

    fun showKeyboard() {
        if (activity.currentFocus != null) {
            val inputMethodManager = context.inputMethodManager
            inputMethodManager.showSoftInput(activity.currentFocus, 0)
        }
    }

    fun hideKeyboard() {
        if (activity.currentFocus != null) {
            val inputMethodManager = context.inputMethodManager
            inputMethodManager.hideSoftInputFromWindow(activity.currentFocus.windowToken, 0)
        }
    }

    fun replaceFragment(activity: FragmentActivity, @LayoutRes layoutToReplace: Int, replacingFragment: Fragment) {
        activity.supportFragmentManager.beginTransaction().replace(layoutToReplace, replacingFragment, tag).commit()
    }




}