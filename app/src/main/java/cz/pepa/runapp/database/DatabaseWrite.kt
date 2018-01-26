
package cz.pepa.runapp.database

import cz.pepa.runapp.data.*
import cz.pepa.runapp.logic.Auth
import io.stepuplabs.settleup.util.extensions.todayBegin
import logError

/**
 * Main class for writing to Firebase Database.
 *
 * @author Josef Hruska (josef@stepuplabs.io)
 */

object DatabaseWrite {

    fun updateToday(todayItem: TodayItem) {
        val today = TodayItem().apply { this.steps = todayItem.steps; this.distance = todayItem.distance; this.calories = todayItem.calories; this.dayStart = todayBegin()  }
        update("/days/${Auth.getUserId()}/${todayBegin()}", today )
    }

    fun updateMissingDays(todayItems: Map<Long, TodayItem>, lastSyncedDay: Long) {
        todayItems.forEach {
            update("/days/${Auth.getUserId()}/${it.key}", it.value, {
                update("/userInfo/${Auth.getUserId()}", UserInfo().apply { lastSync = lastSyncedDay })
            })
        }
    }

    fun updateAllDays(todayItems: Map<Long, TodayItem>) {
        todayItems.forEach {
            update("/days/${Auth.getUserId()}/${it.key}", it.value)
        }
    }


    fun updateGoals(goals: List<GoalData>) {
        goals.forEachIndexed { index, goalData ->
            update("/goals/${Auth.getUserId()}/$index", goalData)
        }
    }

    fun addGoal(goal: GoalData, goalCount: Int) {
        insert("/goals/${Auth.getUserId()}/$goalCount", goal)
    }

    fun deleteGoal(goalOrder: Int) {
        delete("/goals/${Auth.getUserId()}/$goalOrder")
    }

    private fun delete(path: String) {
        Database.get().reference
                .child(path)
                .removeValue()
                .addOnFailureListener { logError(it) }
    }
    // Users:

    fun addCurrentUser(authProvider: String, photoUrl: String?, mInviteLinkHash: String?) {
        val user = User().apply { name = Auth.getUsername(); email = Auth.getEmail(); this.photoUrl = photoUrl; this.authProvider = authProvider; currentTabId = Tab.OVERVIEW; inviteLinkHash = mInviteLinkHash }
        insert("goals/${Auth.getUserId()}/${todayBegin()}", user)
    }

    private fun insert(path: String, data: Any): String {
        val newReference = Database.get().reference
                .child(path)
                .push()
        newReference.setValue(data)
                .addOnFailureListener { logError(it) }
        return newReference.key
    }
    private fun update(path: String, data: DatabaseModel, databaseWriteCallback: (() -> Unit)? = null) {
        update(path, data.toMap(), databaseWriteCallback)
    }

    private fun update(path: String, data: Map<String, Any>, databaseWriteCallback: (() -> Unit)? = null) {
        val task = Database.get().reference
                .child(path)
                .updateChildren(data)
                .addOnFailureListener { logError(it) }
        if (databaseWriteCallback != null) {
            task.addOnSuccessListener { databaseWriteCallback() }
        }
    }
}