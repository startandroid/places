package ru.startandroid.places.search.ui;

import android.location.Location;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.robolectric.RobolectricGradleTestRunner;
import org.robolectric.annotation.Config;

import ru.startandroid.places.BuildConfig;
import ru.startandroid.places.R;
import ru.startandroid.places.events.EventBus;
import ru.startandroid.places.utils.Generator;
import ru.startandroid.places.utils.MapHelper;
import ru.startandroid.places.utils.PicassoWrapper;
import ru.startandroid.places.utils.location.LocationHelper;
import ru.startandroid.places.web.ApiService;
import rx.Observable;
import rx.schedulers.Schedulers;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


@RunWith(RobolectricGradleTestRunner.class)
@Config(constants = BuildConfig.class, sdk = 21)
public class SearchPresenterTest {

    @Mock
    ApiService apiService;
    @Mock
    LocationHelper locationHelper;
    @Mock
    SearchContract.View view;
    @Mock
    Location currentLocation;
    @Mock
    PicassoWrapper picassoWrapper;
    @Mock
    EventBus eventBus;
    private SearchPresenter searchPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        searchPresenter =
                new SearchPresenter(apiService, locationHelper, picassoWrapper, Schedulers.immediate(), Schedulers.immediate(), eventBus);
        searchPresenter.attachView(view);

    }

    @Test
    public void searchSuccess() {
        when(apiService.search(anyString(), anyString(), anyInt())).thenReturn(Observable.just(Generator.getDummyResponse()));

        searchPresenter.setCurrentLocation(currentLocation);

        searchPresenter.onSearchClick("shop");
        verify(apiService).search(anyString(), anyString(), anyInt());
        verify(view).showSearchLoading();
        verify(view).hideSearchLoading();
        verify(view, times(2)).updateMap(any(MapHelper.class));
        verify(view, never()).showErrorMessage(anyString());
    }

    @Test
    public void searchWithoutLocation() {
        searchPresenter.onSearchClick("shop");
        verify(view).showErrorMessage(R.string.message_location_is_unknown);
        verify(apiService, never()).search(anyString(), anyString(), anyInt());
        verify(view, never()).showSearchLoading();
    }

    @Test
    public void searchWithEmptyQuery() {
        searchPresenter.setCurrentLocation(currentLocation);
        searchPresenter.onSearchClick("");
        verify(view).showErrorMessage(R.string.message_query_is_empty);
        verify(apiService, never()).search(anyString(), anyString(), anyInt());
        verify(view, never()).showSearchLoading();
    }

    @Test
    public void mapIsReady() {
        searchPresenter.mapIsReady();
        verify(view).updateMap(any(MapHelper.class));
    }

    @Test
    public void setCurrentLocation() {
        searchPresenter.setCurrentLocation(currentLocation);
        verify(view).enableSearch(true);
    }

    @Test
    public void filView() {
        searchPresenter.fillView();
        verify(view).hideLocationProgress();
        verify(view).showLocationInfo(anyString());
        verify(view).hideSearchLoading();
        verify(view).enableSearch(false);
    }

}