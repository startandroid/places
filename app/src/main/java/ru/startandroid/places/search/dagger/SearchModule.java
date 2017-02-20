package ru.startandroid.places.search.dagger;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.places.events.Event;
import ru.startandroid.places.events.EventBus;
import ru.startandroid.places.search.ui.SearchContract;
import ru.startandroid.places.search.ui.SearchPresenter;
import ru.startandroid.places.utils.PicassoWrapper;
import ru.startandroid.places.utils.location.LocationHelper;
import ru.startandroid.places.web.ApiService;
import rx.Scheduler;

@Module
public class SearchModule {

    @Provides
    @SearchScope
    SearchContract.Presenter providePresenter(ApiService apiService, LocationHelper locationHelper, PicassoWrapper picassoWrapper,
                                              @Named("io") Scheduler ioScheduler, @Named("main") Scheduler mainScheduler, EventBus eventBus) {
        return new SearchPresenter(apiService, locationHelper, picassoWrapper, ioScheduler, mainScheduler, eventBus);
    }


}
