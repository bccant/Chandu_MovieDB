package com.chandu.moviedatabase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.chandu.moviedatabase.Database.MovieDatabase;
import com.chandu.moviedatabase.Database.MovieEntry;
import com.chandu.moviedatabase.utilities.NetworkUtils;
import com.chandu.moviedatabase.utilities.OpenMoviesJsonUtils;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity implements MoviesAdapter.MoviesAdapterOnClickHandler,
        LoaderManager.LoaderCallbacks<List<Movie>> {
    private RecyclerView mRecyclerView;
    private TextView mErrorMessageDisplay;
    private ProgressBar mLoadingIndicator;
    private MoviesAdapter moviesAdapter;
    private static final int MOVIE_DETAILS_LOADER = 22;
    private static final String SEARCH_QUERY_URL_EXTRA = "query";
    final String moviesPopular = "popular?api_key=";
    final String moviesRated = "top_rated?api_key=";
    final String movieDB = "http://api.themoviedb.org/3/movie/";
    final String movieDBAPIKey = BuildConfig.ApiKey;
    final String movieObject = "MovieObject";
    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    private MovieDatabase mDB;
    private int optionID = R.id.sort_popular;
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    Parcelable savedRecyclerLayoutState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mRecyclerView = (RecyclerView) findViewById(R.id.recyclerview_forecast);
        mErrorMessageDisplay = (TextView) findViewById(R.id.tv_error_message_display);

        GridLayoutManager layoutManager
                = new GridLayoutManager(this, calculateNoOfColumns(this));

        mRecyclerView.setLayoutManager(layoutManager);
        //mRecyclerView.setHasFixedSize(true);

        moviesAdapter = new MoviesAdapter(this);
        mRecyclerView.setAdapter(moviesAdapter);

        mLoadingIndicator = (ProgressBar) findViewById(R.id.pb_loading_indicator);
        mDB = MovieDatabase.getsInstance(getApplicationContext());

        if (savedInstanceState != null) {
            optionID = savedInstanceState.getInt("SORT");
            Log.d("OnCreate", "SAVED INSTANCE is not NULL " + optionID);
        } else {
            Log.d("OnCreate", "SAVED INSTANCE is NULL");
        }

        loadMovies(optionID);
    }

    public static int calculateNoOfColumns(Context context) {
        DisplayMetrics displayMetrics = context.getResources().getDisplayMetrics();
        float dpWidth = displayMetrics.widthPixels / displayMetrics.density;
        int scalingFactor = 200;
        int noOfColumns = (int) (dpWidth / scalingFactor);
        if(noOfColumns < 2)
            noOfColumns = 2;
        return noOfColumns;
    }

    private boolean loadMovies(final int optionValues) {
        boolean result = true;
        String ratingType;

        Throwable throwable = new Throwable();
        throwable.printStackTrace();

        switch (optionValues) {
            case R.id.sort_popular:
                ratingType = moviesPopular;
                loadMovieData(ratingType);
                break;
            case R.id.sort_rating:
                ratingType = moviesRated;
                loadMovieData(ratingType);
                break;
            case R.id.sort_favourite:
                optionID = optionValues;
                Log.d("loadMovies", "calling loadFavMovieData ");
                loadFavMovieData();
                break;
            default:
                result = false;
                break;
        }

        Log.d("loadMovies", "Option is " + optionValues);

        return result;
    }

    private void loadMovieData(final String ratingType) {

        Log.d("loadMovieData", "Option of loadMovieData started");
        showMovieDataView();

        String movieDBURL = movieDB + ratingType + movieDBAPIKey;

        Bundle queryBundle = new Bundle();
        queryBundle.putString(SEARCH_QUERY_URL_EXTRA, movieDBURL);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Movie>> movieLoader = loaderManager.getLoader(MOVIE_DETAILS_LOADER);

        if (movieLoader == null) {
            loaderManager.initLoader(MOVIE_DETAILS_LOADER, queryBundle, this);
        } else {
            loaderManager.restartLoader(MOVIE_DETAILS_LOADER, queryBundle, this);
        }
    }

    private void loadFavMovieData() {
        final LiveData<List<MovieEntry>> movieEntry = mDB.movieDAO().loadAllTasks();
        movieEntry.observe(this, new Observer<List<MovieEntry>>() {
            @Override
            public void onChanged(@Nullable List<MovieEntry> movieEntries) {
                if (movieEntries.isEmpty()) {
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    showDBErrorMessage();
                } else {
                    final List<Movie> movieData = new ArrayList<>();
                    for (int i = 0; i < movieEntries.size(); i++) {
                        Movie newMovie = new Movie();
                        newMovie.setId(movieEntries.get(i).getMovieID());
                        newMovie.setPoster(movieEntries.get(i).getMoviePoster());
                        newMovie.setReleaseData(movieEntries.get(i).getMovieDate());
                        newMovie.setSummary(movieEntries.get(i).getMoviePlot());
                        newMovie.setTitle(movieEntries.get(i).getMovieTitle());
                        newMovie.setVote(movieEntries.get(i).getMovieRating());
                        newMovie.setFav(movieEntries.get(i).getMovieIsFav());

                        movieData.add(newMovie);
                    }
                    mLoadingIndicator.setVisibility(View.INVISIBLE);
                    moviesAdapter.setMovieDetailsFromDB(movieData);
                    showMovieDataView();
                }
            }
        });

    }

    private void showMovieDataView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showDBErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setText(R.string.empty_db);
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(Movie movieDetails) {
        Context context = this;
        Intent intentToStartDetails = new Intent(context, MovieDetailsActivity.class);

        intentToStartDetails.putExtra(movieObject, movieDetails);
        startActivity(intentToStartDetails);
    }

    @NonNull
    @Override
    public Loader<List<Movie>> onCreateLoader(int i, final Bundle args) {
        return new AsyncTaskLoader<List<Movie>>(this) {

            @Override
            protected void onStartLoading() {
                super.onStartLoading();
                if (args == null) {
                    return;
                }
                mLoadingIndicator.setVisibility(View.VISIBLE);

                forceLoad();
            }

            @Nullable
            @Override
            public List<Movie> loadInBackground() {
                String movieDBURL = args.getString(SEARCH_QUERY_URL_EXTRA);

                if (movieDBURL == null || TextUtils.isEmpty(movieDBURL)) {
                    return null;
                }

                URL newURL = null;
                try {
                    newURL = new URL(movieDBURL);
                } catch (MalformedURLException e) {
                    e.printStackTrace();
                }

                try {
                    String jsonMovieDBResponse = NetworkUtils
                            .getResponseFromHttpUrl(newURL);

                    List<Movie> simpleJsonMovieData = OpenMoviesJsonUtils.getMovieDetailsFromJson(MainActivity.this, jsonMovieDBResponse);

                    return simpleJsonMovieData;

                } catch (Exception e) {
                    e.printStackTrace();
                    return null;
                }
            }
        };
    }

    @Override
    public void onLoadFinished(@NonNull Loader<List<Movie>> loader, List<Movie> movieData) {
        if (optionID != R.id.sort_favourite) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieData != null) {
                Throwable throwable = new Throwable();
                throwable.printStackTrace();
                showMovieDataView();
                moviesAdapter.setMovieDetails(movieData);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
            } else {
                showErrorMessage();
            }
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<List<Movie>> loader) {
        getLoaderManager().destroyLoader(MOVIE_DETAILS_LOADER);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.sort_type, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        optionID = item.getItemId();

        moviesAdapter.setMovieDetails(null);

        loadMovies(optionID);

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt("SORT", optionID);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        optionID = savedInstanceState.getInt("SORT");
        if (savedInstanceState != null) {
            savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
            mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        loadMovies(optionID);
    }
}
