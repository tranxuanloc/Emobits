package com.tranxuanloc.emobits.retrofit;


import com.tranxuanloc.emobits.dj.DJInfo;
import com.tranxuanloc.emobits.main.UserInfo;
import com.tranxuanloc.emobits.search.SessionInfo;

import retrofit.Call;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.POST;

/**
 * Created by Trần Xuân Lộc on 1/3/2016.
 */
public interface MyRequests {
    @FormUrlEncoded
    @POST("user")
    Call<UserInfo> user(@Field("device") String device, @Field("name") String name, @Field("param") String param);

    @FormUrlEncoded
    @POST("user/score")
    Call<DJInfo> getDJScore(@Field("device") String device);

    @FormUrlEncoded
    @POST("user/music")
    Call<SessionInfo> getListSession(@Field("device") String device);
}
