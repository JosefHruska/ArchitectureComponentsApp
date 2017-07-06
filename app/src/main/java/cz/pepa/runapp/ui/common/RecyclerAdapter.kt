package cz.pepa.runapp.ui.common

import android.support.annotation.LayoutRes
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup

/**
 * [RecyclerView] adapter for list of items.
 *
 * @author Filip Prochazka (filip@stepuplabs.io), David Vávra (david@stepuplabs.io)
 */

open class RecyclerAdapter<T>(@LayoutRes val itemLayoutRes: Int) : RecyclerView.Adapter<RecyclerAdapter.DataViewHolder>() {

    var mData: List<T> = listOf()
    private var mBinder: DataBinder<T>? = null
    override fun getItemCount(): Int {
        return mData.size
    }

    override fun onBindViewHolder(holder: DataViewHolder, position: Int) {
        mBinder?.bindHolder(mData[position], holder)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): DataViewHolder? {
        return DataViewHolder(LayoutInflater.from(parent.context).inflate(itemLayoutRes, parent, false))
    }

    fun setData(data: List<T>, binder: DataBinder<T>) {
        mBinder = binder
        mData = data
        // It would be cool to use DiffUtil here, but it requires a larger refactoring (all equals methods, ids handling) and it's not worth it
        notifyDataSetChanged()
    }

    class DataViewHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)
}

abstract class DataBinder<in T> {

    abstract fun bind(data: T, view: View)

    open fun bindHolder(data: T, dataHolder: RecyclerAdapter.DataViewHolder) {
        bind(data, dataHolder.itemView)
    }
}