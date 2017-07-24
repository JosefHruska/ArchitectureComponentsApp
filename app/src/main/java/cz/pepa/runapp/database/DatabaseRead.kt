package cz.pepa.runapp.database

import com.gojuno.koptional.Optional
import cz.pepa.runapp.data.GoalData
import cz.pepa.runapp.data.TodayItem
import cz.pepa.runapp.data.User
import cz.pepa.runapp.data.UserInfo
import cz.pepa.runapp.logic.Auth
import io.reactivex.Flowable
import io.stepuplabs.settleup.util.extensions.todayBegin


/**
 * Main class for reading from Firebase Database.
 *
 * @author David Vávra (david@stepuplabs.io)
 */
object DatabaseRead {
    //
//    // Users:
//
    fun user(): Flowable<Optional<User>> {
        return DatabaseQuery().apply { path = "users/${Auth.getUserId()}" }
                .observe()
                .toObjectObservable(User::class.java)
    }

//    fun today(mToday: MutableLiveData<TodayItem> ): MutableLiveData<TodayItem> {
//        DatabaseQuery().apply { path = "days/${Auth.getUserId()}/${todayBegin()}" }
//                .observe()
//                .toPrimitiveObservable(TodayItem::class.java).map { mToday.value = it }
//        return mToday
//    }

    fun today(): Flowable<Optional<TodayItem>> {
        return DatabaseQuery().apply { path = "days/${Auth.getUserId()}/${todayBegin()}" }
                .observe()
                .toPrimitiveObservable(TodayItem::class.java)
    }

    fun lastWeek(): Flowable<Optional<List<TodayItem>>> {
        return DatabaseQuery().apply { path = "days/${Auth.getUserId()}"; orderByChild = "dayStart"; limitToLast = 7}
                .observe()
                .toListObservable(TodayItem::class.java)
    }

    fun lastMonth(): Flowable<Optional<List<TodayItem>>> {
        return DatabaseQuery().apply { path = "days/${Auth.getUserId()}"; orderByChild = "dayStart"; limitToLast = 30}
                .observe()
                .toListObservable(TodayItem::class.java)
    }

    fun userInfo(): Flowable<Optional<UserInfo>> {
        return DatabaseQuery().apply { path = "userInfo/${Auth.getUserId()}" }
                .observe()
                .toPrimitiveObservable(UserInfo::class.java)
    }

    fun goals(): Flowable<Optional<List<GoalData>>> {
        return DatabaseQuery().apply { path = "goals/${Auth.getUserId()}" }
                .observe()
                .toListObservable(GoalData::class.java)
    }
//
//    fun userPhotoUrl(): Observable<String?> {
//        return DatabaseQuery().apply { path = "users/${Auth.getUserId()}/photoUrl" }
//                .observe()
//                .toPrimitiveObservable(String::class.java)
//    }
//
//    fun user(userId: String?): Observable<User?> {
//        return DatabaseQuery().apply { path = "users/$userId" }
//                .observe()
//                .toObjectObservable(User::class.java)
//    }
//
//    // User groups:
//
//    fun userGroups(): Observable<List<UserGroup>?> {
//        return DatabaseQuery().apply { path = "userGroups/${Auth.getUserId()}"; orderByChild = "order" }
//                .observe()
//                .toListObservable(UserGroup::class.java)
//    }
//
//    fun userMember(groupId: String): Observable<String?> {
//        return DatabaseQuery().apply { path = "userGroups/${Auth.getUserId()}/$groupId/member" }
//                .observe()
//                .toPrimitiveObservable(String::class.java)
//                .map {
//                    if (it == null) {
//                        return@map "UNKNOWN" // We can't pass null because we have some logic on whether this exists or not
//                    }
//                    it
//                }
//    }
//
//    fun groupColor(groupId: String): Observable<String?> {
//        return DatabaseQuery().apply { path = "userGroups/${Auth.getUserId()}/$groupId/color" }
//                .observe()
//                .toPrimitiveObservable(String::class.java)
//    }
//
//    fun doesUserGroupExist(groupId: String): Observable<Boolean?> {
//        if (!Auth.isSignedIn()) {
//            // Fixes occasional crash in sign out
//            return Observable.just(false)
//        }
//        return DatabaseQuery().apply { path = "userGroups/${Auth.getUserId()}/$groupId" }
//                .exists()
//    }
//
//    fun userGroupCountOnce(): Observable<Long> {
//        return DatabaseQuery().apply { path = "userGroups/${Auth.getUserId()}"; }
//                .observeCountOnce()
//    }
//
//    // Groups:
//
//    fun group(groupId: String): Observable<Group?> {
//        return DatabaseQuery().apply { path = "groups/$groupId" }
//                .observe()
//                .toObjectObservable(Group::class.java)
//    }
//
//    fun ownerGroupColor(groupId: String): Observable<String?> {
//        return DatabaseQuery().apply { path = "groups/$groupId/ownerColor" }
//                .observe()
//                .toPrimitiveObservable(String::class.java)
//    }
//
//    fun groupCurrency(groupId: String): Observable<String?> {
//        return DatabaseQuery().apply { path = "groups/$groupId/convertedToCurrency" }
//                .observe()
//                .toPrimitiveObservable(String::class.java)
//    }
//
//    fun groupName(groupId: String): Observable<String?> {
//        return DatabaseQuery().apply { path = "groups/$groupId/name" }
//                .observe()
//                .toPrimitiveObservable(String::class.java)
//    }
//
//    fun groupLink(groupId: String): Observable<String?> {
//        return DatabaseQuery().apply { path = "groups/$groupId/inviteLink" }
//                .observe()
//                .toPrimitiveObservable(String::class.java)
//    }
//
//    fun groupLinkEnabled(groupId: String): Observable<Boolean?> {
//        return DatabaseQuery().apply { path = "groups/$groupId/inviteLinkActive" }
//                .observe()
//                .toPrimitiveObservable(Boolean::class.java)
//    }
//
//    fun groupMinimizeDebts(groupId: String): Observable<Boolean?> {
//        return DatabaseQuery().apply { path = "groups/$groupId/minimizeDebts" }
//                .observe()
//                .toPrimitiveObservable(Boolean::class.java)
//    }
//
//    fun legacyGroups(): Observable<List<LegacyGroup>?> {
//        return DatabaseQuery().apply { path = "legacyGroups/${Auth.getUserId()}" }
//                .observe()
//                .toListObservable(LegacyGroup::class.java)
//    }
//
//    // Members:
//
//    fun members(groupId: String): Observable<List<Member>?> {
//        return DatabaseQuery().apply { path = "members/$groupId"; orderByChild = "name" }
//                .observe()
//                .toListObservable(Member::class.java)
//    }
//
//    fun membersCount(groupId: String): Observable<Int?> {
//        return DatabaseQuery().apply { path = "members/$groupId" }
//                .observe()
//                .toCountObservable()
//    }
//
//    fun member(groupId: String, memberId: String): Observable<Member?> {
//        return DatabaseQuery().apply { path = "members/$groupId/$memberId" }
//                .observe()
//                .toObjectObservable(Member::class.java)
//    }
//
//    // Transactions:
//
//    fun transactions(groupId: String): Observable<List<Transaction>?> {
//        return DatabaseQuery().apply { path = "transactions/$groupId"; orderByChild = "dateTime" }
//                .observe()
//                .toListObservable(Transaction::class.java)
//    }
//
//    fun expenseCount(groupId: String): Observable<Int?> {
//        return DatabaseQuery().apply { path = "transactions/$groupId"; orderByChild = "type"; equalTo = Transaction.TYPE_EXPENSE }
//                .observe()
//                .toCountObservable()
//    }
//
//    fun transactionCount(groupId: String): Observable<Int?> {
//        return DatabaseQuery().apply { path = "transactions/$groupId"; }
//                .observe()
//                .toCountObservable()
//    }
//
//    fun last5Transactions(groupId: String): Observable<List<Transaction>?> {
//        return DatabaseQuery().apply { path = "transactions/$groupId"; orderByChild = "dateTime"; limitToLast = 5 }
//                .observe()
//                .toListObservable(Transaction::class.java)
//    }
//
//    fun transaction(groupId: String, transactionId: String): Observable<Transaction?> {
//        return DatabaseQuery().apply { path = "transactions/$groupId/$transactionId" }
//                .observe()
//                .toObjectObservable(Transaction::class.java)
//    }
//
//    fun lastTransaction(groupId: String): Observable<Transaction?> {
//        return DatabaseQuery().apply { path = "transactions/$groupId"; limitToLast = 1 }
//                .observe()
//                .toListObservable(Transaction::class.java)
//                .map { if (it == null || it.isEmpty()) Transaction().apply { setId("UNKNOWN") } else it[0] } // We can't use null because we have logic whether it exists or not
//    }
//
//    // Exchange rates:
//
//    fun latestExchangeRates(): Observable<Map<String, String>?> {
//        return DatabaseQuery().apply { path = "exchangeRatesToUsd/latest" }
//                .observe()
//                .toMapObservable()
//    }
//
//    fun exchangeRates(date: String): Observable<Map<String, String>?> {
//        return DatabaseQuery().apply { path = "exchangeRatesToUsd/$date" }
//                .observe()
//                .toMapObservable()
//    }
//
//    // Changes:
//
//    fun last5Changes(groupId: String): Observable<List<Change>> {
//        return DatabaseQuery().apply { path = "changes/$groupId"; limitToLast = 5 }
//                .observe()
//                .toChangesObservable(groupId)
//    }
//
//    fun changes(groupId: String, startDate: Double, endDate: Double): Observable<List<Change>> {
//        return DatabaseQuery().apply { path = "changes/$groupId"; startAt = startDate; endAt = endDate; orderByChild = "serverTimestamp" }
//                .observe()
//                .toChangesObservable(groupId)
//    }
//
//    fun changesBeforeMonth(groupId: String, month: Double): Observable<Int?> {
//        return DatabaseQuery().apply { path = "changes/$groupId"; startAt = 0.0; endAt = month; orderByChild = "serverTimestamp" }
//                .observe()
//                .toCountObservable()
//    }
//
//    // Permissions:
//
//    fun permissions(groupId: String): Observable<List<Permission>?> {
//        return DatabaseQuery().apply { path = "permissions/$groupId" }
//                .observe()
//                .toListObservable(Permission::class.java)
//    }
//
//    fun isReadOnly(groupId: String): Observable<Boolean?> {
//        return DatabaseQuery().apply { path = "permissions/$groupId/${Auth.getUserId()}/level" }
//                .observe()
//                .toPrimitiveObservable(Int::class.java)
//                .map {
//                    if (it == null) {
//                        return@map true
//                    } else {
//                        return@map it < Permission.LEVEL_WRITE
//                    }
//                }
//    }
//
//    // Server tasks:
//
//    fun serverTaskResponse(name: String, taskId: String): Observable<ServerTaskResponse?> {
//        return DatabaseQuery().apply { path = "serverTasks/$name/$taskId/response" }
//                .observe()
//                .toObjectObservable(ServerTaskResponse::class.java)
//    }
//
//    // Statistics:
//
//    fun statistics(): Observable<Statistics?> {
//        return DatabaseQuery().apply { path = "statistics/public" }
//                .observe()
//                .toObjectObservable(Statistics::class.java)
//    }
//
//    // Subscriptions:
//
//    fun subscriptions(): Observable<List<Subscription>?> {
//        return DatabaseQuery().apply { path = "subscriptions/${Auth.getUserId()}" }
//                .observe()
//                .toListObservable(Subscription::class.java)
//    }
//
//    // Internal state:
//
fun connected(): Flowable<Boolean> {
        return DatabaseQuery().apply { path = ".info/connected" }
                .observe()
                .toPrimitiveObservable(Boolean::class.java)
                .map { checkNotNull(it.toNullable(), { "Connected is null" }) }
    }

//
//    fun serverTimeOffset(): Observable<Long?> {
//        return DatabaseQuery().apply { path = ".info/serverTimeOffset" }
//                .observe()
//                .toPrimitiveObservable(Long::class.java)
//    }
}
