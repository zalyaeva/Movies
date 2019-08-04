package com.example.movies.ui.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.arellomobile.mvp.MvpAppCompatFragment;
import com.example.movies.R;
import com.example.movies.data.Movie;
import com.example.movies.db.AppDatabase;
import com.example.movies.presenters.MoviePresenter;
import com.example.movies.viewmodel.AppExecutors;
import com.squareup.picasso.Picasso;

import org.parceler.Parcels;

public class MovieDetailFragment extends MvpAppCompatFragment {

    private static final String MOVIES_KEY = "MOVIES";
    private AppDatabase appDatabase;
    static MoviePresenter moviePresenter;

    public static MovieDetailFragment create(Movie movie, MoviePresenter presenter) {
        Bundle args = new Bundle();
        args.putParcelable(MOVIES_KEY, Parcels.wrap(movie));
        MovieDetailFragment fragment = new MovieDetailFragment();
        fragment.setArguments(args);
        moviePresenter = presenter;
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_movie, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();

        TextView titleMovie, descriptionMovie, ratingMovie, realiseDateMovie;
        ImageView posterMovie;

        appDatabase = AppDatabase.getInstance(getContext());

        ImageButton shareButton = getView().findViewById(R.id.btShare);
        shareButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setType("text/plain");
                String shareBody = "Обязательно посмотрю этот фильм";
                String shareSub = "Ого!";
                intent.putExtra(Intent.EXTRA_SUBJECT, shareSub);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, "Поделиться"));
            }
        });

        final Movie movie = Parcels.unwrap(getArguments().getParcelable("MOVIES"));

        final ToggleButton btnFavourites = getView().findViewById(R.id.btFavorites);

        moviePresenter.loadFavMovie(movie);
        if(movie.isFavorite()){
            btnFavourites.setChecked(true);
        }

        btnFavourites.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    addFavorite(movie);
                    Snackbar.make(buttonView, "Added to favorite", Snackbar.LENGTH_SHORT).show();
                } else {
                    deleteMovie(movie);
                    Snackbar.make(buttonView, "Removed from favorite", Snackbar.LENGTH_SHORT).show();
                }
            }
        });

        posterMovie = getActivity().findViewById(R.id.ivMoviePoster);
        titleMovie = getActivity().findViewById(R.id.tvTitle);
        descriptionMovie = getActivity().findViewById(R.id.tvOverview);
        ratingMovie = getActivity().findViewById(R.id.tvVoteAverage);
        realiseDateMovie = getActivity().findViewById(R.id.tvReleaseDate);

        titleMovie.setText(movie.getTitle());
        descriptionMovie.setText(movie.getOverview());
        ratingMovie.setText(String.valueOf(movie.getVoteAverage()));
        realiseDateMovie.setText(movie.getReleaseDate());

        Picasso.get()
                .load(movie.getFullImageUrl())
                .into(posterMovie);
    }

    public void saveMovie(final Movie movie) {
        AppExecutors.getInstance().diskIO().execute(new Runnable() {
            @Override
            public void run() {
                appDatabase.movieDao().insertMovie(movie);
            }
        });
    }

    public void addFavorite(final Movie movie) {
        movie.isFavorite(true);
        saveMovie(movie);
        appDatabase.movieDao().update(movie);
    }

    private void deleteMovie(final Movie movie) {
        movie.isFavorite(false);
        appDatabase.movieDao().deleteMovie(movie);
        appDatabase.movieDao().update(movie);
    }
}