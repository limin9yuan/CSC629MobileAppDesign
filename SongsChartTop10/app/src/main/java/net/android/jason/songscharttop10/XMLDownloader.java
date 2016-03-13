//------------------------------------------------------------------------------
//  File       : XMLDownloader.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 03/12/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.songscharttop10;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class XMLDownloader extends AsyncTask<String, Void, String> {

    private TextView view = null;
    private boolean display = false;

    public String getXmlData() {
        return xmlData;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }


    public boolean getDisplay() {
        return display;
    }


    public void setDisplayView(TextView view) {
        this.view = view;
    }


    public TextView getDisplayView() {
        return view;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(clazz, "Start with downloading");
    }


    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        Log.d(clazz, "Downloaded: " + result);
        if (view != null && true == display && xmlData != null)
            view.setText(xmlData);
    }


    @Override
    protected String doInBackground(String... params) {
        if (params.length == 0) {
            Log.e(clazz, "No download link is specified.");
            return null;
        }

        xmlData = downloadXML(params[0].trim());
        if (xmlData == null || xmlData.trim().equals(""))
            Log.d(getClass().getSimpleName(), "Downloading failed");

        return xmlData;
    }


    String downloadXML(final String url) {
        StringBuffer buff = new StringBuffer();
        HttpURLConnection conn;
        BufferedReader br;
        try {
            URL address = new URL(url);
            conn = (HttpURLConnection)address.openConnection();
            int rc = conn.getResponseCode();
            Log.d(clazz, "HTTP Response Code: " + rc);
            br = new BufferedReader(new InputStreamReader(
                    conn.getInputStream()));
            String line;
            while ((line = br.readLine()) != null) buff.append(line);
            br.close();
            conn.disconnect();
            return buff.toString();
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


    private String xmlData = null;
    private final String clazz = getClass().getSimpleName();
}
