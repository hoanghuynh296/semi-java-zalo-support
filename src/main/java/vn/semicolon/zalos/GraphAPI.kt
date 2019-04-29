package vn.semicolon.zalos

import io.reactivex.Flowable
import io.reactivex.schedulers.Schedulers
import retrofit2.http.GET
import retrofit2.http.Query
import java.lang.Exception


interface IZaloGraphAPI {
    @GET("me")
    fun getProfile(
            @Query("access_token") accessToken: String,
            @Query("fields") fields: String = "id,birthday,gender,picture,name"
    ): Flowable<ZaloResponse<ZaloUserModel>>

    @GET("me/invitable_friends")
    fun getInvitableFriends(
            @Query("access_token") accessToken: String,
            @Query("fields") fields: String = "id,birthday,gender,picture,name",
            @Query("limit") limit: Int = 200,
            @Query("offset") offset: Int = 0
    ): Flowable<ZaloResponse<Array<ZaloUserModel>>>
}

object ZaloAPI {
    fun getProfile(accessToken: String): Flowable<ZaloResponse<ZaloUserModel>> {
        return ZaloAPIService.graph(ZaloUserModel::class.java).getProfile(accessToken)
    }

    fun getProfile(oauthCode: String, appId: String, appSecret: String): Flowable<ZaloResponse<ZaloUserModel>> {
        return getAccessToken(oauthCode, appId, appSecret)
                .subscribeOn(Schedulers.io())
                .observeOn(Schedulers.io())
                .map {
                    if (it.isSuccess())
                        it.data!!.access_token
                    else throw Exception(it.message)
                }.flatMap {
                    getProfile(it)
                }
    }

    fun getAccessToken(oauthCode: String, appId: String, appSecret: String): Flowable<ZaloResponse<ZaloUserModel.Token>> {
        return ZaloAPIService.oauth(ZaloUserModel.Token::class.java).getAccessToken(oauthCode, appId, appSecret)
    }

    fun getInvitableFriends(accessToken: String): Flowable<ZaloResponse<Array<ZaloUserModel>>> {
        return ZaloAPIService.graph(Array<ZaloUserModel>::class.java).getInvitableFriends(accessToken)
    }

}
