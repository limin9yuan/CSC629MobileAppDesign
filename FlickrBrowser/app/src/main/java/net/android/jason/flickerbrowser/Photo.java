package net.android.jason.flickerbrowser;

import android.database.Cursor;
import android.util.Log;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.Serializable;

/**
 * Created by dev on 17/02/2016.
 */
public class Photo implements Serializable {

    private static final long serialVersionUID = 1L;

    private String mTitle;
    private String mAuthor;
    private String mAuthorId;
    private String mLink;
    private String mTags;
    private String mImage;
    // database row id
    private int mId;
    // database thumbnail image
    private byte[] mImageData;
    // database full image
    private byte[] mPictureData;
    private boolean persisted;


    public static long getSerialVersionUID() {
        return serialVersionUID;
    }

    public String getTitle() {
        return mTitle;
    }

    public String getAuthor() {
        return mAuthor;
    }

    public String getAuthorId() {
        return mAuthorId;
    }

    public String getLink() {
        return mLink;
    }

    public String getTags() {
        return mTags;
    }

    public String getImage() {
        return mImage;
    }

    public byte[] getPicture() {
        return mPictureData;
    }

    public byte[] getThumbnail() {
        return mImageData;
    }

    public int getId() {
        return mId;
    }

    public boolean isPersisted() {
        return persisted;
    }


    @Override
    public String toString() {
        return "Photo{" +
                "mId='" + mId + '\'' +
                ", mTitle='" + mTitle + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mAuthorId='" + mAuthorId + '\'' +
                ", mLink='" + mLink + '\'' +
                ", mTags='" + mTags + '\'' +
                ", mImage='" + mImage + '\'' +
                ", mPersisted='" + persisted + '\'' +
                '}';
    }


    public static Photo getInstance(JSONObject photo) throws JSONException {
        String title = photo.getString(flickr_title);
        String author = photo.getString(flickr_author);
        String authorId = photo.getString(flickr_author_id);
        String tags = photo.getString(flickr_tags);

        JSONObject jsonMedia = photo.getJSONObject(flickr_media);
        String photoUrl = jsonMedia.getString(flickr_photo_url);
        String link = photoUrl.replaceFirst("_m.", "_b.");

        Photo p = new Photo();
        p.setAuthor(author);
        p.setAuthorId(authorId);
        p.setPictureLink(link);
        p.setTags(tags);
        p.setThumbnailLink(photoUrl);
        p.setTitle(title);
        p.setPersisted(false);

        return p;
    }


    public static Photo getInstance(Cursor photo) {
        try {
            Photo p = new Photo();

            // _id, _TITLE, _AUTHOR_ID, _AUTHOR, _THUMB_LINK, _LINK, _TAGS,
            //  0     1         2         3         4           5      6
            // _THUMBNAIL, _PICTURE
            //   7            8
            p.setId(photo.getInt(
                    photo.getColumnIndex(PhotoContract.FlickrEntry._ID)));
            p.setTitle(photo.getString(
                    photo.getColumnIndex(PhotoContract.FlickrEntry._TITLE)));
            p.setAuthorId(photo.getString(
                    photo.getColumnIndex(PhotoContract.FlickrEntry._AUTHOR_ID)));
            p.setAuthor(photo.getString(
                    photo.getColumnIndex(PhotoContract.FlickrEntry._AUTHOR)));
            p.setThumbnailLink(photo.getString(
                    photo.getColumnIndex(PhotoContract.FlickrEntry._THUMB_LINK)));
            p.setPictureLink(photo.getString(
                    photo.getColumnIndex(PhotoContract.FlickrEntry._LINK)));
            p.setTags(photo.getString(
                    photo.getColumnIndex(PhotoContract.FlickrEntry._TAGS)));
            p.setThumbnail(photo.getBlob(
                    photo.getColumnIndex(PhotoContract.FlickrEntry._THUMBNAIL)));
            p.setPicture(photo.getBlob(
                    photo.getColumnIndex(PhotoContract.FlickrEntry._PICTURE)));
            p.setPersisted(true);

            return p;
        } catch (Exception e) {
            Log.e(clazz, "Failed to create Photo instance", e);
        }
        return null;
    }


    protected void setId(int id) {
        mId = id;
    }

    protected void setPersisted(boolean persisted) {
        persisted = true;
    }

    protected void setThumbnail(byte[] data) {
        mImageData = data;
    }

    protected void setThumbnailLink(String thumbnailLink) {
        mImage = thumbnailLink;
    }

    protected void setTags(String tags) {
        mTags = tags;
    }

    protected void setAuthorId(String authorId) {
        mAuthorId = authorId;
    }

    protected void setAuthor(String author) {
        mAuthor = author;
    }

    protected void setPicture(byte[] data) {
        mPictureData = data;
    }

    protected void setPictureLink(String mLink) {
        this.mLink = mLink;
    }

    public void setTitle(String title) {
        mTitle = title;
    }


    // kill the outside initialization
    private Photo() {
        persisted = false;
    }

    private static final String flickr_title = "title";
    private static final String flickr_media = "media";
    private static final String flickr_photo_url = "m";
    private static final String flickr_author = "author";
    private static final String flickr_author_id = "author_id";
    private static final String flickr_tags = "tags";

    private static final String clazz = Photo.class.getSimpleName();
}
