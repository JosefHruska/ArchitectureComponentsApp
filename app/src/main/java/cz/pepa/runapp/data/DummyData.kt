package cz.pepa.runapp.data

import cz.pepa.runapp.ui.common.Day
import cz.pepa.runapp.ui.common.MatchedDay
import io.stepuplabs.settleup.util.extensions.formatHtml
import java.time.Month

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object DummyData {

    fun getGroups(): List<Group> {
        return listOf(Group("Fashounci", "1"), Group("Mimoni", "2"), Group("Jezuité", "3"))
    }

    fun getToday(): TodayItem {
        return TodayItem().apply { steps = 4200; distance = 9.8F; calories = 1864F }
    }

    fun getDummyGoals(): List<Goal> {
        val goals = mutableListOf<Goal>()
        val firstGoal = Goal().apply { name = "Burn <b>1500</b> calories <b>per day</b>".formatHtml(); type = GoalId.CALORIES; percentage = 80.0; currentValue = 1200.0; targetValue = 1500.0; averageValue = 1335.0; matchedDays = DummyData.getMatchedDays() }
        val secondGoal = Goal().apply { name = "Take <b>18000</b> step <b>per day</b>".formatHtml(); type = GoalId.STEPS; percentage = 65.0; currentValue =  10056.0 ; targetValue = 18000.0; averageValue = 12017.0; matchedDays = DummyData.getMatchedDays()}
        val thirdGoal = Goal().apply { name = "Travel <b>40</b> Km <b>per week</b>".formatHtml(); type = GoalId.DISTANCE; percentage = 23.0; currentValue =  5.4 ; targetValue = 40.0; averageValue = 36.8; matchedDays = DummyData.getMatchedDays()}
        val fourthGoal = Goal().apply { name = "Be active for <b>50</b> minute <b>per day</b>".formatHtml(); type = GoalId.ACTIVE_TIME; percentage = 130.0; currentValue =  72.4 ; targetValue = 50.0; averageValue = 41.5; matchedDays = DummyData.getMatchedDays()}
        goals.addAll(listOf(firstGoal, secondGoal, thirdGoal, fourthGoal))
//        goals.addAll(listOf(thirdGoal, fourthGoal))
        return goals
    }

    fun getAverageResults(): MutableMap<Type, List<Float>> {
        val average = mutableMapOf<Type, List<Float>>()
        val activeResults = listOf(45f, 38f, 34f, 32f)
        val stepsResults = listOf(6945f, 7052f, 7012f, 9622f)
        val caloriesResult = listOf(1602f, 1703f, 2100f, 2006f)
        val distanceResult = listOf(6200f, 7504f, 6054f, 7453f)
        average.put(Type.ACTIVE, activeResults)
        average.put(Type.STEPS, stepsResults)
        average.put(Type.CALORIES, caloriesResult)
        average.put(Type.DISTANCE, distanceResult)

        return average
    }

    fun getMatchedDays(): List<MatchedDay> {
        val days = mutableListOf<MatchedDay>()
        days.add( MatchedDay(Day.Sunday, true))
        days.add( MatchedDay(Day.Monday, true))
        days.add(  MatchedDay(Day.Tuesday, true))
        days.add(  MatchedDay(Day.Wednesday, false))
        days.add(  MatchedDay(Day.Thursday, true))
        days.add(  MatchedDay(Day.Friday, false))
        days.add(  MatchedDay(Day.Saturday, true))

        return days
    }
}