package com.example.vaibhav.whereami.util;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

/**
 * Author: Vaibhav Savla
 * Date: 18/06/16.
 */

public abstract class AbstractLocationListener implements LocationListener {
    @Override public void onLocationChanged(Location location) {

    }

    @Override public void onStatusChanged(String s, int i, Bundle bundle) {

    }

    @Override public void onProviderEnabled(String s) {

    }

    @Override public void onProviderDisabled(String s) {

    }
}
