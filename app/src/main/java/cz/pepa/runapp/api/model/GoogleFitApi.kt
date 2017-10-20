package cz.pepa.runapp.api.model

import retrofit2.Call
import retrofit2.http.GET

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

interface GoogleFitAPI {

    @GET("/fitness/v1/users/me/dataSources")
    fun getMyDataSources(): Call<ListWrapper<DataSource>>

    companion object {
        val BASE_URL = "https://www.googleapis.com"
    }
}