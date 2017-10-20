package cz.pepa.runapp.api


import android.content.Context
import cz.pepa.runapp.BuildConfig
import cz.pepa.runapp.app
import okhttp3.Authenticator
import okhttp3.OkHttpClient
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.IOException

object ServiceGenerator {

    val API_BASE_URL = "www.googleapis.com"
    val API_OAUTH_REDIRECT = "https://runapp-3a64c.firebaseapp.com/__/auth/handler"

    private var httpClient: OkHttpClient.Builder? = null

    private var builder: Retrofit.Builder? = null

    private var mToken: AccessToken? = null

    fun <S> createService(serviceClass: Class<S>): S {
        httpClient = OkHttpClient.Builder()
        builder = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

        val client = httpClient!!.build()
        val retrofit = builder!!.client(client).build()
        return retrofit.create(serviceClass)
    }

    fun <S> createService(serviceClass: Class<S>, accessToken: AccessToken?, c: Context): S {
        httpClient = OkHttpClient.Builder()
        builder = Retrofit.Builder()
                .baseUrl(API_BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())

        if (accessToken != null) {
            mToken = accessToken
            httpClient!!.addInterceptor { chain ->
                val original = chain.request()

                val requestBuilder = original.newBuilder()
                        .header("Accept", "application/json")
                        .header("Content-type", "application/json")
                        .header("Authorization",
                                accessToken.tokenType + " " + accessToken.accessToken)
                        .method(original.method(), original.body())

                val request = requestBuilder.build()
                chain.proceed(request)
            }

            httpClient!!.authenticator(Authenticator { route, response ->
                if (responseCount(response) >= 2) {
                    // If both the original call and the call with refreshed token failed,
                    // it will probably keep failing, so don't try again.
                    return@Authenticator null
                }

                // We need a new client, since we don't want to make another call using our client with access token
                val tokenClient = createService(APIClient::class.java)
                val call = tokenClient.getRefreshAccessToken(mToken!!.refreshToken,
                        mToken!!.clientID, mToken!!.clientSecret, API_OAUTH_REDIRECT,
                        "refresh_token")
                try {
                    val tokenResponse = call.execute()
                    if (tokenResponse.code() == 200) {
                        val newToken = tokenResponse.body()
                        mToken = newToken
                        val prefs = app().getSharedPreferences(BuildConfig.APPLICATION_ID, Context.MODE_PRIVATE)
                        prefs.edit().putBoolean("oauth.loggedin", true).apply()
                        prefs.edit().putString("oauth.accesstoken", newToken!!.accessToken).apply()
                        prefs.edit().putString("oauth.refreshtoken", newToken.accessToken).apply()
                        prefs.edit().putString("oauth.tokentype", newToken.accessToken).apply()

                        return@Authenticator response.request().newBuilder()
                                .header("Authorization", newToken.tokenType + " " + newToken.accessToken)
                                .build()
                    } else {
                        return@Authenticator null
                    }
                } catch (e: IOException) {
                    return@Authenticator null
                }
            })
        }

        val client = httpClient!!.build()
        val retrofit = builder!!.client(client).build()
        return retrofit.create(serviceClass)
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        while ((response.priorResponse()) != null) {
            result++
        }
        return result
    }
}