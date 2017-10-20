package cz.pepa.runapp.api.model

import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path


/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

interface StackOverflowAPI {

    @GET("/2.2/questions?order=desc&sort=votes&site=stackoverflow&tagged=android&filter=withbody")
    fun questions(): Call<ListWrapper<Question>>

    @GET("/2.2/questions/{id}/answers?order=desc&sort=votes&site=stackoverflow")
    fun getAnswersForQuestion(@Path("id") questionId: String): Call<ListWrapper<Answer>>

    companion object {
        val BASE_URL = "https://api.stackexchange.com"
    }

}

class ListWrapper<T> {
    internal var items: List<T>? = null
}