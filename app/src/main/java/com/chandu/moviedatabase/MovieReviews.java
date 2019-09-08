package com.chandu.moviedatabase;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class MovieReviews implements Serializable {
    public MovieReviews() {
    }

    @SerializedName("author")
    public String name;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @SerializedName("content")
    public String content;
}
