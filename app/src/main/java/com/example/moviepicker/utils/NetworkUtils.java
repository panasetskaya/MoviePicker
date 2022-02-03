package com.example.moviepicker.utils;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.loader.content.AsyncTaskLoader;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.ExecutionException;

public class NetworkUtils {

    private static final String BASE_URL = "https://api.themoviedb.org/3/discover/movie?";

    private static final String PARAMS_API_KEY = "api_key";
    private static final String PARAMS_PAGE = "page";
    private static final String PARAMS_SORT = "sort_by";
    private static final String PARAMS_LANGUAGE = "language";
    private static final String PARAMS_MIN_VOTE_COUNT = "vote.count.gte";

    private static final String API_KEY = "b513a81069845a70e812028642a23df1";
    private static final String LANGUAGE_VALUE = "ru-RU";
    private static final String SORT_BY_POPULARITY = "popularity.desc";
    private static final String SORT_BY_TOP_RATED = "vote_count.desc";
    private static final String MIN_VOTE_COUNT_VALUE = "1000";

    public static final int POPULARITY = 0;
    public static final int TOP_RATED = 1;

    private static final String BASE_REVIEWS_URL = "https://api.themoviedb.org/3/movie/%s/reviews?";
    private static final String BASE_VIDEOS_URL = "https://api.themoviedb.org/3/movie/%s/videos?";


    private static URL buildURLForVideos(int id) {
        String base = String.format(BASE_VIDEOS_URL, id);
        URL result = null;
        Uri uri = Uri.parse(base).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY,API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE,LANGUAGE_VALUE)
                .build();
        try {
            return result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    private static URL buildURLForReviews(int id) {
        String base = String.format(BASE_REVIEWS_URL, id);
        URL result = null;
        Uri uri = Uri.parse(base).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY,API_KEY)
                .build();
        try {
            return result = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject getJSONForVideos(int id) {
        URL url = buildURLForVideos(id);
        JSONObject result = null;
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONForReviews(int id) {
        URL url = buildURLForReviews(id);
        JSONObject result = null;
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static URL buildURL(int sortBy, int page) {
        String sortingMethod;
        URL result = null;
        if (sortBy==POPULARITY) {
            sortingMethod = SORT_BY_POPULARITY;
        } else {
            sortingMethod = SORT_BY_TOP_RATED;
        }
        String pageStr = Integer.toString(page);
        Uri uri = Uri.parse(BASE_URL).buildUpon()
                .appendQueryParameter(PARAMS_API_KEY,API_KEY)
                .appendQueryParameter(PARAMS_LANGUAGE,LANGUAGE_VALUE)
                .appendQueryParameter(PARAMS_SORT,sortingMethod)
                .appendQueryParameter(PARAMS_MIN_VOTE_COUNT,MIN_VOTE_COUNT_VALUE)
                .appendQueryParameter(PARAMS_PAGE,pageStr)
                .build();
        try {
            result = new URL(uri.toString());
        } catch (MalformedURLException e){
            e.printStackTrace();
        }
        return result;
    }

    public static JSONObject getJSONFromNetwork(int sortBy, int page) {
        URL url = buildURL(sortBy, page);
        JSONObject result = null;
        try {
            result = new JSONLoadTask().execute(url).get();
        } catch (ExecutionException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return result;
    }

    public static URL buildSearchUrl(String query) {
        URL url = null;
        try {
            url = new URL("https://api.themoviedb.org/3/search/movie?api_key=b513a81069845a70e812028642a23df1&query="+query);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static class JSONLoader extends AsyncTaskLoader<JSONObject> {

        private Bundle bundle;
        private OnStartLoadingListener onStartLoadingListener;

        public void setOnStartLoadingListener(OnStartLoadingListener onStartLoadingListener) {
            this.onStartLoadingListener = onStartLoadingListener;
        }

        public interface OnStartLoadingListener {
            void onStartLoading();
        }

        public JSONLoader(@NonNull Context context, Bundle bundle) {
            super(context);
            this.bundle = bundle;
        }

        @Override
        protected void onStartLoading() {
            super.onStartLoading();
            if (onStartLoadingListener!=null) {
                onStartLoadingListener.onStartLoading();
            }
            forceLoad();
        }

        @Nullable
        @Override
        public JSONObject loadInBackground() {
            if (bundle==null) {
                return null;
            }
            String urlAsString = bundle.getString("url");
            URL url = null;
            try {
                url = new URL(urlAsString);
            } catch (MalformedURLException e) {
                e.printStackTrace();
            }
            JSONObject result = null;
            if (url == null) {
                return null;
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) url.openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line!=null) {
                    builder.append(line);
                    line = bufferedReader.readLine();
                }
                inputStream.close();
                result = new JSONObject(builder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection!=null) {
                    connection.disconnect();
                }
            }
            return result;
        }
    }

    private static class JSONLoadTask extends AsyncTask<URL, Void, JSONObject> {

        @Override
        protected JSONObject doInBackground(URL... urls) {
            JSONObject result = null;
            if (urls == null || urls.length == 0) {
                return result;
            }
            HttpURLConnection connection = null;
            try {
                connection = (HttpURLConnection) urls[0].openConnection();
                InputStream inputStream = connection.getInputStream();
                InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
                BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
                StringBuilder builder = new StringBuilder();
                String line = bufferedReader.readLine();
                while (line!=null) {
                    builder.append(line);
                    line = bufferedReader.readLine();
                }
                inputStream.close();
                result = new JSONObject(builder.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            } finally {
                if (connection!=null) {
                    connection.disconnect();
                }
            }
            return result;
        }

    }

}
