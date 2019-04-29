package vn.semicolon.zalos

import io.reactivex.Flowable
import retrofit2.http.GET
import retrofit2.http.Query


interface IZaloOauthAPI {
    @GET("access_token")
    fun getAccessToken(
            @Query("code") oauthCode: String,
            @Query("app_id") appId: String,
            @Query("app_secret") appSecret: String
    ): Flowable<ZaloResponse<ZaloUserModel.Token>>
}
