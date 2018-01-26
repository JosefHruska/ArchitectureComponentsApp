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
 * @author Josef Hruška (josef@stepuplabs.io)
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