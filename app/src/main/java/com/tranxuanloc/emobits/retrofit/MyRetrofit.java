package com.tranxuanloc.emobits.retrofit;

import android.content.Context;

import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.logging.HttpLoggingInterceptor;
import com.tranxuanloc.emobits.utilities.API;

import java.util.concurrent.TimeUnit;

import retrofit.GsonConverterFactory;
import retrofit.Retrofit;

/**
 * Created by Trần Xuân Lộc on 1/3/2016.
 */
public class MyRetrofit {

    public static MyRequests initRequest(Context context) {
        OkHttpClient client = new OkHttpClient();
        client.setConnectTimeout(10, TimeUnit.SECONDS);
        client.setWriteTimeout(10, TimeUnit.SECONDS);
        client.setReadTimeout(10, TimeUnit.SECONDS);
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        client.interceptors().add(interceptor);
        Retrofit retrofit = new Retrofit.Builder().baseUrl(API.BASE_URL).client(client).addConverterFactory(GsonConverterFactory.create()).build();
        return retrofit.create(MyRequests.class);
    }
}
