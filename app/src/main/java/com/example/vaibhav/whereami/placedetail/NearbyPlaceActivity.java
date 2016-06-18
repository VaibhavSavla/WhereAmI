package com.example.vaibhav.whereami.placedetail;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Build;
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
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class NearbyPlaceActivity extends AppCompatActivity implements OnMapReadyCallback {

    // public static final String TAG = NearbyPlaceActivity.class.getSimpleName();

    @BindView(R.id.list_item_date_textview) TextView mPrimaryTextView;
    @BindView(R.id.list_item_forecast_textview) TextView mSecondaryTextView;
    @BindView(R.id.list_item_icon) ImageView mPlaceIcon;
    @BindView(R.id.fab_search_places) FloatingActionButton mSearchPlaces;
    private LatLng latLng;

    @OnClick(R.id.fab_search_places)
    void onClick() {
        Uri gmmIntentUri = Uri.parse("google.navigation:q=" + latLng.latitude + "," + latLng.longitude + "\n");
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage(Constants.PACKAGE_MAPS);
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
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_TEXT, mPrimaryTextView.getText() + ", " + mSecondaryTextView.getText());
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                    shareIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT);
                }
                startActivity(Intent.createChooser(shareIntent, "Share Location via: "));
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
        updateUI();
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        try {
            Address address = geocoder.getFromLocationName(mPrimaryTextView.getText() + ", " + mSecondaryTextView.getText(), 1).get(0);
            latLng = new LatLng(
                    address.getLatitude(),
                    address.getLongitude()
            );
            // Obtain the SupportMapFragment and get notified when the map is ready to be used.
            SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                    .findFragmentById(R.id.map);
            mapFragment.getMapAsync(NearbyPlaceActivity.this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void updateUI() {
        Intent intent = getIntent();
        String primaryText = intent.getStringExtra(Constants.EXTRA_PRIMARY_TEXT);
        String secondaryText = intent.getStringExtra(Constants.EXTRA_SECONDARY_TEXT);
        ArrayList<Integer> playTypes = intent.getIntegerArrayListExtra(Constants.EXTRA_PLACE_TYPES);
        mPrimaryTextView.setText(primaryText);
        mSecondaryTextView.setText(secondaryText);
        mPlaceIcon.setImageResource(Utils.getDrawableFromPlaceId(playTypes));
    }

    @Override public void onMapReady(GoogleMap googleMap) {
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 17));
        googleMap.addMarker(new MarkerOptions().position(latLng));
        mSearchPlaces.show();
    }
}
