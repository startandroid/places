package ru.startandroid.places.utils.location;

public interface LocationHelper {
    void start();
    void resume();
    void pause();
    void stop();
    void setCallback(LocationHelperCallback callback);
}
