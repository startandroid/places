package ru.startandroid.places.events;

public class Events {

    public static Event startSearch() {
        return createEvent(EventType.SEARCH_STARTED);
    }

    public static Event finishSearch() {
        return createEvent(EventType.SEARCH_FINISHED);
    }

    private static Event createEvent(int type) {
        Event event = new Event();
        event.setType(type);
        return event;
    }

}
