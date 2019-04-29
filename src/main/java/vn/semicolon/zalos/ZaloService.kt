package vn.semicolon.zalos


object ZaloAPIService {
    const val ZALO_OAUTH_HOST = "https://oauth.zaloapp.com/v3/"
    const val ZALO_GRAPH_HOST = "https://graph.zalo.me/v2.0/"
    fun <O> oauth(resultClass: Class<O>): IZaloOauthAPI {
        return SemiAPI.createService(IZaloOauthAPI::class.java, resultClass, ZALO_OAUTH_HOST)
    }

    fun <O> graph(resultClass: Class<O>): IZaloGraphAPI {
        return SemiAPI.createService(IZaloGraphAPI::class.java, resultClass, ZALO_GRAPH_HOST)
    }
}

data class ZaloResponse<T>(
        var summary: Summary? = null,
        var data: T? = null,
        var paging: Paging? = null,
        var code: Int? = null,
        var message: String? = null
) {
    data class Paging(
            var next: String? = null,
            var previous: String? = null
    )

    data class Summary(
            var total_count: Int = 0
    )

    fun isSuccess() = message.isNullOrEmpty() && code == null
}