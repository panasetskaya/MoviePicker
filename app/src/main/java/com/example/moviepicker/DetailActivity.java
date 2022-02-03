package com.example.moviepicker;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.moviepicker.adapters.ReviewAdapter;
import com.example.moviepicker.adapters.TrailerAdapter;
import com.example.moviepicker.data.FavouriteMovie;
import com.example.moviepicker.data.MainViewModel;
import com.example.moviepicker.data.Movie;
import com.example.moviepicker.data.MovieDatabase;
import com.example.moviepicker.data.Review;
import com.example.moviepicker.data.Trailer;
import com.example.moviepicker.utils.JSONUtils;
import com.example.moviepicker.utils.NetworkUtils;
import com.squareup.picasso.Picasso;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Locale;

public class DetailActivity extends AppCompatActivity {

    private ImageView imageViewBigPoster;
    private TextView textViewTitle;
    private TextView textViewOverview;
    private TextView textViewOriginalTitle;
    private TextView textViewRating;
    private TextView textViewReleaseDate;
    private MainViewModel mainViewModel;
    private ImageView starred;
    private ImageView unstarred;
    private Movie movie;
    private int id;
    private RecyclerView recyclerViewTrailers;
    private RecyclerView recyclerViewReviews;
    private ReviewAdapter reviewAdapter;
    private TrailerAdapter trailerAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        starred = findViewById(R.id.imageViewStarred);
        unstarred = findViewById(R.id.imageViewNotStarred);
        imageViewBigPoster = findViewById(R.id.imageViewBigPoster);
        textViewTitle = findViewById(R.id.textViewTitle);
        textViewOverview = findViewById(R.id.textViewDescription);
        textViewOriginalTitle = findViewById(R.id.textViewOriginalTitle);
        textViewRating = findViewById(R.id.textViewMovieRating);
        textViewReleaseDate = findViewById(R.id.textViewReleaseDate);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("id")) {
            id = intent.getIntExtra("id", -1);
        }
        movie = mainViewModel.getMovieByID(id);
        if (movie==null) {
            movie = mainViewModel.getFavouriteMovieByID(id);
        }
        if (movie==null) {
            Bundle arguments = getIntent().getExtras();
            if (arguments!=null) {
                movie = (Movie) arguments.getSerializable("movie");
            }
        }
        textViewTitle.setText(movie.getTitle().toUpperCase(Locale.ROOT));
        textViewOverview.setText(movie.getOverview());
        textViewOriginalTitle.setText(movie.getOriginalTitle());
        String rating = String.valueOf(movie.getVoteAverage());
        textViewRating.setText(rating);
        textViewReleaseDate.setText(movie.getReleaseDate());
        Picasso.get().load(movie.getBigPosterPath()).into(imageViewBigPoster);
        if (isFavourite()) {
            starred.setVisibility(View.VISIBLE);
            unstarred.setVisibility(View.INVISIBLE);
        } else {
            starred.setVisibility(View.INVISIBLE);
            unstarred.setVisibility(View.VISIBLE);
        }
        recyclerViewTrailers = findViewById(R.id.recyclerViewTrailers);
        recyclerViewReviews = findViewById(R.id.recyclerViewReviews);
        reviewAdapter = new ReviewAdapter();
        trailerAdapter = new TrailerAdapter();
        trailerAdapter.setOnTrailerClickListener(new TrailerAdapter.OnTrailerClickListener() {
            @Override
            public void onTrailerClick(String url) {
                Intent intentToTrailer = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                startActivity(intentToTrailer);
            }
        });
        recyclerViewReviews.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewTrailers.setAdapter(trailerAdapter);
        recyclerViewReviews.setAdapter(reviewAdapter);
        JSONObject jsonObjectTrailers = NetworkUtils.getJSONForVideos(movie.getId());
        JSONObject jsonObjectReviews = NetworkUtils.getJSONForReviews(movie.getId());
        ArrayList<Trailer> trailers = JSONUtils.getVideosFromJSON(jsonObjectTrailers);
        ArrayList<Review> reviews = JSONUtils.getReviewsFromJSON(jsonObjectReviews);
        reviewAdapter.setReviews(reviews);
        trailerAdapter.setTrailers(trailers);

    }

    public void onClickChangeFavourite(View view) {
        if (isFavourite()) {
            mainViewModel.deleteFavouriteMovie(mainViewModel.getFavouriteMovieByID(id));
            starred.setVisibility(View.INVISIBLE);
            unstarred.setVisibility(View.VISIBLE);
            Toast.makeText(getApplicationContext(), R.string.deleted_from_favourites, Toast.LENGTH_SHORT).show();
        } else {
            mainViewModel.insertFavouriteMovie(new FavouriteMovie(movie));
            starred.setVisibility(View.VISIBLE);
            unstarred.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), getString(R.string.added_to_favourites), Toast.LENGTH_SHORT).show();
        }
    }

    private Boolean isFavourite() {
        FavouriteMovie favouriteMovie = mainViewModel.getFavouriteMovieByID(id);
        if (favouriteMovie == null) {
            return false;
        } else {
            return true;
        }
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
}