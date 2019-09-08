package com.chandu.moviedatabase.Database;

import android.arch.lifecycle.LiveData;
import android.arch.persistence.room.Dao;
import android.arch.persistence.room.Delete;
import android.arch.persistence.room.Insert;
import android.arch.persistence.room.OnConflictStrategy;
import android.arch.persistence.room.Query;
import android.arch.persistence.room.Update;

import java.util.List;

@Dao
public interface MovieDAO {

    @Query("SELECT * FROM cinema ORDER by movieRating")
    LiveData<List<MovieEntry>> loadAllTasks();

    @Insert()
    void insertMovie(MovieEntry movieEntry);

    @Update(onConflict = OnConflictStrategy.REPLACE)
    void updateMovie(MovieEntry movieEntry);

    @Delete
    void deleteMovie(MovieEntry movieEntry);

    @Query("SELECT * FROM cinema where movieID = :id")
    MovieEntry loadMovieById(String id);

    @Query("SELECT * FROM cinema where movieID = :id")
    LiveData<MovieEntry> loadFavMovieById(String id);
}
