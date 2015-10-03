package com.fr3estudio.sherpa.sherpav3p.model;

/**
 * Created by Alfr3 on 9/24/2015.
 */
public class Data {

    String text;
    double value;

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public double getValue() {
        return value/1000;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
