package com.example.ds_rest.model;

public class StatsInfo {

    private CharacterInfo lowest;
    private CharacterInfo tallest;
    private CharacterInfo lightest;
    private CharacterInfo heaviest;
    private float avgHeight;
    private float avgWeight;
    private float avgBmi;

    public StatsInfo() {
    }

    public String toHtml() {
        return "<h1>STATS FOR STAR WARS CHARACTERS</h1>" +
                "<h3>avgHeight: " + avgHeight + "m</h3>" +
                "<h3>avgWeight: " + avgWeight + "kg</h3>" +
                "<h3>avgBmi: " + avgBmi + "</h3>" +
                "<hr>" +
                "<h3>tallest:</h3>" + tallest.toHtml("<h4>", "<p>") +
                "<hr>" +
                "<h3>lowest:</h3>" + lowest.toHtml("<h4>", "<p>") +
                "<hr>" +
                "<h3>lightest:</h3>" + lightest.toHtml("<h4>", "<p>") +
                "<hr>" +
                "<h3>heaviest:</h3>" + heaviest.toHtml("<h4>", "<p>");
    }

    public CharacterInfo getLowest() {
        return lowest;
    }

    public void setLowest(CharacterInfo lowest) {
        this.lowest = lowest;
    }

    public CharacterInfo getTallest() {
        return tallest;
    }

    public void setTallest(CharacterInfo tallest) {
        this.tallest = tallest;
    }

    public CharacterInfo getLightest() {
        return lightest;
    }

    public void setLightest(CharacterInfo lightest) {
        this.lightest = lightest;
    }

    public CharacterInfo getHeaviest() {
        return heaviest;
    }

    public void setHeaviest(CharacterInfo heaviest) {
        this.heaviest = heaviest;
    }

    public float getAvgHeight() {
        return avgHeight;
    }

    public void setAvgHeight(float avgHeight) {
        this.avgHeight = avgHeight;
    }

    public float getAvgWeight() {
        return avgWeight;
    }

    public void setAvgWeight(float avgWeight) {
        this.avgWeight = avgWeight;
    }

    public float getAvgBmi() {
        return avgBmi;
    }

    public void setAvgBmi(float avgBmi) {
        this.avgBmi = avgBmi;
    }
}
