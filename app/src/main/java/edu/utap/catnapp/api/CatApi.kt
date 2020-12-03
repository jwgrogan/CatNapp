package edu.utap.catnapp.api

import android.text.SpannableString
import com.google.gson.GsonBuilder
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import okhttp3.HttpUrl
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import java.lang.reflect.Type

interface CatApi {
    @GET("/v1/images/search?limit=10")
    suspend fun getNineCats(@Query("category_ids") categories: String, @Header("x-api-key") key: String) : List<CatPost>


    // I just looked at the response and "parsed" it by eye
//    data class CatResponse(val results: List<CatPost>)
//    class ListingResponse(val data: ListingData)

//    class ListingData(
//        val children: List<KittenResponse>,
//        val after: String?,
//        val before: String?
//    )
//    data class CatResponse(val data: List<CatPost>)
//    data class CatResponse(val data: CatPost)

//    class SpannableDeserializer : JsonDeserializer<SpannableString> {
//        // @Throws(JsonParseException::class)
//        override fun deserialize(
//            json: JsonElement,
//            typeOfT: Type,
//            context: JsonDeserializationContext
//        ): SpannableString {
//            return SpannableString(json.asString)
//        }
//    }

    companion object {
        // Tell Gson to ignore fields without @expose
//        private fun buildGsonConverterFactory(): GsonConverterFactory {
//            val gsonBuilder = GsonBuilder().excludeFieldsWithoutExposeAnnotation()
//            return GsonConverterFactory.create(gsonBuilder.create())
//        }

        var httpurl = HttpUrl.Builder()
            .scheme("https")
            .host("api.thecatapi.com")
            .build()
        fun create(): CatApi = create(httpurl)
        private fun create(httpUrl: HttpUrl): CatApi {
            val client = OkHttpClient.Builder()
                .addInterceptor(HttpLoggingInterceptor().apply {
                    // Enable basic HTTP logging to help with debugging.
                    this.level = HttpLoggingInterceptor.Level.BASIC
                })
                .build()
            return Retrofit.Builder()
                .baseUrl(httpUrl)
                .client(client)
//                .addConverterFactory(buildGsonConverterFactory())
                .addConverterFactory(GsonConverterFactory.create())
                .build()
                .create(CatApi::class.java)
        }
    }
}