package org.techtown.newandroid;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientApi {
    private static Retrofit retrofit;
//    private static String BASE_URL = "http://192.168.0.24:3001";
//    private static String BASE_URL = "http://172.30.1.15:3001";
    private static String BASE_URL = "http://192.168.0.111:3001";

    public static Retrofit getClientApi()
    {
        Gson gson = new GsonBuilder().setLenient().create();

        if(retrofit == null)
        {
            retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .build();
        }
        return retrofit;
    }
}
