package com.example.ds_rest.model;

import java.util.ArrayList;
import java.util.LinkedHashMap;

public class CharacterResponse {

    private int count;
    private String next;
    private String previous;
    private ArrayList<LinkedHashMap<String, Object>> results;

    public CharacterResponse(int count, String next, String previous, ArrayList<LinkedHashMap<String, Object>> results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    public CharacterResponse() {
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public String getNext() {
        return next;
    }

    public void setNext(String next) {
        this.next = next;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public ArrayList<LinkedHashMap<String, Object>> getResults() {
        return results;
    }

    public void setResults(ArrayList<LinkedHashMap<String, Object>> results) {
        this.results = results;
    }
}
