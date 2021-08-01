package com.centennial.deanpinlac_sanjibsaha_comp304sec002_lab5_ex1;

import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AddressComponent;
import com.google.android.libraries.places.api.model.AddressComponents;
import com.google.android.libraries.places.api.model.Period;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.PlacesClient;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

public class DetailsActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "Details";
    private SharedPreferences sharedPref;

    private PlacesClient placesClient;
    private List<Place.Field> placeFields;
    private String placeId;

    private TextView textRestaurantName;
    private TextView textRestaurantAddress;
    private TextView textRestaurantOpening;
    private TextView textRestaurantContact;
    private TextView textRestaurantRating;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        sharedPref = getSharedPreferences("", MODE_PRIVATE);
        placeId = sharedPref.getString("placeId", "");

        //Initialize Text Views
        textRestaurantName = findViewById(R.id.restaurantName);
        textRestaurantAddress = findViewById(R.id.restaurantAddress);
        textRestaurantOpening = findViewById(R.id.restaurantOpening);
        textRestaurantContact = findViewById(R.id.restaurantContact);
        textRestaurantRating = findViewById(R.id.restaurantRating);

        //Initialize the SDK
        Places.initialize(getApplicationContext(), BuildConfig.MAPS_API_KEY);

        //Create a new PlacesClient instance
        placesClient = Places.createClient(this);

        // Specify the fields to return.
        placeFields = Arrays.asList(
                Place.Field.NAME,
                Place.Field.ADDRESS,
                Place.Field.ADDRESS_COMPONENTS,
                Place.Field.LAT_LNG,
                Place.Field.OPENING_HOURS,
                Place.Field.PHONE_NUMBER,
                Place.Field.RATING
        );

        // Get the SupportMapFragment and request notification when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private void displayMessage(String message){
        Toast.makeText(DetailsActivity.this, message, Toast.LENGTH_SHORT).show();
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public void onMapReady(GoogleMap googleMap) {
        DateTimeFormatter dtf = DateTimeFormatter.ofPattern("h:mm a");
        googleMap.getUiSettings().setZoomControlsEnabled(true);
        // Construct a request object, passing the place ID and fields array.
        final FetchPlaceRequest request = FetchPlaceRequest.newInstance(placeId, placeFields);

        placesClient.fetchPlace(request).addOnSuccessListener((response) -> {
            Place place = response.getPlace();

            //Set Restaurant Details
            textRestaurantName.setText(place.getName());
            String shortAddress = getShortAddress(place.getAddressComponents());
            textRestaurantAddress.setText("Address: " + shortAddress);
            try{
                List<Period> periods = place.getOpeningHours().getPeriods();
                LocalDate date = LocalDate.now();
                int day = date.getDayOfWeek().getValue();
                Period period = periods.get(day % 7);
                LocalTime openTime = LocalTime.of(period.getOpen().getTime().getHours(),
                        period.getOpen().getTime().getMinutes());
                LocalTime closeTime = LocalTime.of(period.getClose().getTime().getHours(),
                        period.getClose().getTime().getMinutes());
                String opening = openTime.format(dtf) +
                        " to " + closeTime.format(dtf);
                textRestaurantOpening.setText("Today's Opening: " + opening);
            }catch(Exception e){
                textRestaurantOpening.setText("Today's Opening: Not Available" );
            }
            textRestaurantContact.setText("Contact: " + place.getPhoneNumber());
            textRestaurantRating.setText("Rating: " + place.getRating() + " out of 5");

            googleMap.addMarker(new MarkerOptions()
                    .position(place.getLatLng())
                    .title(place.getName()));

            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(place.getLatLng(), 15));
        }).addOnFailureListener((exception) -> {
            if (exception instanceof ApiException) {
                final ApiException apiException = (ApiException) exception;
                Log.e(TAG, "Place not found: " + exception.getMessage());
                final int statusCode = apiException.getStatusCode();
                // TODO: Handle error with given status code.
            }
        });
    }

    private String getShortAddress(AddressComponents addressComponents){
        List<AddressComponent> components = addressComponents.asList();

        String address = components.get(0).getName() + " " +
                        components.get(1).getName() + ", " +
                        components.get(2).getName() + ", " +
                        components.get(3).getShortName();

        return address;
    }
}