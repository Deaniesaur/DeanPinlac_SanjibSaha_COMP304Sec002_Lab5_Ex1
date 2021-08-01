package com.centennial.deanpinlac_sanjibsaha_comp304sec002_lab5_ex1;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextPaint;
import android.text.style.CharacterStyle;
import android.util.Log;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.RectangularBounds;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.google.android.libraries.places.widget.AutocompleteSupportFragment;
import com.google.android.libraries.places.widget.listener.PlaceSelectionListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

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

        RectangularBounds bounds = RectangularBounds.newInstance(
                new LatLng(43.582520, -79.650662),
                new LatLng(43.857446, -79.117825)
        );
//
        FindAutocompletePredictionsRequest request = FindAutocompletePredictionsRequest.builder()
                .setLocationBias(bounds)
                .setCountry("CA")
                .setTypeFilter(TypeFilter.ESTABLISHMENT)
                .setSessionToken(token)
                .setQuery(cuisineQuery)
                .build();
//
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



//        //Initialize the AutocompleteSupportFragment
//        AutocompleteSupportFragment autocompleteRestaurant = (AutocompleteSupportFragment)
//                getSupportFragmentManager().findFragmentById(R.id.autocompleteRestaurant);
//
//        //Specify the types of place data to return
//        autocompleteRestaurant.setPlaceFields(Arrays.asList(
//                Place.Field.ID, Place.Field.NAME, Place.Field.TYPES));
//
//        autocompleteRestaurant.setTypeFilter(TypeFilter.ESTABLISHMENT);
////        autocompleteRestaurant.setTypeFilter(TypeFilter.valueOf("restaurant"));
//        autocompleteRestaurant.setCountry("CA");
//        displayMessage("Hello Dean");
//        autocompleteRestaurant.setOnPlaceSelectedListener(new PlaceSelectionListener() {
//            @Override
//            public void onPlaceSelected(@NonNull Place place) {
//                displayMessage(place.getId() + " | " + place.getName());
//            }
//
//            @Override
//            public void onError(@NonNull Status status) {
//
//            }
//        });
    }

    private void displayMessage(String message){
        Toast.makeText(RestaurantListActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}