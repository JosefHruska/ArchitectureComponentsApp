package cz.pepa.runapp.database
import com.gojuno.koptional.None
import com.gojuno.koptional.Optional
import com.gojuno.koptional.toOptional
import com.google.firebase.database.DataSnapshot
import cz.pepa.runapp.data.DatabaseModel
import io.reactivex.Flowable

/**
 * Converts Observables from Database and parses data.
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

fun <T : DatabaseModel> Flowable<Optional<DataSnapshot>>.toObjectObservable(type: Class<T>): Flowable<Optional<T>> {
    return this.map {
        if (it is None) {
            val nothing: Optional<T> = None
            return@map nothing
        }
        val snapshot = it.toNullable()
        val data = snapshot?.getValue(type)
        data?.setId(snapshot.key)
        data.toOptional()
    }
}

fun <T : DatabaseModel> Flowable<Optional<DataSnapshot>>.toListObservable(type: Class<T>): Flowable<Optional<List<T>>> {
    return this.map {
        val noneList: Optional<List<T>> = None
        val list = it.toNullable() ?: return@map noneList
        list.children.map {
            val data = checkNotNull(it.getValue(type), {"Non-existing db path"})
            data.setId(it.key)
            data
        }.toOptional()
    }
}


fun <T : Any> Flowable<Optional<DataSnapshot>>.toPrimitiveObservable(type: Class<T>): Flowable<Optional<T>> {
    return this.map {
        if (it is None) {
            val nothing: Optional<T> = None
            return@map nothing
        }
        val primitive = it.toNullable()?.getValue(type)
        primitive.toOptional()
    }
}