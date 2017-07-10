package io.stepuplabs.settleup.firebase.database

import io.stepuplabs.settleup.R
import io.stepuplabs.settleup.calculation.*
import io.stepuplabs.settleup.calculation.debts.MinimizedDebtCalculator
import io.stepuplabs.settleup.calculation.debts.NotMinimizedDebtCalculator
import io.stepuplabs.settleup.model.*
import io.stepuplabs.settleup.model.derived.Circle
import io.stepuplabs.settleup.model.derived.MemberAmount
import io.stepuplabs.settleup.util.Preferences
import io.stepuplabs.settleup.util.extensions.*
import rx.Observable
import java.math.BigDecimal
import java.util.*

/**
 * Combining multiple database queries and calculations.
 *
 * @author David Vávra (david@stepuplabs.io)
 */
object DatabaseCombine {

    val DEFAULT_CURRENCIES = listOf("USD", "EUR", "INR")
    val BASIC_COLORS = listOf("#FF9800", "#E91E63", "#2196F3", "#4CAF50", "#795548")
    val PREMIUM_COLORS = listOf("#DBAF00", "#FF9800", "#F44336", "#E91E63", "#00BCD4", "#2196F3", "#4C62E2", "#7E57C2", "#009688", "#4CAF50", "#795548", "#607D8B")

    fun groupTabData(groupId: String): Observable<GroupTabData?> {
        return combineLatest(
                DatabaseRead.groupMinimizeDebts(groupId),
                DatabaseRead.groupCurrency(groupId),
                DatabaseRead.members(groupId),
                DatabaseRead.transactions(groupId),
                DatabaseRead.latestExchangeRates(),
                DatabaseRead.userMember(groupId),
                DatabaseCombine.groupColor(groupId),
                DatabaseRead.isReadOnly(groupId), {
            minimizeDebts, currency, members, transactions, latestExchangeRates, userMember, color, readOnly ->
            val group = Group().apply { convertedToCurrency = currency }
            val balances = BalanceCalculator.calculate(transactions, members, group, latestExchangeRates)
            val debtsResult = if (minimizeDebts) {
                MinimizedDebtCalculator.calculate(balances, members)
            } else {
                NotMinimizedDebtCalculator.calculate(transactions, members, group, latestExchangeRates)
            }
            val debts = debtsResult.map {
                DebtItem(it, members.findById(it.from), members.findById(it.to), userMember, currency, color, groupId, readOnly)
            }
            val totalPaid = TotalPaidCalculator.calculate(transactions, group, latestExchangeRates)
            val width = R.dimen.circles_height.resToPx()
            val circlesResult = CircleCalculator.calculate(balances, width, width)
            val circles = CirclesResult(balances, circlesResult.sortedBy { it.size }, members, currency, transactions.size, groupId)
            val whoShouldPay = if (balances.areZero()) {
                "ANYONE"
            } else {
                WhoShouldPayCalculator.calculate(transactions, members, group, latestExchangeRates).name
            }
            GroupTabData(debts, totalPaid, circles, whoShouldPay, readOnly, groupId)
        })
    }

    fun groupCircles(groupId: String): Observable<CirclesResult?> {
        return combineLatest(
                DatabaseRead.groupCurrency(groupId),
                DatabaseRead.members(groupId),
                DatabaseRead.transactions(groupId),
                DatabaseRead.latestExchangeRates(), {
            currency, members, transactions, latestExchangeRates ->
            val group = Group().apply { convertedToCurrency = currency }
            val balances = BalanceCalculator.calculate(transactions, members, group, latestExchangeRates)
            val width = R.dimen.circles_height.resToPx()
            val circles = CircleCalculator.calculate(balances, width, width)
            CirclesResult(balances, circles.sortedBy { it.size }, members, currency, transactions.size, groupId)
        })
    }

    fun overviewCircles(): Observable<OverviewCirclesResult?> {
        return DatabaseRead.userGroups()
                .mapSubQueries { DatabaseRead.group(it.getId()) }
                .joinSubQueries()
                .mapSubQueries {
                    combineLatest(
                            DatabaseRead.groupColor(it.getId()),
                            DatabaseRead.members(it.getId()),
                            DatabaseRead.userMember(it.getId()),
                            DatabaseRead.transactions(it.getId()),
                            DatabaseRead.latestExchangeRates(), {
                        color, members, userMember, transactions, latestExchangeRates ->
                        val group = Group().apply { convertedToCurrency = it.convertedToCurrency }
                        val balances = BalanceCalculator.calculate(transactions, members, group, latestExchangeRates)
                        val balance = balances.findById(userMember)
                        val result = if (balance == null) {
                            OverviewCircleBalance(MemberAmount(it.getId(), BigDecimal.ZERO), it.name, isUserMemberDefined = false, groupId = it.getId(), currency = it.convertedToCurrency, color = color, latestExchangeRates = latestExchangeRates)
                        } else {
                            OverviewCircleBalance(MemberAmount(it.getId(), balance.amount), it.name, isUserMemberDefined = true, groupId = it.getId(), currency = it.convertedToCurrency, color = color, latestExchangeRates = latestExchangeRates)
                        }
                        result
                    })
                }
                .joinSubQueries()
                .map {
                    if (it == null) {
                        return@map null
                    }
                    val balances = it.map {
                        // Overview circles size is compared with other circles when converted to USD
                        MemberAmount(it.balance.memberId, it.balance.amount.convertWithLatest(it.currency, "USD", it.latestExchangeRates))
                    }
                    val colorMap = it.map {
                        it.groupId to it.color
                    }.toMap()
                    val width = R.dimen.circles_height.resToPx()
                    val circles = CircleCalculator.calculate(balances, width, width)
                    circles.forEach {
                        val circleColor = colorMap[it.memberId] ?: "#FFFFFF" // +X circle
                        it.color = "#59${circleColor.removePrefix("#")}" // 35% opacity
                    }
                    val overviewCirclesInfo = it.map { OverviewCircleInfo(it.groupId, it.groupName, it.balance.amount, it.currency, it.isUserMemberDefined) }
                    OverviewCirclesResult(overviewCirclesInfo, circles.sortedBy { it.size })
                }
    }

    fun balances(groupId: String): Observable<List<MemberAmount>?> {
        return combineLatest(
                DatabaseRead.groupCurrency(groupId),
                DatabaseRead.members(groupId),
                DatabaseRead.transactions(groupId),
                DatabaseRead.latestExchangeRates(), {
            currency, members, transactions, latestExchangeRates ->
            val group = Group().apply { convertedToCurrency = currency }
            BalanceCalculator.calculate(transactions, members, group, latestExchangeRates)
        })
    }

    fun availableCurrencies(groupId: String): Observable<List<String>?> {
        return DatabaseRead.transactions(groupId).map {
            if (it == null) {
                return@map DEFAULT_CURRENCIES
            }
            val currencies = it.map { it.currencyCode }.toMutableSet()
            currencies.add(Locale.getDefault().currency())
            currencies.addAll(DEFAULT_CURRENCIES)
            currencies.toList()
        }
    }

    fun last5Changes(): Observable<List<Change>?> {
        return DatabaseRead.userGroups()
                .mapSubQueries { DatabaseRead.last5Changes(it.getId()) }
                .flatMap {
                    Observable.combineLatest(it, {
                        it.mergeChanges().take(5)
                    })
                }
    }

    fun changesCountBeforeMonth(month: Date): Observable<Int?> {
        val lastLoadedMonth = month.firstDayOfMonth().toMillis().toDouble()
        return DatabaseRead.userGroups()
                .mapSubQueries { DatabaseRead.changesBeforeMonth(it.getId(), lastLoadedMonth) }
                .flatMap {
                    Observable.combineLatest(it, {
                        var sum: Int = 0
                        it.forEach {
                            sum += it as Int
                        }
                        sum
                    })
                }
    }

    fun isMemberPartOfAnyTransaction(groupId: String, memberId: String): Observable<Boolean?> {
        var isMemberPartOfAnyTransaction = false
        return DatabaseRead.transactions(groupId).map {
            if (it == null) {
                return@map false
            }
            for (transaction in it) {
                if (transaction.whoPaidMemberIds().contains(memberId) || transaction.forWhomMemberIds().contains(memberId)) {
                    isMemberPartOfAnyTransaction = true
                    break
                }
            }
            isMemberPartOfAnyTransaction
        }
    }

    fun permissionsUsers(groupId: String): Observable<PermissionsUsers?> {
        return DatabaseRead.permissions(groupId)
                .flatMap {
                    Observable.from(it).map {
                        combineLatest(DatabaseRead.user(it.getId()), Observable.just(it.level), ::UserLevel)
                    }.toList()
                }
                .flatMap {
                    Observable.combineLatest(it) {
                        var owner: User? = null
                        val editPermissions = mutableListOf<User>()
                        val readOnlyPermissions = mutableListOf<User>()
                        it.forEach {
                            it as UserLevel
                            when (it.level) {
                                Permission.LEVEL_OWNER -> owner = it.user
                                Permission.LEVEL_WRITE -> editPermissions.add(it.user)
                                Permission.LEVEL_READONLY -> readOnlyPermissions.add(it.user)
                            }
                        }
                        PermissionsUsers(checkNotNull(owner, { "Owner doesn't exist" }), editPermissions.sortedBy(User::name), readOnlyPermissions.sortedBy(User::name))
                    }
                }
    }

    fun owner(groupId: String): Observable<UserColorPremium?> {
        return DatabaseRead.permissions(groupId)
                .map {
                    if (it == null) {
                        return@map null
                    }
                    it.first { it.level == Permission.LEVEL_OWNER }
                }
                .flatMap {
                    if (it == null) {
                        return@flatMap null
                    }
                    combineLatest(DatabaseRead.user(it.getId()), DatabaseRead.groupColor(groupId), DatabaseCombine.plan(), {
                        user, groupColor, (isPremium) ->
                        UserColorPremium(user, groupColor, isPremium)
                    })
                }
    }

    fun nextDefaultGroupColor(): Observable<String?> {
        return combineLatest(DatabaseRead.userGroups(), DatabaseCombine.plan(), {
            userGroups, (isPremium) ->
            val availableColors = if (isPremium) PREMIUM_COLORS else BASIC_COLORS
            val usedColors = userGroups.map { it.color }
            val notUsedColors = availableColors
                    .filter {
                        !usedColors.contains(it)
                    }
            if (notUsedColors.isEmpty()) {
                return@combineLatest availableColors.randomElement()
            } else {
                return@combineLatest notUsedColors.randomElement()
            }
        })
    }

    fun nextGroupOrder(): Observable<Int?> {
        return DatabaseRead.userGroups()
                .map {
                    if (it == null || it.isEmpty()) {
                        return@map 0
                    } else {
                        return@map checkNotNull(it.map { it.order }.min(), { "Groups are empty" }).minus(1)
                    }
                }
    }

    fun groupColor(groupId: String): Observable<Int?> {
        return DatabaseRead.doesUserGroupExist(groupId)
                .flatMap {
                    if (it == null || !it) {
                        DatabaseRead.ownerGroupColor(groupId)
                    } else {
                        DatabaseRead.groupColor(groupId)
                    }
                }.map {
            if (it == null) {
                return@map null
            }
            it.toColor()
        }
    }

    fun groupCurrency(groupId: String): Observable<CurrencyInfo?> {
        return combineLatest(DatabaseRead.groupCurrency(groupId), availableCurrencies(groupId), ::CurrencyInfo)
    }

    fun groupsAndUser(): Observable<GroupsUser?> {
        return combineLatest(DatabaseListItems.groups(), DatabaseRead.user(), ::GroupsUser)
    }

    fun newTransactionRequirements(groupId: String): Observable<NewTransactionRequirements?> {
        return combineLatest(DatabaseRead.members(groupId),
                DatabaseRead.userMember(groupId),
                DatabaseRead.groupCurrency(groupId),
                DatabaseRead.lastTransaction(groupId),
                DatabaseRead.latestExchangeRates(),
                DatabaseCombine.plan(), ::NewTransactionRequirements)
    }

    fun editTransactionRequirements(groupId: String, transactionId: String): Observable<EditTransactionRequirements?> {
        return combineLatest(DatabaseRead.members(groupId),
                DatabaseRead.userMember(groupId),
                DatabaseRead.groupCurrency(groupId),
                DatabaseRead.transaction(groupId, transactionId),
                DatabaseRead.isReadOnly(groupId),
                DatabaseRead.latestExchangeRates(),
                DatabaseCombine.plan(), ::EditTransactionRequirements)
    }

    fun changesForMonthAndBefore(month: Date): Observable<ChangesForMonthAndBefore?> {
        return combineLatest(DatabaseListItems.changesForMonth(month), DatabaseCombine.changesCountBeforeMonth(month), ::ChangesForMonthAndBefore)
    }

    fun isMemberUser(groupId: String, memberId: String): Observable<Boolean?> {
        return DatabaseRead.userMember(groupId).map {
            memberId == it
        }
    }

    fun readOnlyAndTransactionCount(groupId: String): Observable<ReadOnlyAndTransactionCount?> {
        return combineLatest(DatabaseRead.isReadOnly(groupId), DatabaseRead.transactionCount(groupId), ::ReadOnlyAndTransactionCount)
    }

    fun plan(): Observable<Plan?> {
        return combineLatest(Preferences.getServerTimeOffset(), DatabaseRead.subscriptions(), {
            serverTimeOffset, subscriptions ->
            val serverTime = Date().time + serverTimeOffset
            val validSubscription = subscriptions
                    .sortedBy { it.order() }
                    .find { it.start < serverTime && serverTime < it.end }
            if (validSubscription == null) {
                return@combineLatest Plan(isPremium = false)
            } else {
                return@combineLatest Plan(isPremium = true, premiumType = validSubscription.type, premiumExpiration = validSubscription.end)
            }
        }, Plan(isPremium = false))
    }

}

data class CirclesResult(val balances: List<MemberAmount>, val circles: List<Circle>, val members: List<Member>, val currency: String, val transactionCount: Int, val groupId: String)

data class OverviewCircleBalance(val balance: MemberAmount, val groupName: String, val isUserMemberDefined: Boolean, val groupId: String, val currency: String, val color: String, val latestExchangeRates: Map<String, String>)

data class OverviewCircleInfo(val groupId: String, val groupName: String, val amount: BigDecimal, val currency: String, val userMemberDefined: Boolean)

data class OverviewCirclesResult(val overviewCirclesInfo: List<OverviewCircleInfo>, val circles: List<Circle>)

data class PermissionsUsers(val owner: User, val editPermissions: List<User>, val readOnlyPermissions: List<User>)

data class UserLevel(val user: User, val level: Int)

data class CurrencyInfo(val currency: String, val availableCurrencies: List<String>)

data class UserColorPremium(val user: User, val color: String, val isPremium: Boolean)

data class GroupsUser(val groups: List<GroupItem>, val user: User)

data class EditTransactionRequirements(val members: List<Member>, val userMember: String?, val currency: String, val transaction: Transaction, val readOnly: Boolean, val latestExchangeRates: Map<String, String>, val plan: Plan)

data class NewTransactionRequirements(val members: List<Member>, val userMember: String?, val currency: String, val lastTransaction: Transaction, val latestExchangeRates: Map<String, String>, val plan: Plan)

data class ChangesForMonthAndBefore(val changes: List<ChangeItem>, val before: Int)

data class ReadOnlyAndTransactionCount(val readOnly: Boolean, val transactionCount: Int)

data class GroupTabData(val debts: List<DebtItem>, val totalPaid: BigDecimal, val circles: CirclesResult, val whoShouldPayName: String, val readOnly: Boolean, val groupId: String)

data class Plan(val isPremium: Boolean, val premiumType: String? = null, val premiumExpiration: Long? = null)

@Suppress("UNCHECKED_CAST")
fun Array<Any>.mergeChanges(): List<Change> {
    var list = mutableListOf<Change>()
    this.filterNotNull().forEach {
        list.addAll(it as List<Change>)
    }
    // Filter out changes not fit for UI (only for windows sync)
    list = list.filter(Change::shouldBeDisplayed).toMutableList()
    list.sortByDescending { it.serverTimestamp }
    return list
}
