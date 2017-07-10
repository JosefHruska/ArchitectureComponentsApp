package cz.pepa.runapp.data

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
}