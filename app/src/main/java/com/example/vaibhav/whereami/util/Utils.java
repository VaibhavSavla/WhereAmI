package com.example.vaibhav.whereami.util;

import android.content.Intent;
import android.location.Address;
import android.os.Build;

import com.example.vaibhav.whereami.R;
import com.google.android.gms.location.places.Place;

import java.util.List;

/**
 * Author: Vaibhav Savla
 * Date: 18/06/16.
 */

public class Utils {

    public static int getDrawableFromPlaceId(List<Integer> placeTypes) {

        if (placeTypes.contains(Place.TYPE_RESTAURANT))
            return R.drawable.ic_local_dining_black_24dp;
        if (placeTypes.contains(Place.TYPE_CAFE))
            return R.drawable.ic_local_cafe_black_24dp;
        if (placeTypes.contains(Place.TYPE_BAR))
            return R.drawable.ic_local_bar_black_24dp;
        if (placeTypes.contains(Place.TYPE_SHOPPING_MALL))
            return R.drawable.ic_local_mall_black_24dp;
        if (placeTypes.contains(Place.TYPE_GAS_STATION))
            return R.drawable.ic_local_gas_station_black_24dp;
        if (placeTypes.contains(Place.TYPE_ATM))
            return R.drawable.ic_local_atm_black_24dp;
        return R.drawable.ic_place_black_24dp;
    }

    public static String fullAddress(String primaryText, String secondaryText) {
        return String.format("%s, %s", primaryText, secondaryText);
    }

    public static Intent getShareLocationIntent(Address address) {

        String fullAddress = fullAddress(address);
        Intent shareIntent = new Intent(Intent.ACTION_SEND);
        shareIntent.setType("text/plain");
        shareIntent.putExtra(Intent.EXTRA_TEXT, fullAddress);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
        }
        return shareIntent;
    }

    private static String fullAddress(Address address) {
        StringBuilder builder = new StringBuilder();
        int limit = address.getMaxAddressLineIndex();
        for (int i = 0; i < limit; i++) {
            builder.append(address.getAddressLine(i)).append(", ");
        }
        builder.append(address.getAddressLine(limit));

        return builder.toString();
    }
}
