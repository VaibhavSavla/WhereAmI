package com.example.vaibhav.whereami.mycity;

import android.content.Intent;
import android.location.Address;
import android.location.Location;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.CardView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.vaibhav.whereami.R;
import com.example.vaibhav.whereami.searchplaces.PlaceAutoCompleteActivity;
import com.example.vaibhav.whereami.util.Constants;
import com.example.vaibhav.whereami.util.Utils;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import io.nlopez.smartlocation.OnLocationUpdatedListener;
import io.nlopez.smartlocation.OnReverseGeocodingListener;
import io.nlopez.smartlocation.SmartLocation;
import io.nlopez.smartlocation.location.providers.LocationGooglePlayServicesProvider;
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

public class MyLocationActivity extends ActivityManagePermission implements OnMapReadyCallback, OnLocationUpdatedListener, OnReverseGeocodingListener, PermissionResult {

    // private static final String TAG = MyLocationActivity.class.getSimpleName();

    @BindView(R.id.city_name_textview) TextView mCityTextView;
    @BindView(R.id.fab_search_places) FloatingActionButton mSearchPlacesFab;
    @BindView(R.id.cardView) CardView cardView;

    private MenuItem actionShare;

    private LatLngBounds bounds;

    private LatLng latLng;
    private boolean hasPermission;

    private Address address;
    private LocationGooglePlayServicesProvider provider;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);

        provider = new LocationGooglePlayServicesProvider();
        provider.setCheckLocationSettings(true);

        askCompactPermission(PermissionUtils.Manifest_ACCESS_FINE_LOCATION, this);
    }

    @Override protected void onResume() {
        super.onResume();
        if (hasPermission) {
            SmartLocation.with(this).location(provider).oneFix().start(this);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_location, menu);
        actionShare = menu.findItem(R.id.action_share);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_share:
                Intent shareIntent = Utils.getShareLocationIntent(address);
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_location_prompt)));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab_search_places)
    void onClick() {
        Intent intent = new Intent(MyLocationActivity.this, PlaceAutoCompleteActivity.class);
        intent.putExtra(Constants.EXTRA_BOUNDS, bounds);
        startActivity(intent);
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
        bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        mSearchPlacesFab.show();
    }

    @Override public void onLocationUpdated(Location location) {
        SmartLocation.with(this).geocoding().reverse(location, this);
        latLng = new LatLng(location.getLatitude(), location.getLongitude());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MyLocationActivity.this);
    }

    @Override public void onAddressResolved(Location location, List<Address> list) {
        address = list.get(0);
        mCityTextView.setText(Utils.fullAddress(address.getLocality(), address.getCountryName()));
        actionShare.setVisible(true);
    }

    @Override public void permissionGranted() {
        hasPermission = true;
    }

    @Override public void permissionDenied() {
        updateUIOnLocationFailed();
        hasPermission = false;
    }

    private void updateUIOnLocationFailed() {
        mCityTextView.setText(R.string.location_error_message);
        mSearchPlacesFab.show();
    }
}
