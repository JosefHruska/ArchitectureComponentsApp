package cz.pepa.runapp.ui.common

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import cz.pepa.runapp.R
import cz.pepa.runapp.utils.setTextAvatar
import io.stepuplabs.settleup.util.extensions.hide
import io.stepuplabs.settleup.util.extensions.inflate
import io.stepuplabs.settleup.util.extensions.show
import kotlinx.android.synthetic.main.view_week_checker.view.*
import org.jetbrains.anko.childrenSequence
import java.time.DayOfWeek

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class WeekChecker @JvmOverloads constructor(context: Context, attrs: AttributeSet, defStyleAttr: Int = 0) : FrameLayout(context, attrs, defStyleAttr) {

    init {
        inflate(R.layout.view_week_checker)
    }

    fun setStreak(count: Int) {
        vDaysLayout.hide()
        vStreak.show()
        vStreak.text = "You streak is $count days"
    }

    fun setMatchedDays(list : List<MatchedDay>) {
        vDaysLayout.show()
        vStreak.hide()
        list.forEach {
            when(it.day) {
                Day.Sunday -> vSunday.setDayDrawable(it.isMatched, "S")
                Day.Monday -> vMonday.setDayDrawable(it.isMatched, "M")
                Day.Tuesday -> vTuesday.setDayDrawable(it.isMatched, "T")
                Day.Wednesday -> vWednesday.setDayDrawable(it.isMatched, "W")
                Day.Thursday -> vThursday.setDayDrawable(it.isMatched, "T")
                Day.Friday -> vFriday.setDayDrawable(it.isMatched, "F")
                Day.Saturday -> vSaturday.setDayDrawable(it.isMatched, "S")
            }
        }
    }

    private fun ImageView.setDayDrawable(isMatched: Boolean, daySymbol: String ) {
        if (isMatched) {
            this.setTextAvatar(daySymbol, R.color.green_positive)
        } else {
            this.setTextAvatar(daySymbol)
        }
    }
}

data class MatchedDay(val day: Day,val isMatched: Boolean)

enum class Day {
    Sunday,
    Monday,
    Tuesday,
    Wednesday,
    Thursday,
    Friday,
    Saturday
}