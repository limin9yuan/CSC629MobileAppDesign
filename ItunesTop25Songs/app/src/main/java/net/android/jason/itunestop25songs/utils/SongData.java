//------------------------------------------------------------------------------
//  File       : SongData.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 03/29/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.itunestop25songs.utils;


import android.content.res.Resources;
import android.util.Log;
import net.android.jason.itunestop25songs.R;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.MalformedURLException;
import java.net.URL;


/**
 * A class which holds the key properties of a song fetched from Itunes RSS
 * feed.
 */
public class SongData {
    public URL getCoverSmall() {
        return coverSmall;
    }

    public void setCoverSmall(String coverSmall) {
        try {
            this.coverSmall = new URL(coverSmall);
        } catch (MalformedURLException e) {
            Log.d(CLAZZ, "Not a good URL of small cover picture");
            Log.e(CLAZZ, "Bad URL of cover small", e);
        }
    }

    public URL getCoverMid() {
        return coverMid;
    }

    public void setCoverMid(String coverMid) {
        try {
            this.coverMid = new URL(coverMid);
        } catch (MalformedURLException e) {
            Log.d(CLAZZ, "Not a good URL of small cover picture");
            Log.e(CLAZZ, "Bad URL of cover small", e);
        }
    }

    public URL getCoverLarge() {
        return coverLarge;
    }

    public void setCoverLarge(String coverLarge) {
        try {
            this.coverLarge = new URL(coverLarge);
        } catch (MalformedURLException e) {
            Log.d(CLAZZ, "Not a good URL of small cover picture");
            Log.e(CLAZZ, "Bad URL of cover small", e);
        }
    }

    public String getArtist() {
        return artist;
    }

    public void setArtist(String artist) {
        this.artist = artist;
    }

    public String getAlbum() {
        return album;
    }

    public void setAlbum(String album) {
        this.album = album;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getReleaseDate() {
        return releaseDate;
    }

    public void setReleaseDate(String releaseDate) {
        this.releaseDate = releaseDate;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getCopyright() {
        return copyright;
    }

    public void setCopyright(String copyright) {
        this.copyright = copyright;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }


    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Title: %s, ", title));
        builder.append(String.format("Name: %s, ", name));
        builder.append(String.format("Artist: %s, ", artist));
        builder.append(String.format("Genre: %s, ", genre));
        builder.append(String.format("Price: %s, ", price));
        builder.append(String.format("Release Date: %s, ", releaseDate));
        builder.append(String.format("Album: %s, ", album));
        builder.append(String.format("Copyright: %s, ", copyright));
        builder.append(String.format("Cover Small: %s, ",
                coverSmall.toString()));
        builder.append(String.format("Cover Mid: %s, ",
                coverMid.toString()));
        builder.append(String.format("Cover Large: %s",
                coverLarge.toString()));
        return builder.toString();
    }


    /**
     * Returns a {@code String} representation of this object. This
     * representation contains only a song's name, artist, price, release date
     * and album.
     *
     * @return Returns a {@code String} representation of this object.
     */
    public String toCompactString() {
        StringBuilder builder = new StringBuilder();
        builder.append(String.format("Name: %s, ", name));
        builder.append(String.format("Artist: %s, ", artist));
        builder.append(String.format("Price: %s, ", price));
        builder.append(String.format("Release Date: %s, ", releaseDate));
        builder.append(String.format("Album: %s", album));
        return builder.toString();
    }


    public static SongData createInstance(JSONObject entry,
                                          Resources res) {
        try {
            String album =
                    entry.getJSONObject(
                            res.getString(R.string.rss_key_album))
                            .getJSONObject(
                                    res.getString(
                                            R.string.rss_key_album_name))
                            .getString(
                                    res.getString(R.string.rss_key_value));
            String name =
                    entry.getJSONObject(
                            res.getString(R.string.rss_key_song_name))
                            .getString(
                                    res.getString(R.string.rss_key_value));
            String artist =
                    entry.getJSONObject(
                            res.getString(R.string.rss_key_artist))
                            .getString(
                                    res.getString(R.string.rss_key_value));
            String title =
                    entry.getJSONObject(
                            res.getString(R.string.rss_key_title))
                            .getString(
                                    res.getString(R.string.rss_key_value));
            String price =
                    entry.getJSONObject(
                            res.getString(R.string.rss_key_price))
                            .getString(
                                    res.getString(R.string.rss_key_value));
            String copyright =
                    entry.getJSONObject(
                            res.getString(R.string.rss_key_copyright))
                            .getString(
                                    res.getString(R.string.rss_key_value));
            String genre =
                    entry.getJSONObject(
                            res.getString(R.string.rss_key_genre))
                            .getJSONObject(
                                    res.getString(
                                            R.string.rss_key_genre_attr))
                            .getString(
                                    res.getString(R.string.rss_key_value));
            String release =
                    entry.getJSONObject(
                            res.getString(R.string.rss_key_release_date))
                            .getJSONObject(
                                    res.getString(
                                            R.string.rss_key_release_date_attr))
                            .getString(
                                    res.getString(R.string.rss_key_value));
            String smallCover =
                    entry.getJSONArray(
                            res.getString(R.string.rss_key_covers))
                            .getJSONObject(0)
                            .getString(
                                    res.getString(R.string.rss_key_value));
            String midCover =
                    entry.getJSONArray(
                            res.getString(R.string.rss_key_covers))
                            .getJSONObject(1)
                            .getString(
                                    res.getString(R.string.rss_key_value));
            String bigCover =
                    entry.getJSONArray(
                            res.getString(R.string.rss_key_covers))
                            .getJSONObject(2)
                            .getString(
                                    res.getString(R.string.rss_key_value));

            SongData data = new SongData();
            data.setAlbum(album);
            data.setReleaseDate(release);
            data.setArtist(artist);
            data.setName(name);
            data.setCopyright(copyright);
            data.setCoverLarge(bigCover);
            data.setCoverMid(midCover);
            data.setCoverSmall(smallCover);
            data.setGenre(genre);
            data.setPrice(price);
            data.setTitle(title);
            return data;
        } catch (JSONException je) {
            Log.d(CLAZZ, "Failed to get JSON object");
            Log.e(CLAZZ, "Unexpected exception happened", je);
        }
        return null;
    }


    private URL coverSmall;
    private URL coverMid;
    private URL coverLarge;
    private String artist;//
    private String album; //
    private String title; //
    private String releaseDate;//
    private String price; //
    private String copyright;
    private String name; //
    private String genre; //


    private static final String CLAZZ = SongData.class.getSimpleName();
}
