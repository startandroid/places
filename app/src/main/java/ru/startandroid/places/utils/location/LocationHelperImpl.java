package ru.startandroid.places.utils.location;

import android.content.Context;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;

import ru.startandroid.places.R;

public class LocationHelperImpl implements LocationHelper, GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {

    private final Context context;
    private GoogleApiClient googleApiClient;
    private LocationRequest locationRequest;
    private LocationHelperCallback callback;
    private boolean viewIsResumed;
    private boolean dialogAlreadyShowed; // to avoid showing dialog for each onPause
    private boolean needToRequestSettings;
    private boolean isRequestingLocation;

    public LocationHelperImpl(Context context) {
        this.context = context;
        createGoogleApiClient();
        createLocationRequest();
    }

    private void createGoogleApiClient() {
        if (googleApiClient == null) {
            googleApiClient = new GoogleApiClient.Builder(context)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .addApi(LocationServices.API)
                    .build();
        }
    }

    private void createLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(10000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }

    private void requestSettings() {
        if (!needToRequestSettings || !googleApiClient.isConnected()) {
            return;
        }
        needToRequestSettings = false;

        checkLocationSettings();
    }

    private void checkLocationSettings() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder()
                .addLocationRequest(locationRequest);
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(googleApiClient, builder.build());

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(LocationSettingsResult result) {
                final Status status = result.getStatus();
                switch (status.getStatusCode()) {
                    case LocationSettingsStatusCodes.SUCCESS:
                        requestLocationUpdates();
                        break;
                    case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                        if (callback != null && !dialogAlreadyShowed) {
                            callback.changeSettings(status);
                            dialogAlreadyShowed = true;
                        }
                        break;
                    case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                        if (callback != null) {
                            callback.onFailed(context.getString(R.string.location_settings_are_not_satisfied));
                        }
                        break;
                }
            }
        });
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Location location = LocationServices.FusedLocationApi.getLastLocation(googleApiClient);
        setCurrentLocation(location);
        requestSettings();
    }

    @Override
    public void onConnectionSuspended(int i) {
        removeLocationUpdates();
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        if (callback != null) {
            if (connectionResult.hasResolution()) {
                callback.showResolutions(connectionResult);
            } else {
                callback.onFailed(String.format("Connection error: %s (%s)",  connectionResult.getErrorMessage(), connectionResult.getErrorCode()));
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        setCurrentLocation(location);
    }

    private void setCurrentLocation(Location location) {
        if (location != null && callback != null) {
            callback.onChangeLocation(location);
        }
    }

    @Override
    public void start() {
        needToRequestSettings = true;
        googleApiClient.connect();

    }

    @Override
    public void resume() {
        viewIsResumed = true;
        requestSettings();
    }

    @Override
    public void pause() {
        viewIsResumed = false;
        needToRequestSettings = true;
        removeLocationUpdates();
    }

    @Override
    public void stop() {
        googleApiClient.disconnect();
        dialogAlreadyShowed = false;
    }

    @Override
    public void setCallback(LocationHelperCallback callback) {
        this.callback = callback;
    }

    private void requestLocationUpdates() {
        if (googleApiClient.isConnected() && viewIsResumed && !isRequestingLocation) {
            isRequestingLocation = true;
            LocationServices.FusedLocationApi.requestLocationUpdates(
                    googleApiClient, locationRequest, this);
            if (callback != null) {
                callback.onRequestLocation();
            }
        }
    }

    private void removeLocationUpdates() {
        isRequestingLocation = false;
        if (googleApiClient.isConnected()) {
            LocationServices.FusedLocationApi.removeLocationUpdates(googleApiClient, this);
        }
    }

}
