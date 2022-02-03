package com.example.moviepicker.data;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;

import java.util.List;

@Dao
public abstract class MovieDAO {

    @Query("SELECT * FROM movies")
    abstract LiveData<List<Movie>> getAllMovies();

    @Query("SELECT * FROM favourite_movies")
    abstract LiveData<List<FavouriteMovie>> getAllFavouriteMovies();


    @Query("SELECT * FROM movies WHERE id==:movieId")
    abstract Movie getMovieById(int movieId);

    @Query("SELECT * FROM favourite_movies WHERE id==:movieId")
    abstract FavouriteMovie getFavouriteMovieById(int movieId);


    @Query("DELETE FROM movies")
    abstract void deleteAllMovies();

    @Query("DELETE FROM favourite_movies")
    abstract void deleteAllFavouriteMovies();

    @Insert
    abstract void insertMovie(Movie movie);

    @Delete
    abstract void deleteMovie(Movie movie);

    @Insert
    abstract void insertFavouriteMovie(FavouriteMovie movie);

    @Delete
    abstract void deleteFavouriteMovie(FavouriteMovie movie);

}
