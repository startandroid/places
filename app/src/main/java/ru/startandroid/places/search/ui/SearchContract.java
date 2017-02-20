package ru.startandroid.places.search.ui;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

import ru.startandroid.places.base.ui.MvpPresenter;
import ru.startandroid.places.base.ui.MvpView;
import ru.startandroid.places.utils.MapHelper;

public interface SearchContract {

    interface View extends MvpView {
        void showLocationProgress();
        void hideLocationProgress();
        void showLocationInfo(String text);
        void showLocationSettingsDialog(Status status);
        void showResolutions(ConnectionResult connectionResult);

        void enableSearch(boolean enable);
        void showSearchLoading();
        void hideSearchLoading();

        void updateMap(MapHelper mapHelper);

        void showErrorMessage(String message);
        void showErrorMessage(int messageId);
    }

    interface Presenter extends MvpPresenter<View> {
        void fillView();
        void onViewStart();
        void onViewResume();
        void onViewPause();
        void onViewStop();
        void onSearchClick(String query);
        void mapIsReady();
    }

}
