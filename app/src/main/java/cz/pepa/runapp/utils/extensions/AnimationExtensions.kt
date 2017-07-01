package io.stepuplabs.settleup.util.extensions

import android.animation.Animator
import android.animation.AnimatorListenerAdapter
import android.animation.AnimatorSet
import android.animation.ObjectAnimator
import android.app.Activity
import android.support.v7.widget.Toolbar
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.view.animation.LinearInterpolator
import android.view.animation.OvershootInterpolator
import android.widget.LinearLayout
import com.github.clans.fab.FloatingActionMenu
import io.stepuplabs.settleup.R
import io.stepuplabs.settleup.app
import io.stepuplabs.settleup.ui.main.AnimationState
import io.stepuplabs.settleup.ui.main.ScrollAwareFloatingActionMenu
import kotlinx.android.synthetic.main.activity_main.view.*

/**
 * Animation extensions.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

fun Toolbar.showTitleAnimated() {
    val showAnimation = AnimationUtils.loadAnimation(app(), R.anim.fade_in)
    showAnimation.setOnAnimationEndListener { vWhoShouldPayTitle.show() }
    vWhoShouldPayTitle.startAnimation(showAnimation)
}

fun Toolbar.hideTitleAnimated() {
    val hideAnimation = AnimationUtils.loadAnimation(app(), R.anim.fade_out)
    hideAnimation.setOnAnimationEndListener { vWhoShouldPayTitle.makeInvisible() /* View.GONE cause UI issues here */ }
    vWhoShouldPayTitle.startAnimation(hideAnimation)
}

fun FloatingActionMenu.setTabChangeShowAnimation() { // Floating action menu scale to 0% of its default size and then back to original size when is tab changed
    this.setMenuButtonShowAnimation(AnimationUtils.loadAnimation(app(), R.anim.floating_action_menu_scale_up))
    val set = AnimatorSet()
    val scaleInX = ObjectAnimator.ofFloat(this.menuIconView, "scaleX", 0.0f, 1.0f).setDuration(200)
    val scaleInY = ObjectAnimator.ofFloat(this.menuIconView, "scaleY", 0.0f, 1.0f).setDuration(200)

    scaleInX.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator) {
            this@setTabChangeShowAnimation.menuIconView.setImageResource(if (this@setTabChangeShowAnimation.isOpened)
                R.drawable.ic_fab_expense
            else
                R.drawable.ic_add)
        }
    })
    set.play(scaleInX).with(scaleInY)
    set.interpolator = OvershootInterpolator(2f)
    this.iconToggleAnimatorSet = set
}

fun FloatingActionMenu.setTabChangeHideAnimation() { // Floating action menu scale to 0% of its default size and then back to original size when is tab changed.
    this.setMenuButtonHideAnimation(AnimationUtils.loadAnimation(app(), R.anim.floating_action_menu_scale_down))
    val set = AnimatorSet()
    val scaleOutX = ObjectAnimator.ofFloat(this.menuIconView, "scaleX", 1.0f, 0.0f).setDuration(120)
    val scaleOutY = ObjectAnimator.ofFloat(this.menuIconView, "scaleY", 1.0f, 0.0f).setDuration(120)

    set.play(scaleOutX).with(scaleOutY)
    set.interpolator = OvershootInterpolator(2f)
    this.iconToggleAnimatorSet = set
}

fun LinearLayout.playScaleUpAnimation() { // ScaleUp animation is triggered when we show new set of circles.
    val set = AnimatorSet()
    val scaleInX = ObjectAnimator.ofFloat(this, "scaleX", 0.0f, 1.0f).setDuration(200)
    val scaleInY = ObjectAnimator.ofFloat(this, "scaleY", 0.0f, 1.0f).setDuration(200)

    scaleInX.addListener(object : AnimatorListenerAdapter() {
        override fun onAnimationStart(animation: Animator?) {
            super.onAnimationStart(animation)
            scaleX = 0f
            scaleY = 0f
        }

        override fun onAnimationEnd(animation: Animator?) {
            super.onAnimationEnd(animation)
            scaleX = 1f
            scaleY = 1f
        }
    })
    set.play(scaleInX).with(scaleInY)
    set.interpolator = LinearInterpolator()
    set.start()
}

fun Animation.setOnAnimationEndListener(onAnimationEnd: () -> Unit) {
    this.setAnimationListener(object : Animation.AnimationListener {
        override fun onAnimationRepeat(p0: Animation?) {
            //Nothing to do here.
        }

        override fun onAnimationStart(p0: Animation?) {
            //Nothing to do here.
        }

        override fun onAnimationEnd(p0: Animation?) {
            onAnimationEnd()
        }
    })
}

fun ScrollAwareFloatingActionMenu.showScaled() {
    if (this.animationState == AnimationState.IDLE) {
        vFloatingActionMenu.setTabChangeShowAnimation()
        vFloatingActionMenu.showMenuButton(true)
        vFloatingActionMenu.menuIconView.startAnimation(AnimationUtils.loadAnimation(app(), R.anim.floating_action_menu_scale_up))
    }
}

fun ScrollAwareFloatingActionMenu.hideScaled() {
    if (this.animationState == AnimationState.IDLE) {
        vFloatingActionMenu.setTabChangeHideAnimation()
        vFloatingActionMenu.hideMenuButton(true)
        vFloatingActionMenu.menuIconView.startAnimation(AnimationUtils.loadAnimation(app(), R.anim.floating_action_menu_scale_down))
    }
}

fun ScrollAwareFloatingActionMenu.showScrolled() {
    if (this.animationState == AnimationState.IDLE) {
        val showAnimation = AnimationUtils.loadAnimation(app(), R.anim.floating_action_menu_show_scroll)
        showAnimation.setOnAnimationEndListener {
            this.showMenu(false)
            this.animationState = AnimationState.IDLE
        }
        this.startAnimation(showAnimation)
        this.animationState = AnimationState.ANIMATING
    }
}

fun ScrollAwareFloatingActionMenu.hideScrolled() {
    if (this.animationState == AnimationState.IDLE) {
        val hideAnimation = AnimationUtils.loadAnimation(app(), R.anim.floating_action_menu_hide_scroll)
        hideAnimation.setOnAnimationEndListener {
            this.hideMenu(false)
            this.animationState = AnimationState.IDLE
        }
        this.startAnimation(hideAnimation)
        this.animationState = AnimationState.ANIMATING
    }
}

fun Activity.animateActivityEnterFromRight() {
    overridePendingTransition(R.anim.enter_from_right, R.anim.exit_to_left)
}

fun Activity.animateActivityEnterFromLeft() {
    overridePendingTransition(R.anim.enter_from_left, R.anim.exit_to_right)
}

