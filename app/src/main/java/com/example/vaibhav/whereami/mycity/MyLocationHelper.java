package com.example.vaibhav.whereami.mycity;

import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.util.Log;

import com.example.vaibhav.whereami.util.AbstractLocationListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Locale;

import static android.content.Context.LOCATION_SERVICE;

/**
 * Author: Vaibhav Savla
 * Date: 18/06/16.
 */

class MyLocationHelper {

    private static final String TAG = MyLocationHelper.class.getSimpleName();

    private MyLocationActivity mContext;

    private Address address;

    private LatLng cityLatLng;

    MyLocationHelper(MyLocationActivity mContext) {
        this.mContext = mContext;
    }

    Intent getShareLocationIntent() {
        StringBuilder builder = new StringBuilder();
        int limit = address.getMaxAddressLineIndex();
        for (int i = 0; i < limit; i++) {
            builder.append(address.getAddressLine(i)).append(", ");
        }
        builder.append(address.getAddressLine(limit));
        Log.d(TAG, builder.toString());

        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, builder.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }
        return shareIntent;
    }

    void requestUserLocation() {
        final LocationManager manager = (LocationManager) mContext.getSystemService(LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        criteria.setAltitudeRequired(false);

        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            requestLocationProvider();
        }

        if (ActivityCompat.checkSelfPermission(mContext, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        manager.requestSingleUpdate(criteria, new AbstractLocationListener() {
            @Override public void onLocationChanged(Location location) {

                try {
                    LatLng exactLatLng = new LatLng(
                            location.getLatitude(),
                            location.getLongitude()
                    );

                    Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
                    address = geocoder.getFromLocation(exactLatLng.latitude, exactLatLng.longitude, 1).get(0);
                    String cityName = address.getLocality();
                    String countryName = address.getCountryName();
                    Log.d(TAG, cityName);

                    Address cityAddress = geocoder.getFromLocationName(cityName, 1).get(0);
                    cityLatLng = new LatLng(
                            cityAddress.getLatitude(),
                            cityAddress.getLongitude()
                    );
                    mContext.updateUIOnLocationReceived(cityName + ", " + countryName);
                } catch (IOException e) {
                    Log.e(TAG, "Unable to get location", e);
                }
            }
        }, null);
    }

    private void requestLocationProvider() {
        AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
        dialog.setMessage("Please Enable Device Location");
        dialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                Intent myIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                mContext.startActivity(myIntent);
            }
        });
        dialog.setNegativeButton("No", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface paramDialogInterface, int paramInt) {
                // If user does not turn on location directly take the user
                // to the second screen where he can enter the location manually.
                mContext.updateUIOnLocationFailed();
            }
        });
        dialog.show();
    }

    void prepareGoogleMap(GoogleMap map) {
        map.moveCamera(CameraUpdateFactory.newLatLngZoom(cityLatLng, 12));
    }
}
