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

public class MovieTrailerAdapter extends RecyclerView.Adapter<MovieTrailerAdapter.MovieTrailerAdapterViewHolder> {
    private List<MovieTrailer> movieTrailers;
    private final MovieTrailerAdapterOnClickHandler mClickHandler;
    public static final String youtubeLink = "http://img.youtube.com/vi/";

    public interface MovieTrailerAdapterOnClickHandler {
        void onClick(MovieTrailer movieTrailer);
    }

    public MovieTrailerAdapter(MovieTrailerAdapterOnClickHandler clickHandler) {
        mClickHandler = clickHandler;
    }

    @NonNull
    @Override
    public MovieTrailerAdapterViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        Context context = viewGroup.getContext();
        int layoutIdForListItem = R.layout.activity_movie_trailers;
        LayoutInflater inflater = LayoutInflater.from(context);

        View view = inflater.inflate(layoutIdForListItem, viewGroup, false);
        return new MovieTrailerAdapterViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MovieTrailerAdapterViewHolder movieTrailerAdapterViewHolder,
                                 int position) {
        String img_url = youtubeLink + movieTrailers.get(position).getKey() + "/0.jpg";
        Picasso.get().load(img_url).into(movieTrailerAdapterViewHolder.trailerView);
    }

    @Override
    public int getItemCount() {
        return (movieTrailers == null) ? 0:movieTrailers.size();
    }

    public class MovieTrailerAdapterViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public final ImageView trailerView;
        public MovieTrailerAdapterViewHolder(@NonNull View itemView) {
            super(itemView);
            trailerView = (ImageView)  itemView.findViewById(R.id.img_thumnail);

            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View view) {
            int adapterPosition = getAdapterPosition();
            MovieTrailer pictureDetails = movieTrailers.get(adapterPosition);
            mClickHandler.onClick(pictureDetails);
        }
    }

    public void setTrailerDetails(List<MovieTrailer> mMovieDetails) {
        movieTrailers = mMovieDetails;
        notifyDataSetChanged();
    }
}
