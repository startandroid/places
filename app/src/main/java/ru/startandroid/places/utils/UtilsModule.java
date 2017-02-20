package ru.startandroid.places.utils;

import android.content.Context;

import com.squareup.picasso.Picasso;

import dagger.Module;
import dagger.Provides;
import ru.startandroid.places.app.AppScope;
import ru.startandroid.places.utils.location.LocationHelper;
import ru.startandroid.places.utils.location.LocationHelperImpl;

@Module
public class UtilsModule {

    @Provides
    LocationHelper provideLocationHelper(Context context) {
        return new LocationHelperImpl(context);
    }



}
