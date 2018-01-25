package cz.pepa.runapp.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.fasterxml.jackson.databind.ObjectMapper
import com.google.firebase.database.Exclude

/**
 * Abstract class for every database model. Handles database keys.
 *
 * @author Filip Prochazka (filip@stepuplabs.io), David Vávra (david@stepuplabs.io)
 */
abstract class DatabaseModel {

    @Exclude
    @JsonIgnore
    private var id: String? = null // It needs to be nullable for writing new data to database

    @Exclude
    @JsonIgnore
    fun getId(): String {
        return checkNotNull(id, { "Missing id for ${this.javaClass.name}" })
    }

    fun setId(key: String) {
        id = key
    }

    fun hasId(): Boolean {
        return id != null
    }

    override fun equals(other: Any?): Boolean {
        if (other is DatabaseModel) {
            return id == other.id
        }
        return super.equals(other)
    }

    override fun hashCode(): Int {
        return getId().hashCode()
    }

    @Suppress("UNCHECKED_CAST")
    fun toMap(): Map<String, Any> {
        return ObjectMapper().convertValue(this, Map::class.java) as Map<String, Any>
    }
}