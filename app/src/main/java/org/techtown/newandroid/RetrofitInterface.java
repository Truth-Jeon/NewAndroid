package org.techtown.newandroid;

import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

import kotlin.jvm.JvmMultifileClass;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;

public interface RetrofitInterface {

    @POST("/login")
    Call<LoginActivity> executeLogin(@Body HashMap<String, String> map);

    @POST("/register")
    Call<Void> executeRegister(@Body HashMap<String, String> map);

    @Multipart
    @POST("/upload")
    Call<ResponseBody> postImage(@Part MultipartBody.Part image, @Part("upload") RequestBody name);
}