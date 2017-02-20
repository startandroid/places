package ru.startandroid.places.app;

import android.content.Context;

import ru.startandroid.places.search.dagger.SearchComponent;

public class ComponentsHolder {

    private final Context context;
    private AppComponent appComponent;
    private SearchComponent searchComponent;

    ComponentsHolder(Context context) {
        this.context = context;
    }


    void init() {
        appComponent = DaggerAppComponent.builder().appModule(new AppModule(context)).build();
    }

    // app component
    public AppComponent getAppComponent() {
        return appComponent;
    }

    // search component
    public SearchComponent getSearchComponent() {
        if (searchComponent == null) {
            searchComponent = appComponent.createSearchComponent();
        }
        return searchComponent;
    }

    public void releaseSearchComponent() {
        searchComponent = null;
    }


}
