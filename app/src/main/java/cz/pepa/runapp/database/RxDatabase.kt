package cz.pepa.runapp.database

import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.Some
import com.gojuno.koptional.toOptional
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import io.reactivex.BackpressureStrategy
import io.reactivex.Flowable
import io.reactivex.Single
import io.reactivex.SingleEmitter
import io.reactivex.functions.BiFunction
import io.reactivex.schedulers.Schedulers
import io.stepuplabs.settleup.util.extensions.toSome

/**
 * Utilities related to RX and Database.
 *
 * @author David Vávra (david@stepuplabs.io)
 */

fun DatabaseQuery.observe(): Flowable<Optional<DataSnapshot>> {
    return Flowable.create<Optional<DataSnapshot>>({
        val query = this.build()
        val listener = query.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                val snap: Optional<com.google.firebase.database.DataSnapshot> = None
                it.onNext(snap) // Data no longer exist or permission denied
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                it.onNext(dataSnapshot.toOptional())
            }
        })
        it.setCancellable {
            query.removeEventListener(listener)
        }
    }, BackpressureStrategy.BUFFER)
            .observeOn(Schedulers.computation())
}

fun DatabaseQuery.exists(): Single<Optional<Boolean>> {
    return Single.create({ emitter: SingleEmitter<Optional<Boolean>> ->
        val query = this.build()
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(databaseError: DatabaseError) {
                emitter.onSuccess(false.toOptional())
            }

            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val exists = dataSnapshot.value != null
                emitter.onSuccess(exists.toOptional())
            }
        })
    })
            .observeOn(Schedulers.computation())
}

//fun DatabaseQuery.observeCountOnce(): Observable<Long> {
//    return Observable.create<Long>({
//        val query = this.build()
//        query.addListenerForSingleValueEvent(object : ValueEventListener {
//            override fun onCancelled(databaseError: DatabaseError) {
//                it.onNext(0)
//                it.onCompleted()
//            }
//
//            override fun onDataChange(dataSnapshot: DataSnapshot) {
//                it.onNext(dataSnapshot.childrenCount)
//                it.onCompleted()
//            }
//        })
//    }, Emitter.BackpressureMode.BUFFER)
//            .observeOn(Schedulers.computation())
//}
//
///**
// * Source: http://stackoverflow.com/questions/32292509/rxjava-consolidating-multiple-infinite-observablelistt
// */
//fun <A, B> Observable<List<A>?>.mapSubQueries(subQuery: (A) -> Observable<B>): Observable<List<Observable<B>>> {
//    return this.flatMap {
//        if (it != null && it.isNotEmpty()) {
//            return@flatMap Observable.from(it).map { subQuery(it) }.toList()
//        } else {
//            return@flatMap Observable.just(listOf<Observable<B>>())
//        }
//    }
//}
//
//@Suppress("UNCHECKED_CAST")
//fun <T> Observable<List<Observable<T?>>>.joinSubQueries(): Observable<List<T>?> {
//    return this.flatMap {
//        if (it.isNotEmpty()) {
//            return@flatMap Observable.combineLatest(it, {
//                val list = mutableListOf<T>()
//                it.forEach {
//                    if (it != null) {
//                        list.add(it as T)
//                    }
//                }
//                list
//            })
//        } else {
//            return@flatMap Observable.just(listOf<T>())
//        }
//    }
//}

fun <T1 : Any, T2 : Any, R : Any> combineLatest(observable1: Flowable<Optional<T1>>, observable2: Flowable<Optional<T2>>, merge: (T1, T2) -> R, nullValue: R? = null): Flowable<Optional<R>> {
    return Flowable.combineLatest(observable1, observable2, BiFunction {
        value1, value2 ->
        if (value1 is Some<T1> && value2 is Some<T2>) {
            merge(value1.toSome(), value2.toSome()).toOptional()
        } else {
            val empty: Optional<R> = None
            empty
        }
    })

}



//fun <T1, T2, T3, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, merge: (T1, T2, T3) -> R): Observable<R?> {
//    return Observable.combineLatest(observable1, observable2, observable3) {
//        value1, value2, value3 ->
//        var result: R? = null
//        if (value1 != null && value2 != null && value3 != null) {
//            result = merge(value1, value2, value3)
//        }
//        result
//    }
//}
//
//fun <T1, T2, T3, R> combineLatestValue3Nullable(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, merge: (T1, T2, T3?) -> R): Observable<R?> {
//    return Observable.combineLatest(observable1, observable2, observable3) {
//        value1, value2, value3 ->
//        var result: R? = null
//        if (value1 != null && value2 != null) {
//            result = merge(value1, value2, value3)
//        }
//        result
//    }
//}
//
//fun <T1, T2, T3, T4, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, observable4: Observable<T4?>, merge: (T1, T2, T3, T4) -> R): Observable<R?> {
//    return Observable.combineLatest(observable1, observable2, observable3, observable4) {
//        value1, value2, value3, value4 ->
//        var result: R? = null
//        if (value1 != null && value2 != null && value3 != null && value4 != null) {
//            result = merge(value1, value2, value3, value4)
//        }
//        result
//    }
//}
//
//fun <T1, T2, T3, T4, T5, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, observable4: Observable<T4?>, observable5: Observable<T5?>, merge: (T1, T2, T3, T4, T5) -> R): Observable<R?> {
//    return Observable.combineLatest(observable1, observable2, observable3, observable4, observable5) {
//        value1, value2, value3, value4, value5 ->
//        var result: R? = null
//        if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null) {
//            result = merge(value1, value2, value3, value4, value5)
//        }
//        result
//    }
//}
//
//fun <T1, T2, T3, T4, T5, T6, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, observable4: Observable<T4?>, observable5: Observable<T5?>, observable6: Observable<T6?>, merge: (T1, T2, T3, T4, T5, T6) -> R): Observable<R?> {
//    return Observable.combineLatest(observable1, observable2, observable3, observable4, observable5, observable6) {
//        value1, value2, value3, value4, value5, value6 ->
//        var result: R? = null
//        if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null && value6 != null) {
//            result = merge(value1, value2, value3, value4, value5, value6)
//        }
//        result
//    }
//}
//
//fun <T1, T2, T3, T4, T5, T6, T7, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, observable4: Observable<T4?>, observable5: Observable<T5?>, observable6: Observable<T6?>, observable7: Observable<T7?>, merge: (T1, T2, T3, T4, T5, T6, T7) -> R): Observable<R?> {
//    return Observable.combineLatest(observable1, observable2, observable3, observable4, observable5, observable6, observable7) {
//        value1, value2, value3, value4, value5, value6, value7 ->
//        var result: R? = null
//        if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null && value6 != null && value7 != null) {
//            result = merge(value1, value2, value3, value4, value5, value6, value7)
//        }
//        result
//    }
//}
//
//fun <T1, T2, T3, T4, T5, T6, T7, T8, R> combineLatest(observable1: Observable<T1?>, observable2: Observable<T2?>, observable3: Observable<T3?>, observable4: Observable<T4?>, observable5: Observable<T5?>, observable6: Observable<T6?>, observable7: Observable<T7?>, observable8: Observable<T8?>, merge: (T1, T2, T3, T4, T5, T6, T7, T8) -> R): Observable<R?> {
//    return Observable.combineLatest(observable1, observable2, observable3, observable4, observable5, observable6, observable7, observable8) {
//        value1, value2, value3, value4, value5, value6, value7, value8 ->
//        var result: R? = null
//        if (value1 != null && value2 != null && value3 != null && value4 != null && value5 != null && value6 != null && value7 != null && value8 != null) {
//            result = merge(value1, value2, value3, value4, value5, value6, value7, value8)
//        }
//        result
//    }
//}