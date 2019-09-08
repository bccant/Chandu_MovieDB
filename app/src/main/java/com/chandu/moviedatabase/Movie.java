package com.chandu.moviedatabase;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

public class Movie implements Serializable {

    public Movie() {
    }

    public String getVote() {
        return Vote;
    }

    public void setVote(String vote) {
        Vote = vote;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getPoster() {
        return Poster;
    }

    public void setPoster(String poster) {
        Poster = poster;
    }

    public String getSummary() {
        return Summary;
    }

    public void setSummary(String summary) {
        Summary = summary;
    }

    public String getReleaseData() {
        return ReleaseData;
    }

    public void setReleaseData(String releaseData) {
        ReleaseData = releaseData;
    }

    public String getId() {
        return Id;
    }

    public void setId(String id) {
        Id = id;
    }

    @SerializedName("vote_average")
    public String Vote;

    @SerializedName("original_title")
    public String Title;

    @SerializedName("poster_path")
    public String Poster;

    @SerializedName("overview")
    public String Summary;

    @SerializedName("release_date")
    public String ReleaseData;

    @SerializedName("id")
    public String Id;

    public Boolean getFav() {
        return isFav;
    }

    public void setFav(Boolean fav) {
        isFav = fav;
    }

    @SerializedName("fav")
    public Boolean isFav = false;
}
