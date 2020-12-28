package live.tek.form_data.network

import live.tek.form_data.QuestionsResponse
import live.tek.form_data.Result
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.http.*

interface IApi {

    @Multipart
    @POST("api/upload-images")
    fun uploadImage( @Part image:MultipartBody.Part):Call<Result>


}