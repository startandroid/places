package ru.startandroid.places;

import android.support.test.espresso.IdlingResource;

import ru.startandroid.places.events.Event;
import ru.startandroid.places.events.EventBus;
import ru.startandroid.places.events.EventType;
import rx.Subscription;
import rx.functions.Action1;

public class SearchIdlingResource implements IdlingResource, Action1<Event> {

    private final EventBus eventBus;
    Subscription subscription;
    boolean searchIsRunning;
    private ResourceCallback resourceCallback;

    public SearchIdlingResource(EventBus eventBus) {
        this.eventBus = eventBus;
        subscription = eventBus.getEventsObservable(EventType.SEARCH_STARTED, EventType.SEARCH_FINISHED).subscribe(this);
    }

    @Override
    public String getName() {
        return SearchIdlingResource.class.getName();
    }

    @Override
    public boolean isIdleNow() {
        boolean idle = !searchIsRunning;
        if (idle && resourceCallback != null) {
            resourceCallback.onTransitionToIdle();
        }
        return idle;
    }

    @Override
    public void registerIdleTransitionCallback(ResourceCallback callback) {
        this.resourceCallback = callback;
    }

    public void destroy() {
        subscription.unsubscribe();
    }

    @Override
    public void call(Event event) {
        switch (event.getType()) {
            case EventType.SEARCH_STARTED:
                searchIsRunning = true;
                break;
            case EventType.SEARCH_FINISHED:
                searchIsRunning = false;
                break;
        }
    }
}
