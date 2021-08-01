package com.centennial.deanpinlac_sanjibsaha_comp304sec002_lab5_ex1;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "Main";
    private SharedPreferences sharedPref;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        sharedPref = getSharedPreferences("", MODE_PRIVATE);

        ListView listCuisines = findViewById(R.id.listCuisines);
        listCuisines.setOnItemClickListener((adapterView, view, i, l) -> {
            String cuisine = adapterView.getItemAtPosition(i).toString();
            displayMessage(cuisine);

            //Send Cuisine of Choice to next Activity
            sharedPref.edit().putString("cuisine", cuisine).apply();
            Intent intent = new Intent(getApplicationContext(), RestaurantListActivity.class);
            startActivity(intent);
        });
    }

    private void displayMessage(String message){
        Toast.makeText(MainActivity.this, message, Toast.LENGTH_SHORT).show();
    }
}