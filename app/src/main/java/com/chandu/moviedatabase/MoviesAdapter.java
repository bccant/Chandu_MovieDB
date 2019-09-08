package com.chandu.moviedatabase;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class MoviesAdapter extends RecyclerView.Adapter<MoviesAdapter.MoviesAdapterViewHolder> {
    private List<Movie> movieDetails;

    private final MoviesAdapterOnClickHandler mClickHandler;

    public interface  MoviesAdapterOnClickHandler {
        void onClick(Movie movieDetails);
    }

    public MoviesAdapter(MoviesAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MoviesAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_movies;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);

        return new MoviesAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MoviesAdapterViewHolder moviesAdapterViewHolder, int position) {
        Movie movieSummary = movieDetails.get(position);
        String posterURL = "http://image.tmdb.org/t/p/w185/" +
                movieSummary.getPoster();
        Picasso.get().load(posterURL).into(moviesAdapterViewHolder.moviePoster);
    }

    @Override
    public int getItemCount() {
        return (movieDetails == null) ? 0: movieDetails.size();
    }

    public class MoviesAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView moviePoster;

        public MoviesAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            moviePoster = (ImageView) itemView.findViewById(R.id.movie_names_data);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            Movie pictureDetails = movieDetails.get(adapterPosition);
            mClickHandler.onClick(pictureDetails);
        }
    }

    public void setMovieDetails(List<Movie> mMovieDetails) {
        movieDetails = mMovieDetails;
        notifyDataSetChanged();
    }

    public void setMovieDetailsFromDB(List<Movie> mMovieDetails) {
        movieDetails = mMovieDetails;
        notifyDataSetChanged();
    }
}
