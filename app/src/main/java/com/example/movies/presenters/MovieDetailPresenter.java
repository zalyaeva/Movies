package com.example.movies.presenters;

import com.arellomobile.mvp.InjectViewState;
import com.arellomobile.mvp.MvpPresenter;
import com.example.movies.App;
import com.example.movies.data.Movie;
import com.example.movies.repository.MovieRepository;
import com.example.movies.views.MovieDetailView;

import javax.inject.Inject;

import io.reactivex.Single;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Action;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;

@InjectViewState
public class MovieDetailPresenter extends MvpPresenter<MovieDetailView>{
    @Inject
    MovieRepository repository;

    Disposable disposble;

    public MovieDetailPresenter() {
        App.getInstance().getAppComponent().inject(this);
    }

    public void getMovieDetail(int id){
        disposble = repository.getMovieFromDB(id)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Movie>() {
                    @Override
                    public void accept(Movie movie) throws Exception {
                        getViewState().onMovieDetailLoaded(movie);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.fillInStackTrace();
                    }
                });
    }

    public void getTrailerMovie(int idMovie) {
        disposble = repository.getMovieTrailer(idMovie)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) throws Exception {
                        getViewState().onTrailerMovie(s);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable) throws Exception {
                        throwable.fillInStackTrace();
                    }
                });
    }

    public void addFavoriteMovie(Movie movie){
        repository.addFavoriteToDB(movie);
    }

    public boolean movieIsFavorite(Movie movie){
        return repository.movieIsFavorite(movie);
    }

    public void deleteFavoriteMovie(Movie movie){
        repository.deleteMovieFromDB(movie);
    }

    public Single<Movie> getMovie(int id) {return repository.getMovieFromDB(id);}
}
