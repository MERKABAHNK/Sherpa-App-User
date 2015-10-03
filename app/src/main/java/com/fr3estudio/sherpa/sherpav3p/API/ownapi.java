package com.fr3estudio.sherpa.sherpav3p.API;

import com.fr3estudio.sherpa.sherpav3p.model.DirectionResults;
import com.fr3estudio.sherpa.sherpav3p.model.Response;

import retrofit.Callback;
import retrofit.http.Field;
import retrofit.http.FormUrlEncoded;
import retrofit.http.GET;
import retrofit.http.POST;
import retrofit.http.Query;

/**
 * Created by Alfr3 on 9/14/2015.
 */
public interface ownapi {

    @FormUrlEncoded
    @POST("/sherpa_query_service.php")
    void executeCommand(@Field("commandId") String comm, @Field("params")String parameters, Callback<Response> callback);

    @FormUrlEncoded
    @POST("/recordar_passwd.php")
    void rememberPassword(@Field("user_info") String user, Callback<Response> callback);

    @GET("/maps/api/directions/json")
    public void getJson(@Query("origin") String origin,@Query("destination") String destination, Callback<DirectionResults> callback);
}
