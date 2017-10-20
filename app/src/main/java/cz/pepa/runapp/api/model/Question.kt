package cz.pepa.runapp.api.model

import com.google.gson.annotations.SerializedName

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

class Question {

    var title: String? = null
    var body: String? = null

    @SerializedName("question_id")
    var questionId: String? = null

    override fun toString(): String {
        return title!!
    }
}