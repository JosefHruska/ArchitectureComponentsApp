
package cz.pepa.runapp.database

import cz.pepa.runapp.data.*
import cz.pepa.runapp.logic.Auth
import io.stepuplabs.settleup.util.extensions.todayBegin
import logError


/**
// * Main class for writing to Firebase Database.
// *
// * @author David Vávra (david@stepuplabs.io)
// */
object DatabaseWrite {
//
//    const val DEFAULT_SERVER_TASK_TIMEOUT = 20 // seconds
//    const val MIGRATE_LATEST_TIMEOUT = 50 // seconds
//    const val CONNECT_FIREBASE_TIMEOUT = 30 // seconds
//
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

    fun updateGoals(goals: List<GoalData>) {
        goals.forEachIndexed { index, goalData ->
            update("/goals/${Auth.getUserId()}/$index", goalData)
        }
    }



//    // Transactions:
//
//    fun addTransaction(groupId: String, transaction: Transaction): String {
//        return insert("transactions/$groupId", transaction)
//    }
//
//    fun changeTransaction(groupId: String, mTransaction: Transaction) {
//        update("transactions/$groupId/${mTransaction.getId()}", mTransaction)
//    }
//
//    fun deleteTransaction(groupId: String, transactionId: String) {
//        delete("transactions/$groupId/$transactionId")
//    }
//
//    fun updateTransactionReceiptUrl(groupId: String, transactionId: String, photoUrl: String) {
//        updateSingleValue("transactions/$groupId/$transactionId/receiptUrl", photoUrl)
//    }
//
//    fun deleteAllTransactions(groupId: String, presenter: BasePresenter<*>, @StringRes progressReason: Int, successCallback: (ServerTaskResponse) -> Unit) {
//        runServerTask("deleteTransactions", GroupServerTask(groupId), presenter, progressReason, successCallback)
//    }
//
//    // Groups:
//
//    fun getNextGroupId(): String {
//        return getNextId("groups")
//    }
//
//    fun addGroup(groupId: String, group: Group) {
//        update("groups/$groupId", group)
//    }
//
//    fun changeCurrentTabId(tabId: String) {
//        updateSingleValue("users/${Auth.getUserId()}/currentTabId", tabId, null, addIfOffline = false)
//    }
//
//    fun changeGroupsOrder(uiGroup: List<GroupItem>) {
//        val changes = uiGroup.map { Pair("${it.userGroup.getId()}/order", it.userGroup.order) }.toMap()
//        update("userGroups/${Auth.getUserId()}", changes)
//    }
//
//    fun addUserGroup(groupId: String, userGroup: UserGroup) {
//        update("userGroups/${Auth.getUserId()}/$groupId", userGroup)
//    }
//
//    fun changeInviteLinkEnabled(groupId: String, enabled: Boolean) {
//        updateSingleValue("groups/$groupId/inviteLinkActive", enabled)
//    }
//
//    fun changeGroup(groupId: String, minimizeDebts: Boolean, groupName: String) {
//        val changes = mapOf("groups/$groupId/minimizeDebts" to minimizeDebts, "groups/$groupId/name" to groupName)
//        update("", changes)
//    }
//
//    fun changeOwnerColor(groupId: String, color: String) {
//        val changes = mapOf("groups/$groupId/ownerColor" to color, "userGroups/${Auth.getUserId()}/$groupId/color" to color)
//        update("", changes)
//    }
//
//    fun deleteGroup(groupId: String, presenter: BasePresenter<*>, @StringRes progressReason: Int, successCallback: (ServerTaskResponse) -> Unit) {
//        runServerTask("deleteGroup", GroupServerTask(groupId), presenter, progressReason, successCallback)
//    }
//
//    fun changeGroupCurrency(groupId: String, currencyCode: String, presenter: BasePresenter<*>, @StringRes progressReason: Int) {
//        runServerTask("currencyChange", ChangeCurrencyServerTask(groupId, currencyCode), presenter, progressReason)
//    }
//
//    fun migrateLatest(pendingChanges: List<MigrateServerTask.PendingChange>, presenter: BasePresenter<*>, @StringRes progressReason: Int, successCallback: (ServerTaskResponse) -> Unit) {
//        runServerTask("migrateLegacyGroups", MigrateServerTask("latest", pendingChanges), presenter, progressReason, successCallback, MIGRATE_LATEST_TIMEOUT)
//    }
//
//    fun migrateLegacyGroup(appengineId: String, presenter: BasePresenter<*>, @StringRes progressReason: Int) {
//        runServerTask("migrateLegacyGroups", MigrateServerTask(appengineId), presenter, progressReason)
//    }
//
//    fun removeLegacyGroup(appengineId: String, presenter: BasePresenter<*>, @StringRes progressReason: Int) {
//        runServerTask("removeLegacyGroup", MigrateServerTask(appengineId), presenter, progressReason)
//    }
//
    // Users:

    fun addCurrentUser(authProvider: String, photoUrl: String?, mInviteLinkHash: String?) {
        val user = User().apply { name = Auth.getUsername(); email = Auth.getEmail(); this.photoUrl = photoUrl; this.authProvider = authProvider; currentTabId = Tab.OVERVIEW; inviteLinkHash = mInviteLinkHash }
        update("users/${Auth.getUserId()}/${todayBegin()}", user)
    }
//
//    fun addOwnerPermission(groupId: String) {
//        update("permissions/$groupId/${Auth.getUserId()}", Permission().apply { level = Permission.LEVEL_OWNER })
//    }
//
//    fun addEditPermission(groupId: String, databaseWriteCallback: (() -> Unit)) {
//        update("permissions/$groupId/${Auth.getUserId()}", Permission().apply { level = Permission.LEVEL_WRITE }, databaseWriteCallback)
//    }
//
//    fun removePermission(groupId: String, userId: String) {
//        delete("userGroups/$userId/$groupId")
//        delete("permissions/$groupId/$userId")
//    }
//
//    fun makeReadOnly(groupId: String, userId: String) {
//        updateSingleValue("permissions/$groupId/$userId/level", Permission.LEVEL_READONLY)
//    }
//
//    fun transferOwnership(groupId: String, newOwnerId: String) {
//        val changes = mapOf("$newOwnerId/level" to Permission.LEVEL_OWNER, "${Auth.getUserId()}/level" to Permission.LEVEL_WRITE)
//        update("permissions/$groupId", changes)
//    }
//
//    fun allowEditing(groupId: String, userId: String) {
//        updateSingleValue("permissions/$groupId/$userId/level", Permission.LEVEL_WRITE)
//    }
//
//    fun changeInviteLinkHash(hash: String, databaseWriteCallback: (() -> Unit)) {
//        updateSingleValue("users/${Auth.getUserId()}/inviteLinkHash", hash, databaseWriteCallback)
//    }
//
//    fun changeUserColor(groupId: String, color: String) {
//        updateSingleValue("userGroups/${Auth.getUserId()}/$groupId/color", color)
//    }
//
//    // Push:
//
//    fun registerPushToken(token: String) {
//        val pushRegistration = PushRegistration()
//        val userId = Auth.getUserId()
//        update("pushRegistrations/$userId/$token", pushRegistration)
//    }
//
//    fun unregisterPushToken(token: String) {
//        val userId = Auth.getUserId()
//        delete("pushRegistrations/$userId/$token")
//    }
//
//    // Members:
//
//    fun addMember(groupId: String, member: Member): String {
//        return insert("members/$groupId", member)
//    }
//
//    fun deleteMember(groupId: String, memberId: String) {
//        delete("members/$groupId/$memberId")
//    }
//
//    fun updateMemberPhotoUrl(groupId: String, memberId: String, url: String) {
//        updateSingleValue("members/$groupId/$memberId/photoUrl", url)
//    }
//
//    fun changeMember(groupId: String, member: Member) {
//        update("members/$groupId/${member.getId()}", member)
//    }
//
//    fun setMemberAsMe(groupId: String, memberId: String) {
//        updateSingleValue("userGroups/${Auth.getUserId()}/$groupId/member", memberId)
//    }
//
//    fun setMemberAsNotMe(groupId: String) {
//        delete("userGroups/${Auth.getUserId()}/$groupId/member")
//    }
//
//    // Subscriptions:
//
//    fun verifySubscription(type: String, token: String, presenter: BasePresenter<*>, @StringRes progressReason: Int, successCallback: (ServerTaskResponse) -> Unit) {
//        runServerTask("verifySubscription", VerifySubscriptionServerTask(type, token), presenter, progressReason, successCallback)
//    }
//
//    fun verifyInitialSubscription(update: Boolean, everythingReceipt: String? = null, presenter: BasePresenter<*>, @StringRes progressReason: Int, successCallback: (ServerTaskResponse) -> Unit) {
//        runServerTask("verifyInitialSubscription", VerifyInitialSubscriptionServerTask(update, everythingReceipt), presenter, progressReason, successCallback)
//    }
//
//    // Generic methods:
//
//    private fun getNextId(path: String): String {
//        return Database.get().reference
//                .child(path)
//                .push()
//                .key
//    }
//
//    private fun insert(path: String, data: Any): String {
//        val newReference = Database.get().reference
//                .child(path)
//                .push()
//        newReference.setValue(data)
//                .addOnFailureListener { logError(it) }
//        Offline.addChangeIfOffline()
//        return newReference.key
//    }
//
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
//        Offline.addChangeIfOffline()
    }
//
//    private fun updateSingleValue(path: String, data: Any, databaseWriteCallback: (() -> Unit)? = null, addIfOffline: Boolean = true) {
//        val task = Database.get().reference
//                .child(path)
//                .setValue(data)
//                .addOnFailureListener { logError(it) }
//        if (databaseWriteCallback != null) {
//            task.addOnSuccessListener { databaseWriteCallback() }
//        }
//        if (addIfOffline) {
//            Offline.addChangeIfOffline()
//        }
//    }
//
//    private fun delete(path: String) {
//        Database.get().reference
//                .child(path)
//                .removeValue()
//                .addOnFailureListener { logError(it) }
//        Offline.addChangeIfOffline()
//    }
//
//    private fun runServerTask(name: String, task: ServerTask, presenter: BasePresenter<*>, @StringRes progressReason: Int, successCallback: ((ServerTaskResponse) -> Unit)? = null, timeout: Int = DEFAULT_SERVER_TASK_TIMEOUT) {
//        if (isDeviceOnline()) {
//            Offline.doWhenDatabaseIsConnected(presenter) {
//                writeServerTask(name, task, presenter, progressReason, successCallback, timeout)
//            }
//        } else {
//            if (presenter.isViewAttached) {
//                presenter.getView().showServerTaskOfflineWarning()
//            }
//        }
//    }
//
//    private fun writeServerTask(name: String, task: ServerTask, presenter: BasePresenter<*>, @StringRes progressReason: Int, successCallback: ((ServerTaskResponse) -> Unit)?, timeout: Int) {
//        TimedBlockingProgress.start(progressReason, presenter, timeout)
//        val taskId = insert("serverTasks/$name", task)
//        var success = false
//        var error = false
//        val subscription = DatabaseRead.serverTaskResponse(name, taskId)
//                .take(2) // unsubscribe automatically after 2nd value
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribe({
//                    if (it != null && it.code == ServerTaskResponse.CODE_OK) {
//                        deleteServerTask(name, taskId, progressReason)
//                        success = true
//                        successCallback?.invoke(it)
//                    }
//                    if (it != null && it.code == ServerTaskResponse.CODE_ERROR) {
//                        deleteServerTask(name, taskId, progressReason)
//                        showServerTaskError(presenter)
//                        error = true
//                    }
//                }, {
//                    deleteServerTask(name, taskId, progressReason)
//                    showServerTaskError(presenter)
//                })
//        app().doLater(timeout * 1000.toLong()) {
//            if (!success) {
//                subscription.unsubscribe()
//                if (!error) { // timeout
//                    deleteServerTask(name, taskId, progressReason)
//                    showServerTaskError(presenter)
//                }
//            }
//        }
//    }
//
//    private fun deleteServerTask(name: String, taskId: String, @StringRes progressReason: Int) {
//        delete("serverTasks/$name/$taskId")
//        TimedBlockingProgress.stop(progressReason)
//    }
//
//    private fun showServerTaskError(presenter: BasePresenter<*>) {
//        if (presenter.isViewAttached) {
//            presenter.getView().showServerTaskError()
//        }
//    }
//
}