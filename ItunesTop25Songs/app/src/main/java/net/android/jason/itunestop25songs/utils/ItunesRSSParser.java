//------------------------------------------------------------------------------
//  File       : ItunesRSSParser.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 04/01/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.itunestop25songs.utils;

import android.content.res.Resources;
import android.os.AsyncTask;
import android.util.Log;
import net.android.jason.itunestop25songs.R;
import org.json.JSONArray;
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

public class ItunesRSSParser extends AsyncTask<String, Void, List<SongData>> {

    public ItunesRSSParser(Resources res) {
        if (res == null)
            throw new IllegalArgumentException(
                    new NullPointerException("Resources object is null"));
        this.res = res;
        state = ProcessStatus.IDLE;
    }


    public ProcessStatus getState() {
        return state;
    }


    public List<SongData> getSongs() {
        return songs;
    }


    public String getRawData() {
        return raw;
    }


    public void reset() {
        state = ProcessStatus.IDLE;
        raw = null;
        songs = null;
    }


    /**
     * Override this method to perform a computation on a background thread. The
     * specified parameters are the parameters passed to {@link #execute} by the
     * caller of this task.
     * <p>
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
    protected List<SongData> doInBackground(String... params) {
        if (params == null || params.length != 1) {
            Log.d(clazz, "No URL received");
            return null;
        }
        Log.d(clazz, "Downloading: " + state.toString());
        try {
            URL url = new URL(params[0]);
            return parse(download(url));
        } catch (MalformedURLException me) {
            Log.e(clazz, "Bad URL: " + params[0], me);
        }
        return null;
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
        Log.d(clazz, "PRE Parsing");
    }

    /**
     * <p>Runs on the UI thread after {@link #doInBackground}. The specified
     * result is the value returned by {@link #doInBackground}.</p> <p> <p>This
     * method won't be invoked if the task was cancelled.</p>
     *
     * @param songDatas The result of the operation computed by {@link
     *                  #doInBackground}.
     * @see #onPreExecute
     * @see #doInBackground
     * @see #onCancelled(Object)
     */
    @Override
    protected void onPostExecute(List<SongData> songDatas) {
        super.onPostExecute(songDatas);
        songs = songDatas;

        if (isCancelled())
            state = ProcessStatus.CANCELLED;
        else if (songs == null)
            state = ProcessStatus.FAILED_EMPTY;
        else if (raw == null)
            state = ProcessStatus.NOT_INITIALIZED;
        else {
            state = ProcessStatus.SUCCESS;
            Log.d(clazz, "Parsed songs: Count = " + songs.size());
            Log.d(clazz, "************* Parsed song objects:");
            for (SongData song: songs)
                Log.d(clazz, song.toString());
        }
        Log.d(clazz, "Parsing: " + state.toString());
    }


    protected List<SongData> parse(final String rawJSON) {
        if (rawJSON == null || rawJSON.equals("")) {
            Log.d(clazz, "No JSON data");
            return null;
        }
        raw = rawJSON;
        try {
            state = ProcessStatus.PARSING;
            JSONObject jo = new JSONObject(raw).getJSONObject(
                    res.getString(R.string.rss_key_root));
            JSONArray entries = jo.getJSONArray(
                    res.getString(R.string.rss_key_song_entry));
            List<SongData> songs = null;
            if (entries.length() > 0) {
                songs = new ArrayList<>(entries.length());
                for (int i = 0; i < entries.length(); i++) {
                    SongData song = SongData.createInstance(
                            entries.getJSONObject(i), res);
                    if (song == null)
                        throw new JSONException("Failed to create song object");
                    songs.add(song);
                }
            }
            return songs;
        } catch (JSONException je) {
            Log.d(clazz, "Failed to load JSON object from the raw data");
            Log.e(clazz, "Unexpected exception happened", je);
        }
        return null;
    }


    /**
     * Connect to the specified URL and download the RSS feed content.
     *
     * @param url The RSS site address.
     * @return A {@code String} which represents the RSS feed data.
     */
    protected String download(final URL url) {
        HttpURLConnection conn = null;
        BufferedReader br = null;
        try {
            Log.d(clazz, "Downloading the direction JSON data");
            state = ProcessStatus.DOWNLOADING;
            StringBuilder buff = new StringBuilder();
            conn = (HttpURLConnection)url.openConnection();
            int rc = conn.getResponseCode();
            Log.d(clazz, "HTTP Response Code: " + rc);
            if (rc != 200)
                throw new IOException(
                        "Failed to connect to RSS feed. Return code: " + rc);

            br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null)
                buff.append(String.format("%s%n", line));
            Log.d(clazz, "JSON downloaded: " + buff.toString());
            return buff.toString();
        } catch (IOException ioe) {
            Log.e(clazz, "IOException: " + ioe.getMessage(), ioe);
        } catch (SecurityException se) {
            Log.e(clazz, "Security Exception: " + se.getMessage(), se);
        } finally {
            if (conn != null)
                conn.disconnect();
            if (br != null)
                try {
                    br.close();
                } catch (IOException ioe) {
                    Log.e(clazz, "Error in closing buffer reader", ioe);
                }
        }
        return null;
    }

    private List<SongData> songs;
    private ProcessStatus state;
    private Resources res;
    private String raw;
    private static final String clazz = ItunesRSSParser.class.getSimpleName();
}
