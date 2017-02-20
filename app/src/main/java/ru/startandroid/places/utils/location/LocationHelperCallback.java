package ru.startandroid.places.utils.location;

import android.location.Location;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.Status;

public interface LocationHelperCallback {
    void onRequestLocation();
    void onChangeLocation(Location location);
    void onFailed(String message);
    void changeSettings(Status status);
    void showResolutions(ConnectionResult connectionResult);

}
