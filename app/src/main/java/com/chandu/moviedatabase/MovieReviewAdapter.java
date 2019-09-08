package com.chandu.moviedatabase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class MovieReviewAdapter extends RecyclerView.Adapter<MovieReviewAdapter.MovieReviewAdapterViewHolder> {
    private List<MovieReviews> movieReviews;

    @NonNull
    @Override
    public MovieReviewAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_movie_reviews;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MovieReviewAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieReviewAdapterViewHolder movieReviewAdapterViewHolder, int Position) {
        if (movieReviews != null) {
            String reviewSummary = movieReviews.get(Position).getName() + ": " +
                    movieReviews.get(Position).getContent();

            movieReviewAdapterViewHolder.movieReview.setText(reviewSummary);
        } else {
            String reviewSummary = "No reviews are up yet for this movie!";
            movieReviewAdapterViewHolder.movieReview.setText(reviewSummary);
        }
    }

    @Override
    public int getItemCount() {
        if (movieReviews == null) {
            return 0;
        } else {
            return movieReviews.size();
        }
    }

    public class MovieReviewAdapterViewHolder extends RecyclerView.ViewHolder {
        public final TextView movieReview;

        public MovieReviewAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            movieReview = (TextView) itemView.findViewById(R.id.movie_reviews);
        }
    }

    public void setTrailerDetails(List<MovieReviews> mMovieDetails) {
        movieReviews = mMovieDetails;
        notifyDataSetChanged();
    }
}
