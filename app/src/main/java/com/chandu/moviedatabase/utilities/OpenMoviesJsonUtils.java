/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chandu.moviedatabase.utilities;

import android.content.Context;

import com.chandu.moviedatabase.Movie;
import com.chandu.moviedatabase.MovieReviews;
import com.chandu.moviedatabase.MovieTrailer;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Utility functions to handle OpenWeatherMap JSON data.
 */
public final class OpenMoviesJsonUtils {
    /**
     * This method parses JSON from a web response and returns an array of Strings
     * describing the weather over various days from the forecast.
     * <p/>
     * Later on, we'll be parsing the JSON into structured data within the
     * getFullWeatherDataFromJson function, leveraging the data we have stored in the JSON. For
     * now, we just convert the JSON into human-readable strings.
     *
     * @param moviesJsonStr JSON response from server
     * @return Array of Strings describing weather data
     * @throws JSONException If JSON data cannot be properly parsed
     */
    public static List<Movie> getMovieDetailsFromJson(Context context, String moviesJsonStr)
            throws JSONException {
        List<Movie> movies = null;
        final String API_RESULTS = "results";

        Gson gson;

        JSONObject movieJson = new JSONObject(moviesJsonStr);

        JSONArray resultsArray = movieJson.getJSONArray(API_RESULTS);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        if (resultsArray.length() > 0) {
            movies = Arrays.asList(gson.fromJson(resultsArray.toString(), Movie[].class));
        }

        return movies;
    }

    public static List<MovieTrailer> getMovieTrailersFromJson(Context context, String movieTrailersJson)
            throws JSONException {
        List<MovieTrailer> movieTrailers = null;
        final String API_RESULTS = "results";

        Gson gson;

        JSONObject movieJson = new JSONObject(movieTrailersJson);

        JSONArray resultsArray = movieJson.getJSONArray(API_RESULTS);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        if (resultsArray.length() > 0) {
            movieTrailers = Arrays.asList(gson.fromJson(resultsArray.toString(), MovieTrailer[].class));
        }

        return movieTrailers;
    }

    public static List<MovieReviews> getMovieReviewsFromJson(Context context, String movieReviewsJson)
            throws JSONException {
        List<MovieReviews> movieReviews = null;
        final String API_RESULTS = "results";

        Gson gson;

        JSONObject movieJson = new JSONObject(movieReviewsJson);

        JSONArray resultsArray = movieJson.getJSONArray(API_RESULTS);

        GsonBuilder gsonBuilder = new GsonBuilder();
        gsonBuilder.setDateFormat("M/d/yy hh:mm a");
        gson = gsonBuilder.create();

        if (resultsArray.length() > 0) {
            movieReviews = Arrays.asList(gson.fromJson(resultsArray.toString(), MovieReviews[].class));
        } else {
            movieReviews = new ArrayList<>();
        }

        return movieReviews;
    }
}