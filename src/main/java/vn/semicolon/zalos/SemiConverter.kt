package vn.semicolon.zalos

import com.google.gson.Gson
import okhttp3.MediaType
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Converter
import retrofit2.Retrofit
import java.lang.reflect.Type


/**
 * Created by HuynhMH on 9/4/2019
 */
class SemiZaloConverter<O>(var clazz: Class<O>) : Converter.Factory() {
    override fun requestBodyConverter(
            type: Type?,
            parameterAnnotations: Array<out Annotation>?,
            methodAnnotations: Array<out Annotation>?,
            retrofit: Retrofit?
    ): Converter<*, RequestBody>? {
        return SemiZaloRequestBodyConverter()
    }

    override fun responseBodyConverter(
            type: Type?,
            annotations: Array<out Annotation>?,
            retrofit: Retrofit?
    ): Converter<ResponseBody, *>? {
        return SemiZaloResponseBodyConverter(clazz)
    }

    /**
     * Convert custom request body to RequestBody
     */
    class SemiZaloRequestBodyConverter : Converter<JSONObject, RequestBody> {
        private val MEDIA_TYPE = MediaType.parse("application/json; charset=UTF-8")

        override fun convert(value: JSONObject?): RequestBody {
            return RequestBody.create(MEDIA_TYPE, value.toString().toByteArray())
        }
    }

    /**
     * Convert ResponseBody to custom response
     * NOTE: if you has error when convert List<O> from JsonArray [Expected BEGIN_OBJECT but was BEGIN_ARRAY]
     * Let you use Array<O>::class.java as return type instead of List<O>::class.java
     */
    class SemiZaloResponseBodyConverter<O>(private var clazz: Class<O>) : Converter<ResponseBody, ZaloResponse<O>> {
        override fun convert(value: ResponseBody?): ZaloResponse<O> {
            value?.string().apply {
                val res = JSONObject(this)
                if (res.has("error")) {
                    val msg = if (res.has("error_description"))
                        res.optString("error_description")
                    else if (res.has("error_name"))
                        res.optString("error_name")
                    else res.optString("message")
                    return ZaloResponse(
                            code = res.optInt("error"),
                            message = msg
                    )
                }
                var paging: ZaloResponse.Paging? = null
                if (res.has("paging"))
                    paging = Gson().fromJson<ZaloResponse.Paging>(res.optString("paging"), ZaloResponse.Paging::class.java)

                var sum: ZaloResponse.Summary? = null
                if (res.has("summary"))
                    sum = Gson().fromJson<ZaloResponse.Summary>(res.optString("summary"), ZaloResponse.Summary::class.java)
                val data: O?
                data = if (res.has("data"))
                    Gson().fromJson<O>(res.optString("data"), clazz)
                else
                    Gson().fromJson<O>(res.toString(), clazz)
                return ZaloResponse(
                        paging = paging,
                        summary = sum,
                        data = data
                )
            }
        }
    }
}

