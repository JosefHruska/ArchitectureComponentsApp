package cz.pepa.runapp.data

import cz.pepa.runapp.ui.common.Day
import cz.pepa.runapp.ui.common.MatchedDay

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
        val firstGoal = Goal().apply { name = "Burn 1500 calories per day"; type = GoalId.CALORIES; percentage = 80.0; currentValue = 1200.0; targetValue = 1500.0; averageValue = 1335.0; matchedDays = DummyData.getMatchedDays() }
        val secondGoal = Goal().apply { name = "Take 18000 step per day"; type = GoalId.STEPS; percentage = 65.0; currentValue =  10056.0 ; targetValue = 18000.0; averageValue = 12017.0; matchedDays = DummyData.getMatchedDays()}
        val thirdGoal = Goal().apply { name = "Travel 40 Km per week"; type = GoalId.DISTANCE; percentage = 23.0; currentValue =  11.4 ; targetValue = 40.0; averageValue = 36.8; matchedDays = DummyData.getMatchedDays()}
        val fourthGoal = Goal().apply { name = "Be active for 50 minute per day"; type = GoalId.ACTIVE_TIME; percentage = 130.0; currentValue =  72.4 ; targetValue = 50.0; averageValue = 41.5; matchedDays = DummyData.getMatchedDays()}
        goals.addAll(listOf(firstGoal, secondGoal, thirdGoal, fourthGoal))
        return goals
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