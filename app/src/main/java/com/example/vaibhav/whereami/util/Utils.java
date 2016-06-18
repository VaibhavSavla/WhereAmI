package com.example.vaibhav.whereami.util;

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
        if (placeTypes.contains(Place.TYPE_CAFE)) return R.drawable.ic_local_cafe_black_24dp;
        if (placeTypes.contains(Place.TYPE_BAR)) return R.drawable.ic_local_bar_black_24dp;
        if (placeTypes.contains(Place.TYPE_SHOPPING_MALL))
            return R.drawable.ic_local_mall_black_24dp;
        if (placeTypes.contains(Place.TYPE_GAS_STATION))
            return R.drawable.ic_local_gas_station_black_24dp;
        if (placeTypes.contains(Place.TYPE_ATM)) return R.drawable.ic_local_atm_black_24dp;
        return R.drawable.ic_place_black_24dp;
    }
}
