package com.example.pepa.fitprogressbar

import android.content.Context
import android.graphics.Canvas
import android.support.constraint.ConstraintSet
import android.util.AttributeSet
import cz.pepa.runapp.R
import io.stepuplabs.settleup.util.extensions.hide
import io.stepuplabs.settleup.util.extensions.pxToDp
import io.stepuplabs.settleup.util.extensions.show
import kotlinx.android.synthetic.main.google_fit_progress_bar.view.*

/**
 * Simple progress bar which is inspired by Google Fit progress bar.
 */


class GoogleFitProgressBar @JvmOverloads constructor(context: Context, attrs: AttributeSet? = null, defStyleAttr: Int = 0) : BaseProgressBar(context, attrs, defStyleAttr) {

    var descriptionIsInProgress = true

    override fun getLayoutRes() = R.layout.google_fit_progress_bar

    override fun drawChildViews() {
        drawProgressIcon()
//        setupProgressDescriptionPosition()
    }

//    override fun setupChildViews() {
//        setupProgressDescriptionPosition()
//    }

    fun setProgressText(text: String) {
        vProgressText.text = text
        vProgressTextS.text = text
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)
        setupProgressDescriptionPosition()

    }

    fun moveProgressDescriptionToRight() {
        vProgressDescriptionContainer.hide()
        vProgressDescriptionContainerS.show()
    }

    fun moveProgressDescriptionToProgress() {
        vProgressDescriptionContainer.show()
        vProgressDescriptionContainerS.hide()
    }

    private fun setupProgressDescriptionPosition() {
        // If there is not enough space to show icon + text inside progress it should be moved to the end of progress background
        checkProgressDescriptionSpace()
    }

    // Check if there is enough space for progress description
    private fun checkProgressDescriptionSpace() {
        if (vProgressHolder.width.pxToDp() > calculateProgressWidth()) {
            if (descriptionIsInProgress) {
                moveProgressDescriptionToRight()
                descriptionIsInProgress = false
            }
        } else {
            if (!descriptionIsInProgress) {
                moveProgressDescriptionToProgress()
                descriptionIsInProgress = true
            }
        }
    }

    private fun calculateProgressWidth() = totalWidth * (progressPercentage / 100)

    private fun drawProgressIcon() {
        val iconLayoutParameters = vProgressIcon.layoutParams
        iconLayoutParameters.width = progressIconHeight
        iconLayoutParameters.height = progressIconHeight
        vProgressIcon.layoutParams = iconLayoutParameters
     //   checkProgressDescriptionSpace()

        val iconLayoutParametersS = vProgressIconS.layoutParams
        iconLayoutParametersS.width = progressIconHeight
        iconLayoutParametersS.height = progressIconHeight
        vProgressIconS.layoutParams = iconLayoutParametersS
       // checkProgressDescriptionSpace()
    }

}