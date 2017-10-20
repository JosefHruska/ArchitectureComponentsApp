package cz.pepa.runapp.api.model

import com.google.gson.annotations.SerializedName

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class Answer {

    @SerializedName("answer_id")
    var answerId: Int = 0

    @SerializedName("is_accepted")
    var accepted: Boolean = false

    var score: Int = 0

    override fun toString(): String {
        return answerId.toString() + " - Score: " + score + " - Accepted: " + if (accepted) "Yes" else "No"
    }
}