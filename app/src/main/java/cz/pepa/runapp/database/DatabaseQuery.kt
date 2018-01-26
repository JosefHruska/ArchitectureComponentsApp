package cz.pepa.runapp.database

import com.google.firebase.database.Query

/**
 * Query for a list path in Firebase Database.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */
class DatabaseQuery {

    lateinit var path: String
    var orderByChild: String? = null
    var limitToLast: Int? = null
    var equalTo: String? = null
    var startAt: Double? = null
    var endAt: Double? = null

    fun build(): Query {
        var query: Query = Database.get().reference.child(path)
        if (orderByChild != null) {
            query = query.orderByChild(orderByChild)
        }
        limitToLast?.let { query = query.limitToLast(it) }
        if (equalTo != null) {
            query = query.equalTo(equalTo)
        }
        startAt?.let { query = query.startAt(it) }
        endAt?.let { query = query.endAt(it) }
        return query
    }

    override fun toString(): String {
        return "DatabaseQuery(path='$path')"
    }
}