package cz.pepa.runapp.api

import okhttp3.Credentials
import okhttp3.Interceptor
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import okhttp3.OkHttpClient
import cz.pepa.runapp.api.model.StackOverflowAPI
import retrofit2.converter.gson.GsonConverterFactory
import com.google.gson.GsonBuilder
import com.google.gson.Gson
import cz.pepa.runapp.api.model.GoogleFitAPI


/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

object Retrofit {

    fun getOkClient(): OkHttpClient {
        return OkHttpClient().newBuilder().addInterceptor(object : Interceptor {
           override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
               val newRequest = chain.request().newBuilder()
                       .addHeader("Authorization", "Bearer " + "ya29.GlvpBEN_px3eRdQ5ngEeR3kqN-ybwMNiPiL4bbGsHSa31kRf-HVqbv0kEHnZ56Khxe3ufTLTEN7L1IYHdPXzz59Vcszl91PIxaRV1rI_2X02MCF8-TmuFecYWYuO")
                       .build()
               return chain.proceed(newRequest)
            }
        }).build()

    }

    fun build(): Retrofit? {
        return Retrofit.Builder()
                .baseUrl("www.googleapis.com")
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build()
    }

    fun createStackoverflowAPI(): StackOverflowAPI {
        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create()

        val retrofit = Retrofit.Builder()

                .baseUrl(StackOverflowAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return retrofit.create(StackOverflowAPI::class.java)
    }

    fun createGoogleFitAPI(): GoogleFitAPI {
        val gson = GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ssZ")
                .create()

        val retrofit = Retrofit.Builder()
                .client(getOkClient())
                .baseUrl(GoogleFitAPI.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build()

        return retrofit.create(GoogleFitAPI::class.java)
    }
}