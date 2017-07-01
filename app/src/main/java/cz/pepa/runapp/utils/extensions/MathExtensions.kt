package io.stepuplabs.settleup.util.extensions

import java.math.BigDecimal
import java.math.RoundingMode
import java.util.*

/**
 * Math-related utils.

 * @author David Vávra (david@stepuplabs.io)
 */

fun BigDecimal.isNegative(): Boolean {
    return this < BigDecimal.ZERO
}

fun BigDecimal.divideWithScale(number: BigDecimal): BigDecimal {
    return divide(number, 20, RoundingMode.HALF_UP)
}

fun MutableMap<String, BigDecimal>.add(entry: Pair<String, BigDecimal>) {
    val amount = this[entry.first]
    if (amount == null) {
        this[entry.first] = entry.second
    } else {
        this[entry.first] = amount + entry.second
    }
}

fun MutableMap<String, BigDecimal>.subtract(entry: Pair<String, BigDecimal>) {
    val amount = this[entry.first]
    if (amount == null) {
        this[entry.first] = entry.second.negate()
    } else {
        this[entry.first] = amount - entry.second
    }
}

fun String.canBeConvertedToDouble(): Boolean {
    try {
        this.toDouble()
        return true
    } catch (e: NumberFormatException) {
        return false
    }
}

fun String.removeTrailingZeros(): String {
    return if (!this.contains(".")) this else this.replace("0*$".toRegex(), "").replace("\\.$".toRegex(), "")
}

fun Int.bd(): BigDecimal {
    return BigDecimal(this)
}

fun Long.bd(): BigDecimal {
    return BigDecimal(this)
}

fun Double.bd(): BigDecimal {
    return BigDecimal(this)
}

fun String.bd(): BigDecimal {
    return BigDecimal(this)
}

fun List<BigDecimal>.sum(): BigDecimal {
    return this.fold(BigDecimal.ZERO, BigDecimal::add)
}

fun List<BigDecimal>.gcd(): BigDecimal {
    if (this.isEmpty()) {
        return BigDecimal.ZERO
    }
    var gcd = this.first()
    for (i in 1..this.size - 1) {
        gcd = gcd(gcd, this[i])
    }
    return gcd
}

fun BigDecimal.isZero(): Boolean {
    return this.toDouble() == 0.0
}

fun <E> List<E>.randomElement(): E {
    return this[Random().nextInt(this.size)]
}

/**
 * Calculates Greatest Common Divisor using Euclidean Algorithm.
 */
private fun gcd(a: BigDecimal, b: BigDecimal): BigDecimal {
    if (b.toDouble() == 0.0) return a
    return gcd(b, a % b)
}

