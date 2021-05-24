package com.example.ds_rest.model;

import java.text.DecimalFormat;

public class CharacterInfo {

    private final static DecimalFormat decimalFormat = new DecimalFormat("0.00");

    private String name;
    private float height;
    private float mass;
    private float bmi;

    public CharacterInfo(String name, float height, float mass) {
        this.name = name;
        this.height = height / 100;
        this.mass = mass;
        String s = decimalFormat.format((double) (this.mass / Math.pow(this.height, 2.0))).replace(",", ".");
        this.bmi = Float.parseFloat(s);
    }

    public String toHtml(String headerTag, String bodyTag) {
        String endHeaderTag = headerTag.replace("<","</");
        String endBodyTag = bodyTag.replace("<","</");

        return headerTag + "name: " + name + endHeaderTag +
                bodyTag + "height: " + height + "m" + endBodyTag +
                bodyTag + "weight: " + mass + "kg" + endBodyTag +
                bodyTag + "bmi: " + bmi + endBodyTag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getHeight() {
        return height;
    }

    public void setHeight(float height) {
        this.height = height;
    }

    public float getMass() {
        return mass;
    }

    public void setMass(float mass) {
        this.mass = mass;
    }

    public float getBmi() {
        return bmi;
    }

    public void setBmi(float bmi) {
        this.bmi = bmi;
    }
}
