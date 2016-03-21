//------------------------------------------------------------------------------
//  File       : MapsActivity.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 03/16/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.googlemapapp;

import android.location.Address;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.EditText;
import com.google.android.gms.maps.*;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    public void onClick(View view) {
        Log.d(clazz, "Do searching one route to the destination");
        EditText input = (EditText)findViewById(R.id.address);
        String dest = input.getText().toString();
        if (false == dest.trim().equals("")) {
            Log.d(clazz, "User specified address: " + dest);
            Log.d(clazz, "Clear up all existing markers and routes");
            mMap.clear();

            MapMarker markers = new MapMarker(mMap, MapsActivity.this);
            Address src = markers.getAddress(getString(R.string.init_location));
            Address destAddr = markers.getAddress(dest);
            markers.execute(getString(R.string.init_location));
            markers = new MapMarker(mMap, MapsActivity.this);
            markers.execute(dest);
            MapDirection mapDir = new MapDirection(mMap, MapsActivity.this);
            mapDir.execute(src, destAddr);

            //marker.execute(dest);
        } else
            Log.d(clazz, "User specified nothing");

        Log.d(clazz, "Done with route drawing");
    }


    /**
     * Manipulates the map once available. This callback is triggered when the
     * map is ready to be used. This is where we can add markers or lines, add
     * listeners or move the camera. In this case, we just add a marker near
     * Sydney, Australia. If Google Play services is not installed on the
     * device, the user will be prompted to install it inside the
     * SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(clazz, "Pinning the default address: St. Joseph's Univ");
        mMap = googleMap;
        //mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);

        MapMarker marker = new MapMarker(mMap, MapsActivity.this);
        marker.execute(getString(R.string.init_location));
        Log.d(clazz, "Done with pinning the marker");
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment)getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    private final String clazz = getClass().getSimpleName();
}
