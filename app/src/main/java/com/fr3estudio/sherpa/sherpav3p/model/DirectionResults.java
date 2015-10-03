package com.fr3estudio.sherpa.sherpav3p.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

/**
 * Created by Alfr3 on 9/24/2015.
 */
public class DirectionResults {

    @SerializedName("routes")
    private List<Route> routes;

    public List<Route> getRoutes() {
        return routes;
    }}

