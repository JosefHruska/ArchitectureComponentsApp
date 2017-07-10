package cz.pepa.runapp.database
import com.google.firebase.database.DataSnapshot
import cz.pepa.runapp.data.DatabaseModel
import rx.Observable

//
///**
// * Converts Observables from Database and parses data.
// *
// * @author David Vávra (david@stepuplabs.io)
// */
//
fun <T : DatabaseModel> Observable<DataSnapshot?>.toObjectObservable(type: Class<T>): Observable<T?> {
    return this.map {
        if (it == null) {
            return@map null
        }
        val data = it.getValue(type)
        data?.setId(it.key)
        data
    }
}
//
//fun <T : DatabaseModel> Observable<DataSnapshot?>.toListObservable(type: Class<T>): Observable<List<T>?> {
//    return this.map {
//        if (it == null) {
//            return@map null
//        }
//        it.children.map {
//            val data = checkNotNull(it.getValue(type), {"Non-existing db path"})
//            data.setId(it.key)
//            data
//        }
//    }
//}
//
//fun Observable<DataSnapshot?>.toCountObservable(): Observable<Int?> {
//    return this.map {
//        if (it == null) {
//            return@map 0
//        }
//        it.childrenCount.toInt()
//    }
//}
//

fun <T> Observable<DataSnapshot?>.toPrimitiveObservable(type: Class<T>): rx.Observable<T?> {
    return this.map {
        if (it == null) {
            return@map null
        }
        it.getValue(type)
    }
}
//
//@Suppress("UNCHECKED_CAST")
//fun <T> Observable<DataSnapshot?>.toMapObservable(): Observable<Map<String, T>?> {
//    return this.map {
//        var result = mapOf<String, T>()
//        if (it != null && it.value != null) {
//            result = it.value as Map<String, T>
//        }
//        result
//    }
//}
//
//fun Observable<DataSnapshot?>.toChangesObservable(groupId: String): Observable<List<Change>> {
//    return this.map {
//        if (it == null) {
//            return@map null
//        }
//        it.children.map {
//            val data = checkNotNull(it.getValue(Change::class.java), {"Non existing db path"})
//            data.setId(it.key)
//            data.groupId = groupId
//            data
//        }
//    }
//}