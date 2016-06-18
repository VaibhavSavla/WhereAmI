package com.example.vaibhav.whereami.placedetail;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;

import com.example.vaibhav.whereami.util.Constants;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.Locale;

/**
 * Author: Vaibhav Savla
 * Date: 18/06/16.
 */

class NearbyPlaceHelper {

    private LatLng latLng;

    private NearbyPlaceActivity mContext;

    NearbyPlaceHelper(NearbyPlaceActivity mContext) {
        this.mContext = mContext;
    }

    Intent getShareIntent(String location) {
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, location);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }
        return shareIntent;
    }

    void getLatLngFromLocation(String location) {
        Geocoder geocoder = new Geocoder(mContext, Locale.getDefault());
        try {
            Address address = geocoder.getFromLocationName(location, 1).get(0);
            latLng = new LatLng(
                    address.getLatitude(),
                    address.getLongitude()
            );
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    Intent getMapNavigationIntent() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latLng.latitude + "," + latLng.longitude + "\n");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage(Constants.PACKAGE_MAPS);
        return mapIntent;
    }

    void prepareGoogleMap(GoogleMap map) {

        // for street level zoom
        float zoomLevel = 17.0f;

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, zoomLevel));
        map.addMarker(new MarkerOptions().position(latLng));
    }
}
