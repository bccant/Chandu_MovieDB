package com.chandu.moviedatabase;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.Observer;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.AsyncTaskLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.BounceInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.chandu.moviedatabase.Database.MovieDatabase;
import com.chandu.moviedatabase.Database.MovieEntry;
import com.chandu.moviedatabase.utilities.NetworkUtils;
import com.chandu.moviedatabase.utilities.OpenMoviesJsonUtils;
import com.squareup.picasso.Picasso;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MovieDetailsActivity extends AppCompatActivity implements MovieTrailerAdapter.MovieTrailerAdapterOnClickHandler {
    @BindView(R.id.recyclerview_trailer)
    RecyclerView mRecyclerView;
    @BindView(R.id.recyclerview_review)
    RecyclerView mRecyclerViewReview;
    @BindView(R.id.trailer_error_message_display)
    TextView mErrorMessageDisplay;
    @BindView(R.id.trailer_loading_indicator)
    ProgressBar mLoadingIndicator;
    @BindView(R.id.review_error_message_display)
    TextView mErrorMessageDisplayReview;
    @BindView(R.id.review_loading_indicator)
    ProgressBar mLoadingIndicatorReview;
    @BindView(R.id.movie_poster)
    ImageView moviePoster;
    @BindView(R.id.movie_title)
    TextView movieTitle;
    @BindView(R.id.movie_plot)
    TextView moviePlot;
    @BindView(R.id.movie_ratings)
    TextView movieRatings;
    @BindView(R.id.movie_date)
    TextView movieDate;
    @BindView(R.id.scroll_view)
    ScrollView mScrollView;
    @BindView(R.id.fav_button)
    ToggleButton mFavMovie;
    private MovieTrailerAdapter movieTrailerAdapter;
    private MovieReviewAdapter movieReviewAdapter;
    private Movie pictureDetails;
    final String movieObject = "MovieObject";
    private int[] scrollPositionArray;
    private static final int MOVIE_TRAILERS_LOADER = 33;
    private static final String SEARCH_TRAILER_EXTRA = "trailer";
    private static final int MOVIE_REVIEWS_LOADER = 44;
    private static final String SEARCH_REVIEWS_EXTRA = "reviews";
    private static final String SEARCH_MOVIEID_EXTRA = "movieID";
    final String moviesTrailer = "/videos?api_key=";
    final String moviesReview = "/reviews?api_key=";
    final String movieDB = "http://api.themoviedb.org/3/movie/";
    final String movieDBAPIKey = BuildConfig.ApiKey;
    final String trailerObject = "TrailerObject";
    final String youTubeLink = "https://www.youtube.com/watch?v=";
    String movieID = "";
    String scrollPosition;
    LinearLayoutManager linearLayoutManager, linearLayoutManagerReview;
    private final String KEY_RECYCLER_STATE = "recycler_state";
    private static final String BUNDLE_RECYCLER_LAYOUT = "classname.recycler.layout";
    private boolean reviewsLoaded;
    private boolean trailersLoaded;
    private MovieDatabase mDb;
    LiveData<MovieEntry> currentMovieIsFav;
    Parcelable savedRecyclerLayoutState;
    Parcelable savedRecyclerLayoutStateReview;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_details_2);

        ButterKnife.bind(this);

        mDb = MovieDatabase.getsInstance(getApplicationContext());

        initializeRecyclerViews();

        updateMovieDetails();

        onSaveMovie();

        loadMovieData(SEARCH_TRAILER_EXTRA);

        loadMovieData(SEARCH_REVIEWS_EXTRA);
    }

    private void initializeRecyclerViews() {
        linearLayoutManager =
                new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false);

        mRecyclerView.setLayoutManager(linearLayoutManager);
        movieTrailerAdapter = new MovieTrailerAdapter(this);
        mRecyclerView.setAdapter(movieTrailerAdapter);

        linearLayoutManagerReview =
                new LinearLayoutManager(this);

        mRecyclerViewReview.setLayoutManager(linearLayoutManagerReview);
        movieReviewAdapter = new MovieReviewAdapter();
        mRecyclerViewReview.setAdapter(movieReviewAdapter);
    }

    private void updateMovieDetails() {
        Intent intent = getIntent();

        if ((intent != null) && intent.hasExtra(movieObject)) {
            pictureDetails = (Movie) intent.getSerializableExtra(movieObject);

            String posterURL = "http://image.tmdb.org/t/p/w185/" +
                    pictureDetails.getPoster();
            Picasso.get().load(posterURL).into(moviePoster);

            if (pictureDetails.getTitle() != null || !pictureDetails.getTitle().equals("")) {
                movieTitle.setText(pictureDetails.getTitle());
            } else {
                movieTitle.setVisibility(View.INVISIBLE);
            }

            if (pictureDetails.getVote() != null || !pictureDetails.getVote().equals("")) {
                movieRatings.setText(getString(R.string.movie_rating) + "\n" + pictureDetails.getVote());
            } else {
                movieRatings.setVisibility(View.INVISIBLE);
            }

            if (pictureDetails.getReleaseData() != null || !pictureDetails.getReleaseData().equals("")) {
                movieDate.setText(getString(R.string.movie_release_data) + "\n" + pictureDetails.getReleaseData());
            } else {
                movieDate.setVisibility(View.INVISIBLE);
            }

            if (pictureDetails.getSummary() != null || !pictureDetails.getSummary().equals("")) {
                moviePlot.setText(pictureDetails.getSummary());
            } else {
                moviePlot.setVisibility(View.INVISIBLE);
            }

            if (pictureDetails.getId() != null || !pictureDetails.getId().equals("")) {
                movieID = pictureDetails.getId();
            } else {
                movieID = "";
            }

            setFavButton();

        }
    }

    private void setFavButton() {
        currentMovieIsFav = mDb.movieDAO().loadFavMovieById(pictureDetails.getId());
        currentMovieIsFav.observe(this, new Observer<MovieEntry>() {
            @Override
            public void onChanged(@Nullable MovieEntry currentMovie) {
                if (currentMovie != null && currentMovie.getMovieIsFav()) {
                    mFavMovie.setTextOn("Remove");
                    mFavMovie.setChecked(true);
                    pictureDetails.setFav(true);
                } else {
                    mFavMovie.setTextOff("Add");
                }
            }
        });
    }

    @Override
    public void onClick(MovieTrailer movieTrailer) {
        Context context = this;
        String movieUrl;

        if (movieTrailer != null && movieTrailer.getKey() != null &&
                !TextUtils.isEmpty(movieTrailer.getKey())) {
            movieUrl = youTubeLink + movieTrailer.getKey();
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(movieUrl));
            startActivity(intent);
        }

    }

    void restoreScrollPoisition() {
        if (scrollPosition != null) {
            mScrollView.post(new Runnable() {
                @Override
                public void run() {
                    mScrollView.scrollTo(scrollPositionArray[0], scrollPositionArray[1]);
                }
            });
        }
    }

    public class MovieTrailerLoader implements LoaderManager.LoaderCallbacks<List<MovieTrailer>> {
        @NonNull
        @Override
        public Loader<List<MovieTrailer>> onCreateLoader(int i, final Bundle args) {
            return new AsyncTaskLoader<List<MovieTrailer>>(getApplicationContext()) {

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
                public List<MovieTrailer> loadInBackground() {
                    String movieDBURL = args.getString(SEARCH_TRAILER_EXTRA);

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

                        List<MovieTrailer> simpleJsonMovieData = OpenMoviesJsonUtils.getMovieTrailersFromJson(MovieDetailsActivity.this, jsonMovieDBResponse);

                        return simpleJsonMovieData;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<MovieTrailer>> loader, List<MovieTrailer> movieTrailers) {
            mLoadingIndicator.setVisibility(View.INVISIBLE);
            if (movieTrailers != null) {
                showMovieTrailerView();
                movieTrailerAdapter.setTrailerDetails(movieTrailers);
                mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
                //mRecyclerViewReview.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutStateReview);
            } else {
                showTrailerErrorMessage();
            }

            trailersLoaded = true;

            if (reviewsLoaded) {
                restoreScrollPoisition();
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<MovieTrailer>> loader) {

        }

    }

    public class MovieReviewLoader implements LoaderManager.LoaderCallbacks<List<MovieReviews>> {
        @NonNull
        @Override
        public Loader<List<MovieReviews>> onCreateLoader(int i, final Bundle args) {
            return new AsyncTaskLoader<List<MovieReviews>>(getApplicationContext()) {

                @Override
                protected void onStartLoading() {
                    super.onStartLoading();
                    if (args == null) {
                        return;
                    }
                    mLoadingIndicatorReview.setVisibility(View.VISIBLE);

                    forceLoad();
                }

                @Nullable
                @Override
                public List<MovieReviews> loadInBackground() {
                    String movieDBURL = args.getString(SEARCH_REVIEWS_EXTRA);

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

                        List<MovieReviews> simpleJsonMovieData = OpenMoviesJsonUtils.getMovieReviewsFromJson(MovieDetailsActivity.this, jsonMovieDBResponse);

                        return simpleJsonMovieData;

                    } catch (Exception e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            };
        }

        @Override
        public void onLoadFinished(@NonNull Loader<List<MovieReviews>> loader, List<MovieReviews> movieReviews) {
            mLoadingIndicatorReview.setVisibility(View.INVISIBLE);
            if (movieReviews != null) {
                if (movieReviews.size() != 0) {
                    showMovieReviewView();
                    movieReviewAdapter.setTrailerDetails(movieReviews);
                    //mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
                    mRecyclerViewReview.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutStateReview);
                } else {
                    showReviewEmptyMessage();
                }
            } else {
                showReviewErrorMessage();
            }

            reviewsLoaded = true;

            if (trailersLoaded) {
                restoreScrollPoisition();
            }
        }

        @Override
        public void onLoaderReset(@NonNull Loader<List<MovieReviews>> loader) {

        }
    }

    private void showMovieTrailerView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplay.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void showTrailerErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerView.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplay.setVisibility(View.VISIBLE);
    }

    private void showMovieReviewView() {
        /* First, make sure the error is invisible */
        mErrorMessageDisplayReview.setVisibility(View.INVISIBLE);
        /* Then, make sure the weather data is visible */
        mRecyclerViewReview.setVisibility(View.VISIBLE);
    }

    private void showReviewErrorMessage() {
        /* First, hide the currently visible data */
        mRecyclerViewReview.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplayReview.setVisibility(View.VISIBLE);
    }

    private void showReviewEmptyMessage() {
        /* First, hide the currently visible data */
        mRecyclerViewReview.setVisibility(View.INVISIBLE);
        /* Then, show the error */
        mErrorMessageDisplayReview.setVisibility(View.VISIBLE);
        mErrorMessageDisplayReview.setText("No reviews are up yet for this movie!");
    }

    private void loadMovieData(final String dataType) {
        String bundleQueryType;
        int loaderValue;
        String query;
        String searchURL;

        if (dataType.equals(SEARCH_TRAILER_EXTRA)) {
            showMovieTrailerView();
            bundleQueryType = SEARCH_TRAILER_EXTRA;
            loaderValue = MOVIE_TRAILERS_LOADER;
            query = moviesTrailer;
        } else {
            bundleQueryType = SEARCH_REVIEWS_EXTRA;
            loaderValue = MOVIE_REVIEWS_LOADER;
            query = moviesReview;
        }

        searchURL = movieDB + movieID + query + movieDBAPIKey;

        Bundle queryBundle = new Bundle();
        queryBundle.putString(bundleQueryType, searchURL);

        LoaderManager loaderManager = getSupportLoaderManager();
        Loader<List<Movie>> movieLoader = loaderManager.getLoader(loaderValue);

        if (movieLoader == null) {
            if (dataType.equals(SEARCH_TRAILER_EXTRA)) {
                loaderManager.initLoader(loaderValue, queryBundle, new MovieTrailerLoader());
            } else {
                loaderManager.initLoader(loaderValue, queryBundle, new MovieReviewLoader());
            }
        } else {
            if (dataType.equals(SEARCH_TRAILER_EXTRA)) {
                loaderManager.restartLoader(loaderValue, queryBundle, new MovieTrailerLoader());
            } else {
                loaderManager.restartLoader(loaderValue, queryBundle, new MovieReviewLoader());
            }
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(BUNDLE_RECYCLER_LAYOUT, mRecyclerView.getLayoutManager().onSaveInstanceState());
        outState.putParcelable(KEY_RECYCLER_STATE, mRecyclerViewReview.getLayoutManager().onSaveInstanceState());
        outState.putIntArray(scrollPosition,
                new int[]{mScrollView.getScrollX(), mScrollView.getScrollY()});
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        scrollPositionArray = savedInstanceState.getIntArray(scrollPosition);
        savedRecyclerLayoutState = savedInstanceState.getParcelable(BUNDLE_RECYCLER_LAYOUT);
        mRecyclerView.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutState);
        savedRecyclerLayoutStateReview = savedInstanceState.getParcelable(KEY_RECYCLER_STATE);
        mRecyclerViewReview.getLayoutManager().onRestoreInstanceState(savedRecyclerLayoutStateReview);

    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void onSaveMovie() {
        final Animation scaleAnimation = new ScaleAnimation(0.7f, 1.0f, 0.7f, 1.0f, Animation.RELATIVE_TO_SELF, 0.7f, Animation.RELATIVE_TO_SELF, 0.7f);
        scaleAnimation.setDuration(500);
        BounceInterpolator bounceInterpolator = new BounceInterpolator();
        scaleAnimation.setInterpolator(bounceInterpolator);
        Log.d("onSaveMovie", "New movie is " + pictureDetails.getTitle());

        mFavMovie.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mFavMovie.isChecked()) {
                    mFavMovie.setTextOn("Remove");
                    mFavMovie.setChecked(true);
                    pictureDetails.setFav(true);
                    mFavMovie.startAnimation(scaleAnimation);
                    Log.d("onSaveMovie", "Added movie is " + pictureDetails.getTitle());
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            final MovieEntry movieEntry = new MovieEntry(pictureDetails.getId(), pictureDetails.getTitle(),
                                    pictureDetails.getSummary(), pictureDetails.getVote(), pictureDetails.getReleaseData(),
                                    pictureDetails.getPoster(), pictureDetails.getFav());
                            mDb.movieDAO().insertMovie(movieEntry);
                        }
                    });

                } else {
                    mFavMovie.setTextOff("Add");
                    mFavMovie.setChecked(false);
                    Log.d("onSaveMovie", "Deleted movie is " + pictureDetails.getTitle());
                    pictureDetails.setFav(false);
                    mFavMovie.startAnimation(scaleAnimation);
                    AppExecutors.getInstance().diskIO().execute(new Runnable() {
                        @Override
                        public void run() {
                            mDb.movieDAO().deleteMovie(mDb.movieDAO().loadMovieById(pictureDetails.getId()));
                        }
                    });
                }
            }

        });
    }
}
