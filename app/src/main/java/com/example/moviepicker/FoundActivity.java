package com.example.moviepicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.SearchView;
import android.widget.Toast;

import com.example.moviepicker.adapters.MovieAdapter;
import com.example.moviepicker.data.Movie;
import com.example.moviepicker.utils.JSONUtils;
import com.example.moviepicker.utils.NetworkUtils;

import org.json.JSONObject;

import java.util.ArrayList;

public class FoundActivity extends AppCompatActivity {

    private String query;
    private RecyclerView recyclerViewFound;
    private MovieAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_found);
        handleIntent(getIntent());
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
        JSONObject jsonObject = NetworkUtils.getJSONBySearchQuery(userQuery);
        ArrayList<Movie> movies = JSONUtils.getMoviesFromJSON(jsonObject);
        if (movies!=null && !movies.isEmpty()) {
            adapter.addMovies(movies);
        } else {
            unsuccessfulShow();
        }
    }

    private void handleIntent(Intent intent) {
        recyclerViewFound = findViewById(R.id.recyclerViewFoundMovies);
        adapter = new MovieAdapter();
        recyclerViewFound.setLayoutManager(new GridLayoutManager(FoundActivity.this,2));
        recyclerViewFound.setAdapter(adapter);
        adapter.setOnPosterClickListener(new MovieAdapter.OnPosterClickListener() {
            @Override
            public void onPosterClick(int position) {
                Movie thisMovie = adapter.getMovies().get(position);
                Intent intent1 = new Intent(FoundActivity.this, DetailActivity.class);
                intent1.putExtra("movie",thisMovie);
                startActivity(intent1);
            }
        });
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            query = intent.getStringExtra(SearchManager.QUERY);
            query.trim().replace(" ","+");
            downloadSearched(query);
        } else {
            unsuccessfulShow();
        }

    }
}