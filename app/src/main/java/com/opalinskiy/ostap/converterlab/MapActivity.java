package com.opalinskiy.ostap.converterlab;

import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.opalinskiy.ostap.converterlab.abstractActivities.AbstractMapActivity;
import com.opalinskiy.ostap.converterlab.utils.MapLoader;

import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.ExecutionException;

public class MapActivity extends AbstractMapActivity {
    private GoogleMap map;
    private MapLoader locationLoader;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (isGoogleMapsAvailable()) {
            setContentView(R.layout.activity_map);
            ((MapFragment) getFragmentManager().findFragmentById(R.id.google_map)).getMapAsync(new OnMapReadyCallback() {
                @Override
                public void onMapReady(GoogleMap googleMap) {
                    map = googleMap;
                    map.setMapType(GoogleMap.MAP_TYPE_NORMAL);
                    String address = getIntent().getStringExtra("address");
                    String city = getIntent().getStringExtra("city");
                    locationLoader = new MapLoader();
                    locationLoader.execute(city, address);
                    try {
                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(locationLoader.get(), locationLoader.getZoom()));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    }
//                    String address = getIntent().getStringExtra("address");
////                    Log.d("TAG", address);
////                    if (getLatLngFromAddress(address) != null) {
////                        LatLng currentLatLng = new LatLng(getLatLngFromAddress(address).latitude,
////                                getLatLngFromAddress(address).longitude);
////                        map.moveCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 13));
////                        Log.d("TAG", getLatLngFromAddress(address).latitude + "");
//                    } else {
//                        Toast.makeText(MapActivity.this, "Cant find address on the map", Toast.LENGTH_SHORT).show();
//                    }
                }
            });
        } else {
            //TODO: показати діалог, з поівдомленням, що карти не доступні
        }
    }

    class LocationLoader extends AsyncTask<String, Void, LatLng> {

        @Override
        protected LatLng doInBackground(String... params) {

            Geocoder coder = new Geocoder(MapActivity.this);
            try {
                ArrayList<Address> adresses = (ArrayList<Address>) coder.getFromLocationName(params[0], 5);
                Log.d("TAG", "found addresses: " + adresses.size());
                if (adresses.size() > 0) {
                    Address add = adresses.get(0);
                    double longitude = add.getLongitude();
                    double latitude = add.getLatitude();
                    LatLng res = new LatLng(latitude, longitude);
                    return res;
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(LatLng latLng) {
            super.onPostExecute(latLng);
            if (latLng != null) {
                Log.d("TAG", "onPostExecute");
                map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 13));
            } else {
                Toast.makeText(MapActivity.this, "Cannot find location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}