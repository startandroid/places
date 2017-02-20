package ru.startandroid.places.search.ui;

import android.graphics.Bitmap;
import android.location.Location;
import android.text.TextUtils;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import ru.startandroid.places.R;
import ru.startandroid.places.base.Constants;
import ru.startandroid.places.base.ui.PresenterBase;
import ru.startandroid.places.events.EventBus;
import ru.startandroid.places.events.Events;
import ru.startandroid.places.utils.MapHelper;
import ru.startandroid.places.utils.PicassoWrapper;
import ru.startandroid.places.utils.location.LocationHelper;
import ru.startandroid.places.utils.location.LocationHelperCallback;
import ru.startandroid.places.web.ApiService;
import ru.startandroid.places.web.data.Item;
import ru.startandroid.places.web.data.ItemBitmapWrapper;
import ru.startandroid.places.web.data.SearchResponse;
import rx.Observer;
import rx.Scheduler;
import rx.Subscription;
import rx.functions.Func1;

public class SearchPresenter extends PresenterBase<SearchContract.View> implements SearchContract.Presenter {

    private final ApiService apiService;
    private final LocationHelper locationHelper;

    private final State state;
    private final Func1<SearchResponse, Collection<ItemBitmapWrapper>> wrapFunction;
    private final Scheduler ioScheduler;
    private final Scheduler mainScheduler;
    private final EventBus eventBus;

    private Location currentLocation;
    private Subscription searchSubscription;

    public SearchPresenter(ApiService apiService, LocationHelper locationHelper, final PicassoWrapper picassoWrapper,
                           Scheduler ioScheduler, Scheduler mainScheduler, EventBus eventBus) {
        this.apiService = apiService;
        this.locationHelper = locationHelper;
        this.ioScheduler = ioScheduler;
        this.mainScheduler = mainScheduler;
        this.eventBus = eventBus;

        state = new State();
        wrapFunction = new Func1<SearchResponse, Collection<ItemBitmapWrapper>>() {
            @Override
            public Collection<ItemBitmapWrapper> call(SearchResponse searchResponse) {
                Set<ItemBitmapWrapper> items = new HashSet<ItemBitmapWrapper>();
                for (Item item : searchResponse.getResults().getItems()) {
                    Bitmap bitmap = null;
                    if (!TextUtils.isEmpty(item.getIcon())) {
                        bitmap = picassoWrapper.loadBitmap(item.getIcon());
                    }
                    items.add(new ItemBitmapWrapper(item, bitmap));

                }
                return items;
            }
        };
        locationHelper.setCallback(new LocationHelperCallback() {
            @Override
            public void onRequestLocation() {
                if (currentLocation == null) {
                    state.setLocation(true, "");
                }
            }

            @Override
            public void onChangeLocation(Location location) {
                setCurrentLocation(location);
            }

            @Override
            public void onFailed(String message) {
                state.setLocation(false, message);
            }

            @Override
            public void changeSettings(Status status) {
                if (isViewAttached()) {
                    getView().showLocationSettingsDialog(status);
                }
            }

            @Override
            public void showResolutions(ConnectionResult connectionResult) {
                if (isViewAttached()) {
                    getView().showResolutions(connectionResult);
                }
            }
        });

    }

    @Override
    public void fillView() {
        if (!isViewAttached()) {
            return;
        }
        state.updateUI();
    }

    @Override
    public void onViewStart() {
        locationHelper.start();
    }

    @Override
    public void onViewResume() {
        locationHelper.resume();
    }

    @Override
    public void onViewPause() {
        locationHelper.pause();
    }

    @Override
    public void onViewStop() {
        locationHelper.stop();
    }

    @Override
    public void onSearchClick(String query) {
        if (currentLocation == null) {
            showErrorMessage(R.string.message_location_is_unknown);
            return;
        }
        if ( TextUtils.isEmpty(query)) {
            showErrorMessage(R.string.message_query_is_empty);
            return;
        }

        if (isSearchLoading()) {
            return;
        }
        state.setSearchInProgress(true);
        removeSubscription(searchSubscription);


        System.out.println("qweee onSearchClick start " + query);
        searchSubscription = apiService.search(
                String.format("%s,%s", currentLocation.getLatitude(), currentLocation.getLongitude()),
                query, Constants.QUERY_SIZE)
                .subscribeOn(ioScheduler)
                .map(wrapFunction)
                .observeOn(mainScheduler)
                .subscribe(new Observer<Collection<ItemBitmapWrapper>>() {
                    @Override
                    public void onCompleted() {
                    }

                    @Override
                    public void onError(Throwable e) {
                        eventBus.postEvent(Events.finishSearch());
                        state.setSearchInProgress(false);
                        e.printStackTrace();
                        showErrorMessage(e.getMessage());

                    }

                    @Override
                    public void onNext(Collection<ItemBitmapWrapper> items) {
                        eventBus.postEvent(Events.finishSearch());
                        System.out.println("qweee onSearchClick next ");
                        state.setSearchInProgress(false);
                        state.setMapItems(items);
                    }
                });
        addSubscription(searchSubscription);
        eventBus.postEvent(Events.startSearch());
    }

    @Override
    public void mapIsReady() {
        state.setForceUpdateMap();
        state.updateUiMap();
    }

    boolean isSearchLoading() {
        return searchSubscription != null && !searchSubscription.isUnsubscribed();
    }

    public void setCurrentLocation(Location location) {
        if (location == null) {
            return;
        }
        currentLocation = location;

        state.setLocation(false,
                String.format("%s, %s", currentLocation.getLatitude(), currentLocation.getLongitude()));
        state.enableSearch(true);
        state.setMapCurrentLocation(currentLocation);
    }

    private void showErrorMessage(int messageId) {
        if (isViewAttached()) {
            getView().showErrorMessage(messageId);
        }
    }

    private void showErrorMessage(String message) {
        if (isViewAttached()) {
            getView().showErrorMessage(message);
        }
    }


    public class State {
        private final MapHelper mapHelper = new MapHelper();
        private boolean locationInProgress = false;
        private String locationInfo = "";
        private boolean searchIsEnabled = false;
        private boolean searchInProgress = false;

        // location
        void setLocation(boolean progress, String info) {
            locationInProgress = progress;
            locationInfo = info;
            updateUiLocation();
        }


        void updateUiLocation() {
            if (!isViewAttached()) {
                return;
            }
            if (locationInProgress) {
                getView().showLocationProgress();
            } else {
                getView().hideLocationProgress();
            }
            getView().showLocationInfo(locationInfo);
        }


        //search

        void enableSearch(boolean enable) {
            searchIsEnabled = enable;
            updateUiSearchEnable();
        }

        void updateUiSearchEnable() {
            if (!isViewAttached()) {
                return;
            }

            getView().enableSearch(searchIsEnabled);
        }

        void setSearchInProgress(boolean inProgress) {
            searchInProgress = inProgress;
            updateUiSearchInProgress();
        }

        private void updateUiSearchInProgress() {
            if (!isViewAttached()) {
                return;
            }

            if (searchInProgress) {
                getView().showSearchLoading();
            } else {
                getView().hideSearchLoading();
            }
            System.out.println("qweee updateUiSearchInProgress searchInProgress " + searchInProgress);
        }


        // map
        void setMapItems(Collection<ItemBitmapWrapper> items) {
            mapHelper.setItems(items);
            updateUiMap();
        }

        void setMapCurrentLocation(Location location) {
            mapHelper.setCurrentLocation(location);
            updateUiMap();
        }

        void setForceUpdateMap() {
            mapHelper.forceUpdate();
        }


        public void updateUiMap() {
            if (!isViewAttached()) {
                return;
            }
            getView().updateMap(mapHelper);
        }


        public void updateUI() {
            updateUiLocation();
            updateUiSearchInProgress();
            updateUiSearchEnable();
        }

    }


}
