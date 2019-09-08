package com.chandu.moviedatabase;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MovieTrailer implements Serializable {
    public MovieTrailer() {
    }

    @SerializedName("name")
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    @SerializedName("key")
    public String key;

}
