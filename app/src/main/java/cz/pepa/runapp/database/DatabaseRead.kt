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
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object DatabaseRead {

    fun user(): Flowable<Optional<User>> {
        return DatabaseQuery().apply { path = "users/${Auth.getUserId()}" }
                .observe()
                .toObjectObservable(User::class.java)
    }

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

    fun connected(): Flowable<Boolean> {
        return DatabaseQuery().apply { path = ".info/connected" }
                .observe()
                .toPrimitiveObservable(Boolean::class.java)
                .map { checkNotNull(it.toNullable(), { "Connected is null" }) }
    }
}