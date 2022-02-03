package com.example.moviepicker.utils;

import com.example.moviepicker.data.Movie;
import com.example.moviepicker.data.Review;
import com.example.moviepicker.data.Trailer;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class JSONUtils {

    private static final String KEY_RESULTS = "results";
    // для отзывов
    private static final String KEY_AUTHOR = "author";
    private static final String KEY_CONTENT = "content";
    // для видео
    private static final String KEY_VIDEO_KEY = "key";
    private static final String KEY_VIDEO_NAME = "name";
    private static final String BASE_YOUTUBE_URL = "https://youtube.com/watch?v=";
    // вся информация о фильме
    private static final String KEY_VOTE_COUNT = "vote_count";
    private static final String KEY_TITLE = "title";
    private static final String KEY_ID = "id";
    private static final String KEY_ORIGINAL_TITLE = "original_title";
    private static final String KEY_OVERVIEW = "overview";
    private static final String KEY_POSTER_PATH = "poster_path";
    private static final String KEY_BACKDROP_PATH = "backdrop_path";
    private static final String KEY_VOTE_AVERAGE = "vote_average";
    private static final String KEY_RELEASE_DATE = "release_date";
    private static final String BASE_POSTER_URL = "https://image.tmdb.org/t/p/";
    public static final String SMALL_POSTER_SIZE = "w185";
    public static final String BIG_POSTER_SIZE = "w780";

    public static ArrayList<Review> getReviewsFromJSON(JSONObject jsonObject) {
        ArrayList<Review> result = new ArrayList<>();
        if (jsonObject==null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectReview = jsonArray.getJSONObject(i);
                String author = jsonObjectReview.getString(KEY_AUTHOR);
                String content = jsonObjectReview.getString(KEY_CONTENT);
                Review review = new Review(author, content);
                result.add(review);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Trailer> getVideosFromJSON(JSONObject jsonObject) {
        ArrayList<Trailer> result = new ArrayList<>();
        if (jsonObject==null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObjectReview = jsonArray.getJSONObject(i);
                String key = BASE_YOUTUBE_URL + jsonObjectReview.getString(KEY_VIDEO_KEY);
                String name = jsonObjectReview.getString(KEY_VIDEO_NAME);
                Trailer trailer = new Trailer(key,name);
                result.add(trailer);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static ArrayList<Movie> getMoviesFromJSON(JSONObject jsonObject) {
        ArrayList<Movie> result = new ArrayList<>();
        if (jsonObject==null) {
            return result;
        }
        try {
            JSONArray jsonArray = jsonObject.getJSONArray(KEY_RESULTS);
            for (int i = 0; i <jsonArray.length();i++) {
                JSONObject jsonObjectMovie = jsonArray.getJSONObject(i);
                int id = jsonObjectMovie.getInt(KEY_ID);
                int vote_count = jsonObjectMovie.getInt(KEY_VOTE_COUNT);
                String title = jsonObjectMovie.getString(KEY_TITLE);
                String originalTitle = jsonObjectMovie.getString(KEY_ORIGINAL_TITLE);
                String overview = jsonObjectMovie.getString(KEY_OVERVIEW);
                String posterPath = BASE_POSTER_URL + SMALL_POSTER_SIZE + jsonObjectMovie.getString(KEY_POSTER_PATH);
                String bigPosterPath = BASE_POSTER_URL + BIG_POSTER_SIZE + jsonObjectMovie.getString(KEY_POSTER_PATH);
                String backdropPath = jsonObjectMovie.getString(KEY_BACKDROP_PATH);
                double voteAverage = jsonObjectMovie.getDouble(KEY_VOTE_AVERAGE);
                String releaseDate = jsonObjectMovie.getString(KEY_RELEASE_DATE);
                Movie movie = new Movie(id,vote_count,title,originalTitle,overview,posterPath, bigPosterPath, backdropPath,voteAverage,releaseDate);
                result.add(movie);
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return result;
    }
}
