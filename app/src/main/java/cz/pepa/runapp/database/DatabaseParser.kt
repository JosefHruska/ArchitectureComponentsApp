package cz.pepa.runapp.database
import com.google.firebase.database.DataSnapshot
import cz.pepa.runapp.data.DatabaseModel
import io.reactivex.Flowable


//
///**
// * Converts Observables from Database and parses data.
// *
// * @author David Vávra (david@stepuplabs.io)
// */
//
fun <T : DatabaseModel> Flowable<DataSnapshot>.toObjectObservable(type: Class<T>): Flowable<T> {
    return this.flatMap {
        val data = it.getValue(type)
        data?.setId(it.key)
        val snapshotObject = if (data == null) {
            Flowable.empty<T>()
//            data!!
        } else {
            Flowable.just(data)
        }
        snapshotObject
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

fun <T> Flowable<DataSnapshot>.toPrimitiveObservable(type: Class<T>): Flowable<T> {
    return this.flatMap {
        it.getValue(type)
        val snapshotObject = if (it.getValue(type) == null) {
            Flowable.empty<T>()
        } else {
            Flowable.just(it.getValue(type))
        }
        snapshotObject
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