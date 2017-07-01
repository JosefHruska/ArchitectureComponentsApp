package io.stepuplabs.settleup.util.extensions

import io.stepuplabs.settleup.R
import io.stepuplabs.settleup.calculation.debts.MinimizedDebtCalculator
import io.stepuplabs.settleup.model.Member
import io.stepuplabs.settleup.model.MemberWeight
import io.stepuplabs.settleup.model.base.DatabaseModel
import io.stepuplabs.settleup.model.derived.MemberAmount
import io.stepuplabs.settleup.model.derived.Split
import io.stepuplabs.settleup.model.derived.WhoPaidAmount

/**
 * Model extensions.
 *
 * @author David Vávra (david@stepuplabs.io)
 */

fun List<Member>.findById(id: String): Member {
    return find { it.getId() == id } ?: Member().apply { name = R.string.missing_member.toText(); setId("missing") } // When you delete member offline and some other people add transactions with him, you can have missing member
}

fun <T : DatabaseModel> List<T>.findById(id: String): T {
    return find { it.getId() == id } ?: throw IllegalArgumentException("Trying to get entity with id '$id' which does not exists")
}

fun List<MemberWeight>.findById(memberId: String): MemberWeight {
    return find { it.memberId == memberId } ?: throw IllegalArgumentException("Trying to get member weight with id '$memberId' which does not exists")
}

fun List<MemberAmount>.containsMember(memberId: String): Boolean {
    return this.find { it.memberId == memberId } != null
}

fun List<MemberAmount>.areZero(): Boolean {
    return this.all {
        /*
        Debts <= 1E-10 are not displayed. It means that if balance is smaller than
        debtsTolerance(1E-10), NoDebts circle may be displayed.
        Example:
            0.000000001 (9 decimals)    -> Debt is visible.
            0.0000000001 (10 decimals)  -> Debt is invisible.
        */
        val balance = if (it.amount.abs() <= MinimizedDebtCalculator.TOLERANCE) {
            return true
        } else {
            it.amount.toPlainString()
        }
        it.amount.isZero() || balance.isNegativeZero()
    }
}

fun List<MemberAmount>.findById(memberId: String): MemberAmount? {
    return this.find { it.memberId == memberId }
}

fun List<WhoPaidAmount>.findById(memberId: String): WhoPaidAmount? {
    return this.find { it.memberId == memberId }
}

fun List<Split>.findById(memberId: String): Split {
    return this.find { it.memberId == memberId }?: throw IllegalArgumentException("Trying to get split with id '$memberId' which does not exists")
}