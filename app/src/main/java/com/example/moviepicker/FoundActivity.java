package com.example.moviepicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.Loader;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.moviepicker.adapters.MovieAdapter;
import com.example.moviepicker.data.Movie;
import com.example.moviepicker.utils.JSONUtils;
import com.example.moviepicker.utils.NetworkUtils;

import org.json.JSONObject;

import java.net.URL;
import java.util.ArrayList;

public class FoundActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<JSONObject>{

    private String query;
    private RecyclerView recyclerViewFound;
    private MovieAdapter adapter;
    private static final int loaderId = 134;
    private LoaderManager loaderManager;
    private ProgressBar progressBarLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);
        loaderManager = LoaderManager.getInstance(this);
        progressBarLoad = findViewById(R.id.progressBarLoadingSearch);
        recyclerViewFound = findViewById(R.id.recyclerViewFoundMovies);
        adapter = new MovieAdapter();
        recyclerViewFound.setLayoutManager(new GridLayoutManager(FoundActivity.this,2));
        recyclerViewFound.setAdapter(adapter);
        handleIntent(getIntent());
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie thisMovie = adapter.getMovies().get(position);
                Intent intent1 = new Intent(FoundActivity.this, DetailActivity.class);
                intent1.putExtra("movie",thisMovie);
                startActivity(intent1);
            }
        });
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        handleIntent(intent);
    }

    private void unsuccessfulShow() {
        Toast.makeText(this,"Такой фильм не находится:(", Toast.LENGTH_LONG).show();
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.itemSearch).getActionView();
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));
        searchView.setIconifiedByDefault(true);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.itemMain:
                Intent intent = new Intent(this, MainActivity.class);
                startActivity(intent);
                break;
            case R.id.itemMyFavourites:
                Intent intent1 = new Intent(this, FavouriteActivity.class);
                startActivity(intent1);
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void downloadSearched(String userQuery) {
        URL url = NetworkUtils.buildSearchUrl(userQuery);
        Bundle bundle = new Bundle();
        bundle.putString("url", url.toString());
        loaderManager.restartLoader(loaderId,bundle,this);
    }

    private void handleIntent(Intent intent) {
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            query.trim().replace(" ","+");
            downloadSearched(query);
        } else {
            unsuccessfulShow();
        }

    }

    @NonNull
    @Override
    public Loader<JSONObject> onCreateLoader(int id, @Nullable Bundle args) {
        NetworkUtils.JSONLoader jsonLoader = new NetworkUtils.JSONLoader(this, args);
        jsonLoader.setOnStartLoadingListener(new NetworkUtils.JSONLoader.OnStartLoadingListener() {
            @Override
            public void onStartLoading() {
                progressBarLoad.setVisibility(View.VISIBLE);
            }
        });
        return jsonLoader;
    }

    @Override
    public void onLoadFinished(@NonNull Loader<JSONObject> loader, JSONObject data) {
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(data);
        if (movies!=null && !movies.isEmpty()) {
            adapter.clear();
            adapter.addMovies(movies);
        } else {
            unsuccessfulShow();
        }
        progressBarLoad.setVisibility(View.INVISIBLE);
        loaderManager.destroyLoader(loaderId);

    }

    @Override
    public void onLoaderReset(@NonNull Loader<JSONObject> loader) {

    }
}