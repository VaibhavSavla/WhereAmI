package com.example.vaibhav.whereami.searchplaces;

import android.content.Intent;
import android.database.DataSetObserver;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.vaibhav.whereami.R;
import com.example.vaibhav.whereami.placedetail.NearbyPlaceActivity;
import com.example.vaibhav.whereami.util.Constants;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

public class PlaceAutoCompleteActivity extends AppCompatActivity implements GoogleApiClient.OnConnectionFailedListener {

    private static final String TAG = PlaceAutoCompleteActivity.class.getSimpleName();

    @BindView(R.id.dropdown_anchor) TextView mErrorTextView;

    private GoogleApiClient mGoogleApiClient;

    private PlaceAutoCompleteAdapter mAdapter;

    private DataSetObserver observer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_place_auto_complete);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        ButterKnife.bind(this);
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .enableAutoManage(this, 0 /* clientId */, this)
                .addApi(Places.GEO_DATA_API)
                .build();
        mGoogleApiClient.connect();
        // Retrieve the AutoCompleteTextView that will display Place suggestions.
        AutoCompleteTextView mAutocompleteView = (AutoCompleteTextView)
                findViewById(R.id.autoCompleteTextView);
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

        // Set up the adapter that will retrieve suggestions from the Places Geo Data API that cover
        // the entire world.
        Intent intent = getIntent();
        LatLngBounds bounds = intent.getParcelableExtra(Constants.EXTRA_BOUNDS);

        mAdapter =
                new PlaceAutoCompleteAdapter(this, mGoogleApiClient, bounds, null);
        mAutocompleteView.setAdapter(mAdapter);
        mAutocompleteView.setThreshold(1);
        mAutocompleteView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                AutocompletePrediction prediction = (AutocompletePrediction) adapterView.getItemAtPosition(i);
                Log.d(TAG, "Full Address" + prediction.getFullText(null));
                Intent intent = new Intent(PlaceAutoCompleteActivity.this, NearbyPlaceActivity.class);
                intent.putExtra(Constants.EXTRA_PRIMARY_TEXT, prediction.getPrimaryText(null));
                intent.putExtra(Constants.EXTRA_SECONDARY_TEXT, prediction.getSecondaryText(null));
                intent.putIntegerArrayListExtra(Constants.EXTRA_PLACE_TYPES, (ArrayList<Integer>) prediction.getPlaceTypes());
                startActivity(intent);
            }
        });

        observer = new DataSetObserver() {
            @Override public void onChanged() {
                super.onChanged();
                mErrorTextView.setText("");
            }

            @Override public void onInvalidated() {
                super.onInvalidated();
                mErrorTextView.setText(R.string.error_no_place_found);
            }
        };
        mAdapter.registerDataSetObserver(observer);

    }

    @Override protected void onDestroy() {
        super.onDestroy();
        mGoogleApiClient.disconnect();
        mAdapter.unregisterDataSetObserver(observer);
    }

    /**
     * Called when the Activity could not connect to Google Play services and the auto manager
     * could resolve the error automatically.
     * In this case the API is not available and notify the user.
     *
     * @param connectionResult can be inspected to determine the cause of the failure
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

        Log.e(TAG, "onConnectionFailed: ConnectionResult.getErrorCode() = "
                + connectionResult.getErrorCode());

        Toast.makeText(this,
                "Could not connect to Google API Client: Error " + connectionResult.getErrorCode(),
                Toast.LENGTH_SHORT).show();
    }


}
