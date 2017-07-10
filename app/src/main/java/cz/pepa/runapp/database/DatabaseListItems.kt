package io.stepuplabs.settleup.firebase.database

import android.annotation.SuppressLint
import io.stepuplabs.settleup.calculation.BalanceCalculator
import io.stepuplabs.settleup.calculation.SpentAmountsCalculator
import io.stepuplabs.settleup.model.*
import io.stepuplabs.settleup.model.derived.Debt
import io.stepuplabs.settleup.model.derived.MemberAmount
import io.stepuplabs.settleup.util.extensions.*
import rx.Observable
import java.util.*

/**
 * Generates list items with all data binders need.
 *
 * @author David Vávra (david@stepuplabs.io)
 */
object DatabaseListItems {
    fun last5Changes(): Observable<List<ChangeItem>?> {
        return DatabaseCombine.last5Changes().joinChanges()
    }

    fun balances(groupId: String): Observable<List<MemberAmountItem>?> {
        return combineLatest(
                DatabaseRead.groupCurrency(groupId),
                DatabaseRead.members(groupId),
                DatabaseRead.userMember(groupId),
                DatabaseRead.transactions(groupId),
                DatabaseCombine.groupColor(groupId),
                DatabaseRead.latestExchangeRates()) {
            currency, members, userMember, transactions, groupColor, latestExchangeRates ->
            val group = Group().apply { convertedToCurrency = currency }
            val balances = BalanceCalculator.calculate(transactions, members, group, latestExchangeRates)
            MemberAmountsResult(balances, members, currency, userMember, groupColor)
        }
                .flatMap { result ->
                    if (result == null) {
                        return@flatMap null
                    }
                    Observable.from(result.memberAmount)
                            .map { Observable.just(MemberAmountItem(it, result.members.findById(it.memberId), result.userMember, result.currency, groupId, result.groupColor)) }
                            .toList()
                }
                .joinSubQueries()
                .map { it?.sortedBy { it.member.name } }
    }

    fun spentAmounts(groupId: String): Observable<List<MemberAmountItem>?> {
        return combineLatest(
                DatabaseRead.groupCurrency(groupId),
                DatabaseRead.members(groupId),
                DatabaseRead.userMember(groupId),
                DatabaseRead.transactions(groupId),
                DatabaseCombine.groupColor(groupId),
                DatabaseRead.latestExchangeRates()) {
            currency, members, userMember, transactions, groupColor, latestExchangeRates ->
            val group = Group().apply { convertedToCurrency = currency }
            val spentAmounts = SpentAmountsCalculator.calculate(transactions, members, group, latestExchangeRates)
            MemberAmountsResult(spentAmounts, members, currency, userMember, groupColor)
        }
                .flatMap { result ->
                    if (result == null) {
                        return@flatMap null
                    }
                    Observable.from(result.memberAmount)
                            .map { Observable.just(MemberAmountItem(it, result.members.findById(it.memberId), result.userMember, result.currency, groupId, result.groupColor)) }
                            .toList()
                }
                .joinSubQueries()
                .map { it?.sortedBy { it.member.name } }
    }

    fun groups(): Observable<List<GroupItem>?> {
        return DatabaseRead.userGroups().mapSubQueries {
            combineLatest(DatabaseRead.group(it.getId()), Observable.just(it), ::GroupItem)
        }.joinSubQueries()
    }

    fun allCurrencies(): Observable<List<CurrencyItem>?> {
        return DatabaseRead.latestExchangeRates().map {
            if (it == null) {
                return@map null
            }
            it.keys.map {
                var currencyName: String? = null
                if (isLargerOrEqualApi(19)) {
                    try {
                        @SuppressLint("NewApi")
                        currencyName = java.util.Currency.getInstance(it).displayName.capitalize()
                    } catch (ignored: IllegalArgumentException) {
                    }
                }
                CurrencyItem(it, currencyName)
            }.sortedBy { it.name ?: it.code }
        }
    }

    fun last5Transactions(groupId: String): Observable<List<TransactionItem>?> {
        return processTransactions(groupId) { DatabaseRead.last5Transactions(groupId) }
    }

    fun transactionsGroupedByMonth(groupId: String): Observable<Map<Date, List<TransactionItem>>?> {
        val members = DatabaseRead.members(groupId)
        val userMember = DatabaseRead.userMember(groupId)
        val groupColor = DatabaseCombine.groupColor(groupId)
        return DatabaseRead.transactions(groupId)
                .map {
                    if (it == null) {
                        return@map null
                    }
                    it.reversed()
                }
                .map {
                    if (it == null) {
                        return@map null
                    }
                    it.groupBy { it.dateTime.toDate().firstDayOfMonth() }
                }
                .flatMap { transactions ->
                    if (transactions == null) {
                        return@flatMap null
                    }
                    combineLatest(members, userMember, groupColor) {
                        members, userMember, color ->
                        transactions.map {
                            val newValue = it.value.map { TransactionItem(it, members, userMember, groupId, color) }
                            it.key to newValue
                        }.toMap()
                    }
                }
    }

    fun changesForMonth(month: Date): Observable<List<ChangeItem>?> {
        val firstDayOfMonth = month.firstDayOfMonth().toMillis().toDouble()
        val lastDayOfMonth = month.lastDayOfMonth().toMillis().toDouble()
        return DatabaseRead.userGroups()
                .mapSubQueries { DatabaseRead.changes(it.getId(), firstDayOfMonth, lastDayOfMonth) }
                .flatMap {
                    Observable.combineLatest(it, Array<Any>::mergeChanges)
                }.joinChanges()
    }

    fun transactions(groupId: String): Observable<List<TransactionItem>?> {
        return processTransactions(groupId) { DatabaseRead.transactions(groupId) }
    }

    private fun processTransactions(groupId: String, query: () -> Observable<List<Transaction>?>): Observable<List<TransactionItem>?> {
        val members = DatabaseRead.members(groupId)
        val userMember = DatabaseRead.userMember(groupId)
        val groupColor = DatabaseCombine.groupColor(groupId)
        return query()
                .map {
                    if (it == null) {
                        return@map null
                    }
                    it.reversed()
                }
                .flatMap {
                    transactions ->
                    if (transactions == null) {
                        return@flatMap null
                    }
                    combineLatest(members, userMember, groupColor) {
                        members, userMember, color ->
                        transactions.map { TransactionItem(it, members, userMember, groupId, color) }
                    }
                }
    }

    private fun Observable<List<Change>?>.joinChanges(): Observable<List<ChangeItem>?> {
        return this.mapSubQueries {
            change ->
            combineLatestValue3Nullable(DatabaseRead.groupName(change.groupId), DatabaseCombine.groupColor(change.groupId), DatabaseRead.user(change.by)) {
                groupName, groupColor, user ->
                ChangeItem(change, groupName, groupColor, user)
            }
        }.joinSubQueries()
    }
}

fun List<GroupItem>.findById(groupId: String): GroupItem? {
    return this.find { it.group.getId() == groupId }
}

fun List<GroupItem>.getIndexById(groupId: String): Int {
    return this.indexOfFirst { it.group.getId() == groupId }
}

data class ChangeItem(val change: Change, val groupName: String, val groupColor: Int, val user: User?)

data class MemberAmountItem(val memberAmount: MemberAmount, val member: Member, val userMember: String?, val currency: String, val groupId: String, val groupColor: Int)

data class GroupItem(val group: Group, val userGroup: UserGroup)

data class TransactionItem(val transaction: Transaction, val members: List<Member>, val userMember: String?, val groupId: String, val groupColor: Int)

data class DebtItem(val debt: Debt, val fromMember: Member, val toMember: Member, val userMember: String?, val currency: String, val color: Int, val groupId: String, val readOnly: Boolean)

data class CurrencyItem(val code: String, val name: String?)

data class MemberAmountsResult(val memberAmount: List<MemberAmount>, val members: List<Member>, val currency: String, val userMember: String?, val groupColor: Int)