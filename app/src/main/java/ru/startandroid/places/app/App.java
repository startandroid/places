package ru.startandroid.places.app;

import android.app.Application;
import android.content.Context;

public class App extends Application {

    private ComponentsHolder componentsHolder;

    @Override
    public void onCreate() {
        super.onCreate();
        componentsHolder = new ComponentsHolder(this);
        componentsHolder.init();
    }

    public static App getApp(Context context) {
        return (App) context.getApplicationContext();
    }

    public ComponentsHolder getComponentsHolder() {
        return componentsHolder;
    }
}
