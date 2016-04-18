package net.android.jason.flickerbrowser;

import android.content.Context;
import android.net.Uri;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import android.database.Cursor;

/**
 * Created by dev on 17/02/2016.
 */
public class GetFlickrJsonData extends GetRawData {

    private String LOG_TAG = GetFlickrJsonData.class.getSimpleName();
    //private List<Photo> mPhotos;
    private Uri mDestinationUri;
    private Context context;

    public GetFlickrJsonData(Context context, String searchCriteria,
                             boolean matchAll) {
        super(null);
        createAndUpdateUri(searchCriteria, matchAll);
        //mPhotos = new ArrayList<>();
        this.context = context;
    }

    public void execute() {
        super.setmRawUrl(mDestinationUri.toString());
        DownloadJsonData downloadJsonData = new DownloadJsonData();
        Log.v(LOG_TAG, "Built URI = " + mDestinationUri.toString());
        downloadJsonData.execute(mDestinationUri.toString());
    }

    public boolean createAndUpdateUri(String searchCriteria, boolean matchAll) {
        final String FLICKR_API_BASE_URL = "https://api.flickr.com/services/feeds/photos_public.gne";
        final String TAGS_PARAM = "tags";
        final String TAGMODE_PARAM = "tagmode";
        final String FORMAT_PARAM = "format";
        final String NO_JSON_CALLBACK_PARAM = "nojsoncallback";

        mDestinationUri = Uri.parse(FLICKR_API_BASE_URL).buildUpon()
                .appendQueryParameter(TAGS_PARAM, searchCriteria)
                .appendQueryParameter(TAGMODE_PARAM, matchAll ? "ALL" : "ANY")
                .appendQueryParameter(FORMAT_PARAM, "json")
                .appendQueryParameter(NO_JSON_CALLBACK_PARAM, "1")
                .build();

        return mDestinationUri != null;
    }

    public List<Photo> getPhotos() {
        List<Photo> photos = new ArrayList<>();
        FlickrDatabaseHelper helper = new FlickrDatabaseHelper(context);
        helper.connectForRead();
        Cursor cur = helper.fetchAll();
        while (!cur.isLast()) {
            photos.add(Photo.getInstance(cur));
            cur.moveToNext();
        }
        cur.close();
        helper.disconnect();
        return photos;
    }

    public void processResult() {

        if (getmDownloadStatus() != DownloadStatus.OK) {
            Log.e(LOG_TAG, "Error downloading raw file");
            return;
        }

        final String FLICKR_ITEMS = "items";
        List<Photo> mPhotos = new ArrayList<>();
        try {
            JSONObject jsonData = new JSONObject(getmData());
            JSONArray itemsArray = jsonData.getJSONArray(FLICKR_ITEMS);
            for (int i = 0; i < itemsArray.length(); i++) {
                JSONObject jsonPhoto = itemsArray.getJSONObject(i);
                Photo photoObject = Photo.getInstance(jsonPhoto);
                mPhotos.add(photoObject);
            }
        } catch (JSONException jsone) {
            jsone.printStackTrace();
            Log.e(LOG_TAG, "Error processing Json data");
        }

        FlickrDatabaseHelper helper = new FlickrDatabaseHelper(context);
        helper.connectForUpdate();
        for (Photo singlePhoto : mPhotos) {
            Log.v(LOG_TAG, singlePhoto.toString());

            // download image
            HttpURLConnection conn = null;
            InputStream is = null;
            try {
                URL url = new URL(singlePhoto.getImage());
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
                Log.e(LOG_TAG, "Failed to download image", ioe);
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

            Log.d(LOG_TAG, "Saving into DB");
            helper.insertPhoto(singlePhoto);
        }
        helper.disconnect();

    }


    public class DownloadJsonData extends DownloadRawData {

        protected void onPostExecute(String webData) {
            super.onPostExecute(webData);
            processResult();

        }

        protected String doInBackground(String... params) {
            String[] par = {mDestinationUri.toString()};
            return super.doInBackground(par);
        }

    }

}
