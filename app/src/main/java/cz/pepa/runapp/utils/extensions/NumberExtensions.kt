package io.stepuplabs.settleup.util.extensions

import bd
import removeTrailingZeros
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat

/**
 * Extensions for formatting numbers.
 *
 * @author David Vávra (david@stepuplabs.io)
 */

fun BigDecimal.formatNumber(formatter: NumberFormat): String {
    val formattedAmount = this.makeNegativeZeroPositive(formatter)
    return formatter.format(formattedAmount)
}

fun Double.formatDouble(): String {
    return DecimalFormat("#.#").format(this)
}

fun Int.formatInt(): String {
    return NumberFormat.getInstance().format(this)
}

fun BigDecimal.formatWeight(): String {
    return this.formatNumber(DecimalFormat("#.##"))
}

fun BigDecimal.formatAmount(): String {
    if (this.abs() < 10000.bd()) {
        return this.formatNumber(DecimalFormat("#.##"))
    } else {
        return this.formatNumber(DecimalFormat("#"))
    }
}

fun BigDecimal.divideAmountWithFormattedScale(divider: Long): BigDecimal {
    return this.divide(divider.bd(), getDivisionScale(), java.math.RoundingMode.HALF_UP)
}

fun String.isNegativeZero(): Boolean {
    // It is negative zero if it starts with "-0" and its decimal part contains only zeros.
    if ((this.startsWith("-0")) && this.all { !it.isDigit() || (it.isDigit() && it == '0') }) {
        return true
    }
    return false
}

fun BigDecimal.makeNegativeZeroPositive(formatter: NumberFormat): BigDecimal {
    /*
    If the last decimal number used by formatter is zero, the next decimal number on right side is checked.
    If it is >= 5, the last decimal used by formatter is changed to 1 - otherwise BigDecimal.ZERO is returned.
    Examples: DecimalFormat("#.##")
    0.009 -> 0.01
    0.002 -> 0
    */
    val scale = BigDecimal.ONE.scaleByPowerOfTen(formatter.maximumFractionDigits * -1)
    if (this.abs() < scale.multiply(0.5.bd())) {
        return BigDecimal.ZERO
    }
    return this
}

fun BigDecimal.toStringWeight(): String {
    return this.toPlainString().removeTrailingZeros()
}

private fun BigDecimal.getDivisionScale(): Int {
    val length = this.abs().toBigInteger().toString().length
    // 3442 -> 3.4; 34 421 -> 34K; 340 421 -> 340
    // 1 210 000 -> 1.2; 12 100 000 -> 12; 120 452 000 -> 120
    // 1 210 000 000 -> 1.2; 12 100 000 000 -> 12; 120 452 000 000 -> 120
    if (length == 4 || length == 7 || length == 10) {
        return 1
    } else {
        return 0
    }
}