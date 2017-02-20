package ru.startandroid.places.app;

import dagger.Component;
import ru.startandroid.places.events.EventBus;
import ru.startandroid.places.search.dagger.SearchComponent;
import ru.startandroid.places.utils.UtilsModule;
import ru.startandroid.places.web.WebModule;

@AppScope
@Component(modules = {AppModule.class, WebModule.class, UtilsModule.class})
public interface AppComponent {
    SearchComponent createSearchComponent();
    public EventBus getEventBus();
}
