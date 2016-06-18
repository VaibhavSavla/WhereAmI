package com.example.vaibhav.whereami.placedetail;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.vaibhav.whereami.R;
import com.example.vaibhav.whereami.util.Constants;
import com.example.vaibhav.whereami.util.Utils;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NearbyPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    // public static final String TAG = NearbyPlaceActivity.class.getSimpleName();

    @BindView(R.id.list_item_date_textview) TextView mPrimaryTextView;
    @BindView(R.id.list_item_forecast_textview) TextView mSecondaryTextView;
    @BindView(R.id.list_item_icon) ImageView mPlaceIcon;
    @BindView(R.id.fab_search_places) FloatingActionButton mSearchPlaces;

    private NearbyPlaceHelper mNearbyPlaceHelper;

    private String primaryText;
    private String secondaryText;
    private ArrayList<Integer> playTypes;

    @OnClick(R.id.fab_search_places)
    void onClick() {
        Intent mapIntent = mNearbyPlaceHelper.getMapNavigationIntent();
        if (mapIntent.resolveActivity(getPackageManager()) != null) {
            startActivity(mapIntent);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_my_location, menu);
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
                Intent shareIntent = mNearbyPlaceHelper.getShareIntent(String.format("%s, %s", primaryText, secondaryText));
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share_location_prompt)));
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_place);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        ButterKnife.bind(this);

        Intent intent = getIntent();
        primaryText = intent.getStringExtra(Constants.EXTRA_PRIMARY_TEXT);
        secondaryText = intent.getStringExtra(Constants.EXTRA_SECONDARY_TEXT);
        playTypes = intent.getIntegerArrayListExtra(Constants.EXTRA_PLACE_TYPES);

        mNearbyPlaceHelper = new NearbyPlaceHelper(this);

        mNearbyPlaceHelper.getLatLngFromLocation(Utils.fullAddress(primaryText, secondaryText));
        updateUI();

    }

    private void updateUI() {
        mPrimaryTextView.setText(primaryText);
        mSecondaryTextView.setText(secondaryText);
        mPlaceIcon.setImageResource(Utils.getDrawableFromPlaceId(playTypes));

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(NearbyPlaceActivity.this);
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        mNearbyPlaceHelper.prepareGoogleMap(googleMap);
        mSearchPlaces.show();
    }
}
