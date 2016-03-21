//------------------------------------------------------------------------------
//  File       : DirectionJSONParser.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 03/18/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.googlemapapp;

import android.content.Context;
import android.util.Log;
import com.google.android.gms.maps.model.LatLng;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class DirectionJSONParser {

    DirectionJSONParser(String url) {
        try {
            mapAPI = new URL(url);
        } catch (MalformedURLException mue) {
            Log.e(clazz, "Cannot resolve the given url", mue);
        }
    }


    /**
     * Download and parse the direction data to a list of locations
     *
     * @return
     */
    List<LatLng> parse() {
        return parse(download());
    }


    /**
     * Download the direction way point data from google map direction API.
     *
     * @return A JSON string
     */
    String download() {
        try {
            Log.d(clazz, "Downloading the direction JSON data");
            StringBuffer buff = new StringBuffer();
            HttpURLConnection conn = (HttpURLConnection)mapAPI.openConnection();
            int rc = conn.getResponseCode();
            Log.d(clazz, "HTTP Response Code: " + rc);
            BufferedReader br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) {
                buff.append(line);
                buff.append("\n");
            }
            br.close();
            conn.disconnect();
            Log.d(clazz, "JSON downloaded: " + buff.toString());
            return json = buff.toString();
        } catch (IOException ioe) {
            Log.e(clazz, "IOException: " + ioe.getMessage(), ioe);
            Log.d(clazz, "Unexpected exception: IOException - "
                    + ioe.getMessage());
        } catch (SecurityException se) {
            Log.e(clazz, "Security Exception: " + se.getMessage(), se);
            Log.d(clazz, "Failed. Permission required: " + se.getMessage());
        }
        return null;
    }


    LatLng boundNortheast() {
        return northeast;
    }

    LatLng boundSouthwest() {
        return southwest;
    }


    protected List<LatLng> parse(String jsonContext) {
        Log.d(clazz, "Parsing the JSON data");
        if (jsonContext == null || jsonContext.trim().equals(""))
            return null;

        try {
            final JSONObject jo = new JSONObject(jsonContext);
            final JSONObject route = jo.getJSONArray("routes").getJSONObject(0);
            Log.d(clazz, "Reading the overview way point data");
            String poly = route.getJSONObject("overview_polyline")
                    .getString("points");
            Log.d(clazz, "Overview way points encoded: " + poly);

            Log.d(clazz, "Fetching the bounds");
            final JSONObject bounds = route.getJSONObject("bounds");
            double lat = Double.parseDouble(
                    bounds.getJSONObject("northeast").getString("lat"));
            double lng = Double.parseDouble(
                    bounds.getJSONObject("northeast").getString("lng"));
            Log.d(clazz, "Northeast: " + lat + "," + lng);
            northeast = new LatLng(lat, lng);
            lat = Double.parseDouble(
                    bounds.getJSONObject("southwest").getString("lat"));
            lng = Double.parseDouble(
                    bounds.getJSONObject("southwest").getString("lng"));
            Log.d(clazz, "Southwest: " + lat + "," + lng);
            southwest = new LatLng(lat, lng);

            return decodeWaypoints(poly);
        } catch (JSONException je) {
            Log.e(clazz, "Parsing JSON data failed", je);
        }
        return null;
    }


    private final static String clazz =
            DirectionJSONParser.class.getSimpleName();
    private URL mapAPI;
    private String json;
    private LatLng northeast;
    private LatLng southwest;


    /**
     * Build the Google Map Direction API address
     *
     * @param context
     * @param src
     * @param dest
     * @return
     */
    static String getAPIAddress(Context context, LatLng src, LatLng dest) {
        Log.d(clazz, "Building Google Maps Direction API URL");
        StringBuilder url = new StringBuilder();
        url.append(context.getString(R.string.dir_api_base_url));
        url.append("?origin=");
        url.append(String.valueOf(src.latitude));
        url.append(",");
        url.append(String.valueOf(src.longitude));
        url.append("&destination=");
        url.append(String.valueOf(dest.latitude));
        url.append(",");
        url.append(String.valueOf(dest.longitude));
        url.append("&sensor=false");
        url.append("&units=imperial");
        url.append("&mode=");
        url.append(context.getString(R.string.dir_route_mode));
        url.append("&alternatives=false");
        url.append("&key=");
        url.append(context.getString(R.string.google_direction_server_key));
        Log.d(clazz, "URL: " + url.toString());
        return url.toString();
    }


    /**
     * Decode and translate a polyline(Google Maps) into a list of locations.
     * Special thanks to http://stackoverflow.com/questions/14702621/answer-draw-path-between-two-points-using-google-maps-android-api-v2
     *
     * @param polyEncoded
     * @return
     */
    static List<LatLng> decodeWaypoints(String polyEncoded) {
        Log.d(clazz, "Decoding way points");
        List<LatLng> poly = new ArrayList<>();
        int index = 0, len = polyEncoded.length();
        int lat = 0, lng = 0;

        while (index < len) {
            int b, shift = 0, result = 0;
            do {
                b = polyEncoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlat = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lat += dlat;

            shift = 0;
            result = 0;
            do {
                b = polyEncoded.charAt(index++) - 63;
                result |= (b & 0x1f) << shift;
                shift += 5;
            } while (b >= 0x20);
            int dlng = ((result & 1) != 0 ? ~(result >> 1) : (result >> 1));
            lng += dlng;

            Log.d(clazz, "Way point: " + ((double)lat / 1E5) + "," +
                    ((double)lng / 1E5));
            LatLng p = new LatLng(((double)lat / 1E5),
                    ((double)lng / 1E5));
            poly.add(p);
            Log.d(clazz, "Way point added");
        }
        Log.d(clazz, "Total way points: " + poly.size());
        return poly;
    }
}
