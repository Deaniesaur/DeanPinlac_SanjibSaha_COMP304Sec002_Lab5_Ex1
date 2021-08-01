package com.centennial.deanpinlac_sanjibsaha_comp304sec002_lab5_ex1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.util.ArrayList;
import java.util.List;

public class RestaurantListActivity extends AppCompatActivity {

    private static final String TAG = "RestaurantList";
    private List<String> listPlaceId = new ArrayList<>();
    private List<String> listPlaceName = new ArrayList<>();
    private SharedPreferences sharedPref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_restaurant_list);

        sharedPref = getSharedPreferences("", MODE_PRIVATE);
        String cuisineQuery = sharedPref.getString("cuisine", "") + " Food";

        ListView listRestaurants = findViewById(R.id.listRestaurants);
        ArrayAdapter restaurantsAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1, listPlaceName);
        listRestaurants.setAdapter(restaurantsAdapter);

        //Initialize the SDK
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        //Create a new PlacesClient instance
        PlacesClient placesClient = Places.createClient(this);

        AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        //Rectangular Bounds of Toronto
        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(43.582520, -79.650662),
                new LatLng(43.857446, -79.117825)
        );

        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setCountry("CA")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(cuisineQuery)
                .build();

        placesClient.findAutocompletePredictions(request).addOnSuccessListener((response) -> {
            listPlaceId.clear();
            listPlaceName.clear();
            for (AutocompletePrediction prediction : response.getAutocompletePredictions()) {
                listPlaceId.add(prediction.getPlaceId());
                listPlaceName.add(prediction.getPrimaryText(null).toString());
                Log.i(TAG, prediction.getPlaceId());
                Log.i(TAG, prediction.getPrimaryText(null).toString());
            }

            restaurantsAdapter.notifyDataSetChanged();
        }).addOnFailureListener((exception) -> {
            if(exception instanceof ApiException) {
                ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + apiException.getStatusCode());
            }
        });

        listRestaurants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                sharedPref.edit().putString("placeId", listPlaceId.get(i)).apply();

                Intent intent = new Intent(RestaurantListActivity.this,
                        DetailsActivity.class);
                startActivity(intent);
            }
        });
    }

    private void displayMessage(String message){
        Toast.makeText(RestaurantListActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}