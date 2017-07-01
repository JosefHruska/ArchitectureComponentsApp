package io.stepuplabs.settleup.util.extensions

import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.NumberFormat
import java.util.*

/**
 * Extensions for formatting currency.
 *
 * @author David Vávra (david@stepuplabs.io)
 */

fun BigDecimal.formatCurrency(currencyCode: String?): String {
    // By default show just 2 decimal numbers. If the number is between -1 and 1 it can display 10 decimal number, so it doesn't look like nonsense 0 amount.
    val formatter = if (this.abs() < BigDecimal.ONE) {
        DecimalFormat("#.##########")
    } else {
        DecimalFormat("#.##")
    }
    return this.formatCurrency(currencyCode, formatter)
}

fun BigDecimal.formatCurrencyForCircles(currencyCode: String?): String {
    val value = this.abs().toDouble()
    if (value < 100) {
        return this.formatCurrency(currencyCode, DecimalFormat("#.##"))
    } else if (value < 10_000) {
        return this.formatCurrency(currencyCode, DecimalFormat("#")) // 4204.358 -> 4204
    } else if (value < 1_000_000) {
        return this.divideAmountWithFormattedScale(1000).formatWithQuantitySymbol(currencyCode, "k") // 3442 -> 3.4k; 34 421 -> 34k; 340 421 -> 340k
    } else if (value < 1_000_000_000) {
        return this.divideAmountWithFormattedScale(1_000_000).formatWithQuantitySymbol(currencyCode, "M") // 1 210 000 -> 1.2M; 12 100 000 -> 12M; 120 452 000 -> 120M
    } else {
        return this.divideAmountWithFormattedScale(1_000_000_000).formatWithQuantitySymbol(currencyCode, "B") // 1 210 000 000 -> 1.2B; 12 100 000 000 -> 12B; 120 452 000 000 -> 120B
    }
}

fun BigDecimal.formatCurrency(currencyCode: String?, numberFormat: DecimalFormat, amountSymbol: String? = null): String {
    val formattedCurrencyAmount = when (currencyCode) {
        "CZK" -> {
            // For CZK we want to use formatting according to czech Locale.
            val format =  NumberFormat.getCurrencyInstance(Locale("cs", "CZ"))
            format.maximumFractionDigits = numberFormat.maximumFractionDigits
            val formattedCurrency = this.formatNumber(format)
            formattedCurrency
        }
        else -> {
            try {
                val format = NumberFormat.getCurrencyInstance()
                format.currency = Currency.getInstance(currencyCode)
                format.maximumFractionDigits = numberFormat.maximumFractionDigits
                this.formatNumber(format)
            } catch (unsupportedCode: IllegalArgumentException) {
                this.formatNumber(numberFormat) + " " + currencyCode
            }
        }
    }
    amountSymbol?.let { return this.addAmountSymbolToCorrectPosition(formattedCurrencyAmount, amountSymbol) }

    return formattedCurrencyAmount
}

/**
 * When displayed, it's always > 1
 * Example 1: CZK->USD: 21.7 is displayed as 21.7 CZK = 1 USD
 * Example 2: EUR->USD: 0.91 is displayed as 1.1 USD = 1 EUR
 */
fun BigDecimal.formatExchangeRate(): String {
    if (this > BigDecimal.ONE) {
        return this.formatNumber(DecimalFormat("#.####"))
    } else {
        return BigDecimal.ONE.divideWithScale(this).formatNumber(DecimalFormat("#.####"))
    }
}

fun Locale.currency(): String {
    return Currency.getInstance(this).currencyCode
}

fun Locale.currencySymbol(): String {
    return Currency.getInstance(this).symbol
}

private fun BigDecimal.formatWithQuantitySymbol(currencyCode: String?, symbol: String): String {
    if (this.toBigInteger().toString().removePrefix("-").length == 1) { // This takes only integer part of number ( "-" is removed) and then it checks it length.
        return this.formatCurrency(currencyCode, DecimalFormat("#.#"), symbol)
    } else {
        return this.formatCurrency(currencyCode, DecimalFormat("#"), symbol)
    }
}

private fun BigDecimal.addAmountSymbolToCorrectPosition(amount: String, amountSymbol: String): String {
    var currencyAmount = amount
    if (!currencyAmount.removePrefix("-").first().isDigit()) { // Currency code is added as prefix. Amount symbol is simply appended to the amount
        currencyAmount += amountSymbol
    } else {  // Currency code is added as suffix. Amount symbol has to be put between amount and currency code.
        var lastDigitIndex: Int? = null
        if (this.isNegative()) {
            currencyAmount = currencyAmount.removePrefix("-")
        }
        currencyAmount.forEachIndexed { index, char ->
            if (lastDigitIndex == null && char.isLetter()) {
                lastDigitIndex = index - 1
            }
        }
        lastDigitIndex?.let {
            currencyAmount = currencyAmount.substring(0, it) + amountSymbol + " " + currencyAmount.substring(it.plus(1))
        }
        if (this.isNegative()) {
            currencyAmount = "-" + currencyAmount
        }
    }
    return currencyAmount
}