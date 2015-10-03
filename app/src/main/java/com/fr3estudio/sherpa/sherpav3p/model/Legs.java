package com.fr3estudio.sherpa.sherpav3p.model;

import java.util.List;

public class Legs {
    private List<Steps> steps;
    Data distance;
    Data duration;

    public List<Steps> getSteps() {
        return steps;
    }

    public void setSteps(List<Steps> steps) {
        this.steps = steps;
    }

    public Data getDistance() {
        return distance;
    }

    public void setDistance(Data distance) {
        this.distance = distance;
    }

    public Data getDuration() {
        return duration;
    }

    public void setDuration(Data duration) {
        this.duration = duration;
    }
}
