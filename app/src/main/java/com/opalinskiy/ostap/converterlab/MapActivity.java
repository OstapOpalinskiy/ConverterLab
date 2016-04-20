package com.opalinskiy.ostap.converterlab;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;

public class MapActivity extends AbstractMapActivity {
    private GoogleMap map;

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
                }
            });
        } else {
           //TODO: показати діалог, з поівдомленням, що карти не доступні
        }
    }

}
