//------------------------------------------------------------------------------
//  File       : MapDirection.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 03/18/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.googlemapapp;

import android.content.Context;
import android.graphics.Color;
import android.location.Address;
import android.os.AsyncTask;
import android.util.Log;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.*;

import java.util.List;


/**
 * This class provides methods and async methods to calculate and draw routes on
 * map between locations.
 */
class MapDirection extends AsyncTask<Address, Void, List<LatLng>> {

    MapDirection(GoogleMap map, Context context) {
        this.map = map;
        this.context = context;
    }


    /**
     * Get the direction on the map. If an async task is wanted, use:
     * <pre><code>
     *  MapUtils mu = new MapUtils(map, context);
     *  mu.execute(src, dest);
     * </code></pre>
     *
     * @param src
     * @param dest
     * @see #doInBackground(Address...)
     * @see #execute(Object[])
     */
    List<LatLng> getDirection(Address src, Address dest) {
        try {
            if (dest == null)
                throw new IllegalArgumentException(
                        new NullPointerException("Destination is null"));
            if (src == null)
                throw new IllegalArgumentException(
                        new NullPointerException("source is null"));
            LatLng d = new LatLng(dest.getLatitude(), dest.getLongitude());
            LatLng s = new LatLng(src.getLatitude(), src.getLongitude());
            return getDirection(s, d);
        } catch (Exception e){
            Log.e(clazz, "Unexpected exception", e);
        }
        return null;
    }


    List<LatLng> getDirection(LatLng src, LatLng dest) {
        String url = DirectionJSONParser.getAPIAddress(context, src, dest);
        DirectionJSONParser parser = new DirectionJSONParser(url);
        List<LatLng> wp = parser.parse();
        northeast = parser.boundNortheast();
        southwest = parser.boundSouthwest();
        return wp;
    }


    void drawDirection(List<LatLng> wayPoints) {
        Polyline line = map.addPolyline(new PolylineOptions()
                        .addAll(wayPoints)
                        .width(12)
                        .color(Color.parseColor(
                                context.getString(
                                        R.string.google_map_color_blue)))
                        .geodesic(true)
        );
    }


    void moveCameraWithBounds() {
        LatLngBounds bounds = new LatLngBounds(southwest, northeast);

//        CameraUpdate camUpdate = CameraUpdateFactory.newLatLngZoom(
//                bounds.getCenter(), (float)zoomIn);
        CameraUpdate camUpdate =
                CameraUpdateFactory.newLatLngBounds(bounds, 10);
        map.animateCamera(camUpdate);
    }


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute} by the
     * caller of this task.
     * <p/>
     * This method can call {@link #publishProgress} to publish updates on the
     * UI thread.
     *
     * @param params The parameters of the task.
     * @return A result, defined by the subclass of this task.
     * @see #onPreExecute()
     * @see #onPostExecute
     * @see #publishProgress
     */
    @Override
    protected List<LatLng> doInBackground(Address... params) {
        if (params.length != 2) {
            Log.e(clazz,
                    "Either the source or the destination location is not given");
            return null;
        }

        try {
            return wayPoints = getDirection(params[0], params[1]);
        } catch (Exception e) {
            Log.e(clazz, "Unexpected exception happened", e);
        }
        return wayPoints = null;
    }


    /**
     * Runs on the UI thread before {@link #doInBackground}.
     *
     * @see #onPostExecute
     * @see #doInBackground
     */
    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(clazz, "Start drawing direction");
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The specified
     * result is the value returned by {@link #doInBackground}.</p> <p/> <p>This
     * method won't be invoked if the task was cancelled.</p>
     *
     * @param wayPoints The result of the operation computed by {@link
     *                  #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(List<LatLng> wayPoints) {
        super.onPostExecute(wayPoints);
        Log.d(clazz, "Post exec: drawing the direction");
        if (wayPoints == null || wayPoints.size() == 0) {
            Log.d(clazz, "No way points received");
            return;
        }

        drawDirection(wayPoints);
        moveCameraWithBounds();
    }


    private GoogleMap map;
    private Context context;
    private List<LatLng> wayPoints;
    private LatLng northeast;
    private LatLng southwest;
    private final String clazz = getClass().getSimpleName();
}
