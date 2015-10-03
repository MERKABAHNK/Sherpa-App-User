package com.fr3estudio.sherpa.sherpav3p;

import com.fr3estudio.sherpa.sherpav3p.API.ownapi;
import com.squareup.okhttp.OkHttpClient;

import retrofit.RestAdapter;
import retrofit.client.OkClient;

/**
 * Created by Alfr3 on 8/24/2015.
 */
public class RestMapClient {

    private static ownapi REST_CLIENT;
    private static String ROOT = "http://maps.googleapis.com/";
    //private static String ROOT = "http://fitcars.com.co/app";

    static{
        setupRestClient();
    }

    private RestMapClient(){}

    public static ownapi get() { return REST_CLIENT;}

    private static void setupRestClient(){
        RestAdapter.Builder builder = new RestAdapter.Builder()
                .setEndpoint(ROOT)
                .setClient(new OkClient(new OkHttpClient())).setLogLevel(RestAdapter.LogLevel.FULL);
        RestAdapter restAdapter = builder.build();

        REST_CLIENT = restAdapter.create(ownapi.class);
    }


}
