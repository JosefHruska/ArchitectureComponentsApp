package cz.pepa.runapp.api

import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

/**
 * // TODO: Add description
 *
 * @author Josef Hruška (josef@stepuplabs.io)
 */

interface APIClient {

    @FormUrlEncoded
    @POST("/oauth/token")
    fun getNewAccessToken(
            @Field("code") code: String,
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("grant_type") grantType: String): Call<AccessToken>

    @FormUrlEncoded
    @POST("/oauth/token")
    fun getRefreshAccessToken(
            @Field("refresh_token") refreshToken: String,
            @Field("client_id") clientId: String,
            @Field("client_secret") clientSecret: String,
            @Field("redirect_uri") redirectUri: String,
            @Field("grant_type") grantType: String): Call<AccessToken>

}