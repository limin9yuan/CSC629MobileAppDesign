//------------------------------------------------------------------------------
//  File       : Top10CrossChecker.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 03/12/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.songscharttop10;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

public class Top10CrossChecker extends AsyncTask<String, Void,
        ArrayList<EntryParser.Entry>> {


    public ListView getDisplayView() {
        return top10;
    }

    public void setDisplayView(ListView top10) {
        this.top10 = top10;
    }

    public boolean isDisplay() {
        return display;
    }

    public void setDisplay(boolean display) {
        this.display = display;
    }


    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    @Override
    protected ArrayList<EntryParser.Entry> doInBackground(String... params) {
        if (params.length != 2) {
            Log.e(clazz, "2 urls needed, " + params.length + " " +
                    "specified");
        }

        Log.d(clazz, "URL 0: " + params[0]);
        Log.d(clazz, "URL 1: " + params[1]);

        final String albumsUrl = params[0];
        final String songsUrl = params[1];

        try {
            XMLDownloader downloader = new XMLDownloader();

            final String albums = downloader.downloadXML(albumsUrl);
            final String songs = downloader.downloadXML(songsUrl);

            EntryParser songParser = new EntryParser(songs);
            songParser.parse();

            EntryParser albumParser = new EntryParser(albums);
            albumParser.parse();

            crossCheck(songParser.getEntries(), albumParser.getEntries());
        } catch (Exception e) {
            Log.e(clazz, "Unexpected exception", e);
            Log.d(clazz, "Exception happened");
            Log.d(clazz, "params[0]: " + params[0]);
            Log.d(clazz, "params[1]: " + params[1]);
        }
        return resultList;
    }


    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        Log.d(clazz, "Start cross checking");
    }

    @Override
    protected void onPostExecute(ArrayList<EntryParser.Entry> entries) {
        super.onPostExecute(entries);
        // set the list view
        showAlbums();

        // logging
        for (EntryParser.Entry entry : entries) {
            Log.d(clazz, "-----------------------");
            Log.d(clazz, "Name: " + entry.getName());
            Log.d(clazz, "Artist: " + entry.getArtist());
            Log.d(clazz, "Album: " + entry.getAlbum());
            Log.d(clazz, "Release Date: " + entry.getReleaseDate());
            Log.d(clazz, "Song in Top 10: " + entry.getTop10Songs());
            Log.d(clazz, "-----------------------");
        }
    }

    private void showAlbums() {
        if (context != null && top10 != null && true == display) {
            ArrayAdapter<EntryParser.Entry> adapter =
                    new ArrayAdapter<>(context, R.layout.list_item, resultList);
            top10.setAdapter(adapter);
        }
    }

    private ArrayList<EntryParser.Entry> crossCheck(
            ArrayList<EntryParser.Entry> left,
            ArrayList<EntryParser.Entry> right) {

        if (left == null || right == null)
            throw new NullPointerException(
                    "One or both of the entry lists is/are null");

        ArrayList<EntryParser.Entry> result = new ArrayList<>();

        boolean hasSongInTop10;
        if (left.get(0).getAlbum().equals("")) {
            // left is top 10 albums
            for (EntryParser.Entry album : left) {
                hasSongInTop10 = false;
                for (EntryParser.Entry song : right) {
                    if (song.getAlbum().equalsIgnoreCase(album.getName())) {
                        hasSongInTop10 = true;
                        album.addTop10Song(song.getName());
                    }
                }
                if (true == hasSongInTop10)
                    result.add(album);
            }
        } else {
            // left is top 10 songs
            for (EntryParser.Entry album : right) {
                hasSongInTop10 = false;
                for (EntryParser.Entry song : left) {
                    if (song.getAlbum().equalsIgnoreCase(album.getName())) {
                        hasSongInTop10 = true;
                        album.addTop10Song(song.getName());
                    }
                }
                if (true == hasSongInTop10)
                    result.add(album);
            }
        }

        resultList = result;

        return result;
    }

    private final String clazz = getClass().getSimpleName();
    private ListView top10 = null;
    private Context context = null;
    private boolean display = false;
    private ArrayList<EntryParser.Entry> resultList;
}
