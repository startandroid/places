package ru.startandroid.places.app;

import android.content.Context;

import com.squareup.picasso.Picasso;

import javax.inject.Named;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.places.events.EventBus;
import ru.startandroid.places.utils.PicassoWrapper;
import rx.Scheduler;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

@Module
class AppModule {

    private final Context context;

    AppModule(Context context) {
        this.context = context;
    }

    @Provides
    Context provideContext() {
        return context;
    }

    @AppScope
    @Provides
    PicassoWrapper providePicasso(Context context) {
        return new PicassoWrapper(context);
    }

    @AppScope
    @Named("io")
    @Provides
    Scheduler provideIoScheduler() {
        return Schedulers.io();
    }

    @AppScope
    @Named("main")
    @Provides
    Scheduler provideMainScheduler() {
        return AndroidSchedulers.mainThread();
    }

    @AppScope
    @Provides
    EventBus provideEventBus() {
        return new EventBus();
    }
}
