package cz.pepa.runapp.ui.common

import android.content.Context
import android.util.AttributeSet
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import cz.pepa.runapp.R
import cz.pepa.runapp.utils.setTextAvatar
import io.stepuplabs.settleup.util.extensions.*
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
        currentDay()
        list.forEach {
            when(it.day) {
                Day.Sunday -> vSunday.setDayDrawable(it.isMatched, "S", currentDay() == 1)
                Day.Monday -> vMonday.setDayDrawable(it.isMatched, "M", currentDay() == 2)
                Day.Tuesday -> vTuesday.setDayDrawable(it.isMatched, "T", currentDay() == 3)
                Day.Wednesday -> vWednesday.setDayDrawable(it.isMatched, "W", currentDay() == 4)
                Day.Thursday -> vThursday.setDayDrawable(it.isMatched, "T", currentDay() == 5)
                Day.Friday -> vFriday.setDayDrawable(it.isMatched, "F", currentDay() == 6)
                Day.Saturday -> vSaturday.setDayDrawable(it.isMatched, "S", currentDay() == 7)
            }
        }
    }

    private fun ImageView.setDayDrawable(isMatched: Boolean, daySymbol: String, isToday: Boolean) {
        if (isMatched) {
            this.setTextAvatar(daySymbol, R.color.primary)
        } else {
            this.setTextAvatar(daySymbol)
        }
        if (isToday) {
            val lp = this.layoutParams
            lp.height = 24.dpToPx()
            lp.width = 24.dpToPx()
            this.layoutParams = lp
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