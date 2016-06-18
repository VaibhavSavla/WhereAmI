package com.example.vaibhav.whereami.mycity;

import android.content.Intent;
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
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLngBounds;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import permission.auron.com.marshmallowpermissionhelper.ActivityManagePermission;
import permission.auron.com.marshmallowpermissionhelper.PermissionResult;
import permission.auron.com.marshmallowpermissionhelper.PermissionUtils;

public class MyLocationActivity extends ActivityManagePermission implements OnMapReadyCallback {

    // private static final String TAG = MyLocationActivity.class.getSimpleName();

    @BindView(R.id.city_name_textview) TextView mCityTextView;
    @BindView(R.id.fab_search_places) FloatingActionButton mSearchPlacesFab;
    @BindView(R.id.cardView) CardView cardView;

    private MenuItem actionShare;

    private LatLngBounds bounds;

    private MyLocationHelper locationHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        locationHelper = new MyLocationHelper(this);

        askCompactPermission(PermissionUtils.Manifest_ACCESS_FINE_LOCATION, new PermissionResult() {
            @Override public void permissionGranted() {
                locationHelper.requestUserLocation();
            }

            @Override public void permissionDenied() {
                updateUIOnLocationFailed();
            }
        });

    }

    void updateUIOnLocationFailed() {
        mCityTextView.setText(R.string.location_error_message);
        mSearchPlacesFab.show();
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
                Intent shareIntent = locationHelper.getShareLocationIntent();
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_location_prompt)));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @OnClick(R.id.fab_search_places)
    void onClick() {
        startAutoCompleteActivity();
    }

    private void startAutoCompleteActivity() {
        Intent intent = new Intent(MyLocationActivity.this, PlaceAutoCompleteActivity.class);
        intent.putExtra(Constants.EXTRA_BOUNDS, bounds);
        startActivity(intent);
    }

    void updateUIOnLocationReceived(String cityName, String countryName) {
        mCityTextView.setText(Utils.fullAddress(cityName, countryName));
        actionShare.setVisible(true);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(MyLocationActivity.this);
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        locationHelper.prepareGoogleMap(googleMap);
        bounds = googleMap.getProjection().getVisibleRegion().latLngBounds;
        mSearchPlacesFab.show();
    }
}
