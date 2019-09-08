package com.chandu.moviedatabase.Database;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import androidx.room.Update;

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
