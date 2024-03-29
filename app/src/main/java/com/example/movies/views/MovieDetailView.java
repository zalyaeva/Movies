package com.example.movies.views;

import com.arellomobile.mvp.MvpView;
import com.arellomobile.mvp.viewstate.strategy.OneExecutionStateStrategy;
import com.arellomobile.mvp.viewstate.strategy.StateStrategyType;
import com.example.movies.data.Movie;

public interface MovieDetailView extends BaseView {
    @StateStrategyType(OneExecutionStateStrategy.class)
    void onMovieDetailLoaded(Movie movie);
    @StateStrategyType(OneExecutionStateStrategy.class)
    void onTrailerMovie(String movieId);
}
