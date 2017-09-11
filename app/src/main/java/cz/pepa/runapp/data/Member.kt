package cz.pepa.runapp.data

import com.fasterxml.jackson.annotation.JsonIgnore
import com.google.firebase.database.Exclude

/**
 * TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */
class Member() : DatabaseModel() {

    var active: Boolean = true
    var bankAccount: String? = null
    var defaultWeight = "1"
    lateinit var name: String
    var photoUrl: String? = null

    override fun toString(): String {
        return "Member(active=$active, bankAccount=$bankAccount, defaultWeight='$defaultWeight', name='$name', photoUrl=$photoUrl)"
    }

    // Auto-generated equals() & hashCode() are needed for comparing if circles have changed

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other?.javaClass != javaClass) return false
        if (!super.equals(other)) return false

        other as Member

        if (active != other.active) return false
        if (bankAccount != other.bankAccount) return false
        if (defaultWeight != other.defaultWeight) return false
        if (name != other.name) return false
        if (photoUrl != other.photoUrl) return false

        return true
    }

    override fun hashCode(): Int {
        var result = super.hashCode()
        result = 31 * result + active.hashCode()
        result = 31 * result + (bankAccount?.hashCode() ?: 0)
        result = 31 * result + defaultWeight.hashCode()
        result = 31 * result + name.hashCode()
        result = 31 * result + (photoUrl?.hashCode() ?: 0)
        return result
    }

    @Exclude
    @JsonIgnore
    fun isMissing(): Boolean {
        return hasId() && getId() == "missing"
    }
}