package com.example.gpsapp;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class MainViewModel extends ViewModel {
    public MutableLiveData<String> text = new MutableLiveData<>();
    private Location startLocation;
    private boolean isFirst = true;
    private LocationManager mLocationManager;
    private Context context;
    private Activity activity;
    private int distance = 0;
    private MutableLiveData<Boolean> isStart = new MutableLiveData<>(true);

    void init(Context context, Activity activity) {
        this.context = context;
        mLocationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        this.activity = activity;

    }

    private void searchLocation() {
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED &&
                ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
                        == PackageManager.PERMISSION_GRANTED) {
            if (isLocationEnabled()) {
                getLastLocation();
            } else {
                Toast.makeText(context, "Turn on location", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                context.startActivity(intent);
            }
        } else {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                activity.requestPermissions(
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            } else {
                ActivityCompat.requestPermissions(activity,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                                Manifest.permission.ACCESS_COARSE_LOCATION},
                        1);
            }

        }
    }

    @SuppressLint("MissingPermission")
    void onRequestPermissionsResult(int requestCode, @NonNull int[] grantResults) {
        if (requestCode == 1) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED
                    && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Granted. Start getting the location information
                getLastLocation();
            }
        }
    }

    private boolean isLocationEnabled() {
        LocationManager locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
        if (locationManager != null) {
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);
        }
        return false;
    }


    public void start() {
        searchLocation();
        isStart.postValue(false);
        text.postValue(distance + "");
    }

    public void stop() {
        mLocationManager.removeUpdates(locationListenerNetwork);
        mLocationManager.removeUpdates(locationListenerGps);
        distance = 0;
        isStart.postValue(true);
    }

    @SuppressLint("MissingPermission")
    private void getLastLocation() {
        mLocationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000,
                0, locationListenerNetwork);
        mLocationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000,
                0, locationListenerGps);

    }


    private LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            if (isFirst) {
                startLocation = location;
                isFirst = false;
            } else {
                Location endLocation = startLocation;
                startLocation = location;
                Float res = calculateDistance(startLocation, endLocation);
                distance = distance + Math.round(res);
                text.postValue(distance + "");

            }
            Log.d("location", location.getLongitude() + " " + location.getLatitude());
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };
    private LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
//
            Log.d("network", location.getLongitude() + " " + location.getLatitude());
        }

        public void onProviderDisabled(String provider) {
        }

        public void onProviderEnabled(String provider) {
        }

        public void onStatusChanged(String provider, int status, Bundle extras) {
        }
    };


    private Float calculateDistance(Location start, Location end) {
        float[] results = new float[1];
        Location.distanceBetween(start.getLatitude(), start.getLongitude(),
                end.getLatitude(), end.getLongitude(), results);
        return results[0];
    }


    public MutableLiveData<Boolean> isStart() {
        return isStart;
    }
}
