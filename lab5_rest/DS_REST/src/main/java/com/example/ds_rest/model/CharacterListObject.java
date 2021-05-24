package com.example.ds_rest.model;

import org.json.simple.JSONObject;

import java.util.List;

public class CharacterListObject {
    private long count;
    private String next;
    private String previous;
    private List<JSONObject> results;

    public CharacterListObject() {
    }

    public CharacterListObject(long count, String next, String previous, List<JSONObject> results) {
        this.count = count;
        this.next = next;
        this.previous = previous;
        this.results = results;
    }

    public long getCount() {
        return count;
    }

    public void setCount(long count) {
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

    public List<JSONObject> getResults() {
        return results;
    }

    public void setResults(List<JSONObject> results) {
        this.results = results;
    }
}
