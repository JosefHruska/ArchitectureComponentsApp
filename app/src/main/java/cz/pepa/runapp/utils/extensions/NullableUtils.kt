package io.stepuplabs.settleup.util.extensions

/**
 * Various utils related to nullable stuff.
 *
 * @author Josef Hruška (josef@stepuplabs.io), David Vávra (david@stepuplabs.io)
 */

fun <T1, T2> ifNotNull(value1: T1?, value2: T2?, bothNotNull: (T1, T2) -> (Unit)) {
    if (value1 != null && value2 != null) {
        bothNotNull(value1, value2)
    }
}