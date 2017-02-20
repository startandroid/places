package ru.startandroid.places.base.ui;

public interface MvpPresenter<V extends MvpView> {

    void attachView(V mvpView);

    void detachView();

    void destroy();

}
