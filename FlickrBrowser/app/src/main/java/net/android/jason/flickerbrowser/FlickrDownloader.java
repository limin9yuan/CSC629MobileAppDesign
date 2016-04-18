//------------------------------------------------------------------------------
//  File       : FlickrDownloader.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 04/18/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.flickerbrowser;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcel;
import android.util.Log;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class FlickrDownloader extends AsyncTask<Void, Void, List<Photo>> {


    public FlickrDownloader(Context context, String searchCriteria,
                            boolean matchAll) {
        this.matchAll = matchAll;
        this.searchCriteria = searchCriteria;
        this.context = context;
        this.mPhotos = null;
    }


    public List<Photo> getPhotos() {
        return mPhotos;
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
    protected List<Photo> doInBackground(Void... params) {
        Uri uri = createAndUpdateUri(searchCriteria, matchAll);
        Log.d(clazz, "JSON url: " + uri.toString());
        List<Photo> photos = photoJsonParser(getFlickrJSON(uri.toString()));
        Log.d(clazz, ((photos == null) ? "parsing json to Photos failed" :
                "got photos: " + photos.size()));
        return persistPhotos(photos);
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The specified
     * result is the value returned by {@link #doInBackground}.</p> <p/> <p>This
     * method won't be invoked if the task was cancelled.</p>
     *
     * @param photos The result of the operation computed by {@link
     *               #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(List<Photo> photos) {
        super.onPostExecute(photos);
        mPhotos = getPersistedPhotos();
    }

    private Uri createAndUpdateUri(String searchCriteria, boolean matchAll) {
        final String FLICKR_API_BASE_URL =
                context.getString(R.string.flickr_base_uri);
        final String TAGS_PARAM =
                context.getString(R.string.flickr_param_tags);
        final String TAGMODE_PARAM =
                context.getString(R.string.flickr_param_tagmode);
        final String FORMAT_PARAM =
                context.getString(R.string.flickr_param_format);
        final String NO_JSON_CALLBACK_PARAM =
                context.getString(R.string.flickr_param_no_json_callback);

        return Uri.parse(FLICKR_API_BASE_URL).buildUpon()
                .appendQueryParameter(TAGS_PARAM, searchCriteria)
                .appendQueryParameter(TAGMODE_PARAM, matchAll ? "ALL" : "ANY")
                .appendQueryParameter(FORMAT_PARAM, "json")
                .appendQueryParameter(NO_JSON_CALLBACK_PARAM, "1")
                .build();
    }


    private String getFlickrJSON(String flickrURL) {

        if (flickrURL == null || flickrURL.trim().equals(""))
            return null;

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        try {
            URL url = new URL(flickrURL);

            urlConnection = (HttpURLConnection)url.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            InputStream inputStream = urlConnection.getInputStream();
            if (inputStream == null) {
                return null;
            }

            StringBuffer buffer = new StringBuffer();

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            return buffer.toString();

        } catch (IOException e) {
            Log.e(clazz, "Error", e);
            return null;
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (final IOException e) {
                    Log.e(clazz, "Error closing stream", e);
                }
            }
        }
    }


    private List<Photo> photoJsonParser(String rawData) {
        if (rawData == null || rawData.trim().equals(""))
            return null;

        try {
            List<Photo> photos = new ArrayList<>(20);
            JSONObject jsonData = new JSONObject(rawData);
            JSONArray itemsArray = jsonData.getJSONArray(
                    context.getString(R.string.flickr_json_items));

            for (int i = 0; i < itemsArray.length(); i++)
                photos.add(Photo.getInstance(itemsArray.getJSONObject(i)));

            return photos;
        } catch (JSONException je) {
            Log.e(clazz, "Error processing Json data", je);
        }
        return null;
    }


    private List<Photo> persistPhotos(List<Photo> photos) {

        FlickrDatabaseHelper helper = new FlickrDatabaseHelper(context);
        helper.connectForUpdate();
        for (Photo singlePhoto : photos) {
            Log.v(clazz, singlePhoto.toString());

            // download image
            HttpURLConnection conn = null;
            InputStream is = null;
            try {
                URL url = new URL(singlePhoto.getImage());
                Log.d(clazz, "Downloading thumb: " + url.toString());
                conn = (HttpURLConnection)url.openConnection();

                conn.connect();
                is = conn.getInputStream();
                singlePhoto.setThumbnail(BitmapUtility.readBytes(is));
                is.close();
                conn.disconnect();

                url = new URL(singlePhoto.getLink());
                conn = (HttpURLConnection)url.openConnection();

                conn.connect();
                is = conn.getInputStream();
                singlePhoto.setPicture(BitmapUtility.readBytes(is));
                is.close();
                conn.disconnect();
            } catch (Exception ioe) {
                Log.e(clazz, "Failed to download image", ioe);
            } finally {
                try {
                    if (conn != null)
                        conn.disconnect();
                    if (is != null)
                        is.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            Log.d(clazz, "Saving into DB");
            helper.insertPhoto(singlePhoto);
        }
        helper.disconnect();
        return photos;
    }


    public List<Photo> getPersistedPhotos() {
        List<Photo> photos = null;
        FlickrDatabaseHelper helper =
                new FlickrDatabaseHelper(context);
        helper.connectForRead();
        Cursor cur = helper.fetchAll();
        if (cur.moveToFirst()) {
            photos = new ArrayList<>(20);
            while (!cur.isAfterLast()) {
                photos.add(Photo.getInstance(cur));
                cur.moveToNext();
            }
        }
        cur.close();
        helper.disconnect();
        return photos;
    }


    private Context context;
    private String searchCriteria;
    private boolean matchAll;
    private List<Photo> mPhotos;

    private static final String clazz = FlickrDownloader.class.getSimpleName();
}
