package com.chandu.moviedatabase.Database;

import androidx.room.Entity;
import androidx.room.PrimaryKey;
import androidx.annotation.NonNull;

@Entity(tableName = "cinema")
public class MovieEntry {
    @PrimaryKey(autoGenerate = false)
    @NonNull
    private String movieID;
    private String movieTitle;
    private String moviePlot;
    private String movieRating;
    private String movieDate;
    private String moviePoster;
    private Boolean movieIsFav = false;

    public MovieEntry(@NonNull String movieID, String movieTitle, String moviePlot, String movieRating,
                      String movieDate, String moviePoster, Boolean movieIsFav) {
        this.movieID = movieID;
        this.movieTitle = movieTitle;
        this.moviePlot = moviePlot;
        this.movieRating = movieRating;
        this.movieDate = movieDate;
        this.moviePoster = moviePoster;
        this.movieIsFav = movieIsFav;
    }

    public String getMovieID() {
        return movieID;
    }

    public void setMovieID(String movieID) {
        this.movieID = movieID;
    }

    public String getMovieTitle() {
        return movieTitle;
    }

    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    public String getMoviePlot() {
        return moviePlot;
    }

    public void setMoviePlot(String moviePlot) {
        this.moviePlot = moviePlot;
    }

    public String getMovieRating() {
        return movieRating;
    }

    public void setMovieRating(String movieRating) {
        this.movieRating = movieRating;
    }

    public String getMovieDate() {
        return movieDate;
    }

    public void setMovieDate(String movieDate) {
        this.movieDate = movieDate;
    }

    public String getMoviePoster() {
        return moviePoster;
    }

    public void setMoviePoster(String moviePoster) {
        this.moviePoster = moviePoster;
    }

    public Boolean getMovieIsFav() {
        return movieIsFav;
    }

    public void setMovieIsFav(Boolean movieIsFav) {
        this.movieIsFav = movieIsFav;
    }
}
