//------------------------------------------------------------------------------
//  File       : FlickrDatabaseHelper.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 04/17/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.flickerbrowser;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FlickrDatabaseHelper {

    public FlickrDatabaseHelper(Context context) {
        dbName = context.getString(R.string.database_name);
        helper = new FlickrDatabaseOpenHelper(context, dbName);
    }


    public Cursor fetchAll() {
        String[] columns = new String[9];
        columns[0] = PhotoContract.FlickrEntry._ID;
        columns[1] = PhotoContract.FlickrEntry._TITLE;
        columns[2] = PhotoContract.FlickrEntry._AUTHOR_ID;
        columns[3] = PhotoContract.FlickrEntry._AUTHOR;
        columns[4] = PhotoContract.FlickrEntry._THUMB_LINK;
        columns[5] = PhotoContract.FlickrEntry._LINK;
        columns[6] = PhotoContract.FlickrEntry._TAGS;
        columns[7] = PhotoContract.FlickrEntry._THUMBNAIL;
        columns[8] = PhotoContract.FlickrEntry._PICTURE;

        connectForRead();
        Cursor cur = db.query(PhotoContract.FlickrEntry.TABLE_NAME,
                columns,
                null,
                null,
                null,
                null,
                "_id"
        );
        if (!cur.moveToFirst())
            Log.e(clazz, "Query all returns empty RS");
        return cur;
    }

    public Cursor fetchPhoto(int id) {
        String[] columns = new String[9];
        columns[0] = PhotoContract.FlickrEntry._ID;
        columns[1] = PhotoContract.FlickrEntry._TITLE;
        columns[2] = PhotoContract.FlickrEntry._AUTHOR_ID;
        columns[3] = PhotoContract.FlickrEntry._AUTHOR;
        columns[4] = PhotoContract.FlickrEntry._THUMB_LINK;
        columns[5] = PhotoContract.FlickrEntry._LINK;
        columns[6] = PhotoContract.FlickrEntry._TAGS;
        columns[7] = PhotoContract.FlickrEntry._THUMBNAIL;
        columns[8] = PhotoContract.FlickrEntry._PICTURE;

        connectForRead();
        Cursor cur = db.query(PhotoContract.FlickrEntry.TABLE_NAME,
                columns,
                PhotoContract.FlickrEntry._ID + " = ?",
                new String[]{String.valueOf(id)},
                null,
                null,
                null,
                "1"
        );
        if (!cur.moveToFirst())
            Log.e(clazz, "Query returns empty RS, id=" + id);
        return cur;
    }

    public Cursor fetchPhoto(String title, String authorId) {
        String[] columns = new String[9];
        columns[0] = PhotoContract.FlickrEntry._ID;
        columns[1] = PhotoContract.FlickrEntry._TITLE;
        columns[2] = PhotoContract.FlickrEntry._AUTHOR_ID;
        columns[3] = PhotoContract.FlickrEntry._AUTHOR;
        columns[4] = PhotoContract.FlickrEntry._THUMB_LINK;
        columns[5] = PhotoContract.FlickrEntry._LINK;
        columns[6] = PhotoContract.FlickrEntry._TAGS;
        columns[7] = PhotoContract.FlickrEntry._THUMBNAIL;
        columns[8] = PhotoContract.FlickrEntry._PICTURE;

        StringBuffer where = new StringBuffer();
        where.append(PhotoContract.FlickrEntry._TITLE);
        where.append(" = ?");
        where.append(" AND ");
        where.append(PhotoContract.FlickrEntry._AUTHOR_ID);
        where.append(" = ?");

        connectForRead();
        Cursor cur = db.query(
                PhotoContract.FlickrEntry.TABLE_NAME,
                columns,
                where.toString(),
                new String[]{title, authorId},
                null,
                null,
                "1"
        );
        if (!cur.moveToFirst())
            Log.e(clazz,
                    "Query returns empty RS, title=" + title + ", authorId="
                            + authorId);
        return cur;
    }


    public long insertPhoto(Photo photo) {
        if (photo.isPersisted())
            return -1;

        ContentValues values = new ContentValues();
        values.put(PhotoContract.FlickrEntry._TITLE, photo.getTitle());
        values.put(PhotoContract.FlickrEntry._AUTHOR, photo.getAuthor());
        values.put(PhotoContract.FlickrEntry._AUTHOR_ID, photo.getAuthorId());
        values.put(PhotoContract.FlickrEntry._THUMB_LINK, photo.getImage());
        values.put(PhotoContract.FlickrEntry._LINK, photo.getLink());
        values.put(PhotoContract.FlickrEntry._TAGS, photo.getTitle());
        values.put(PhotoContract.FlickrEntry._THUMBNAIL, photo.getThumbnail());
        values.put(PhotoContract.FlickrEntry._PICTURE, photo.getPicture());

        connectForUpdate();
        return helper.getWritableDatabase().insert(
                PhotoContract.FlickrEntry.TABLE_NAME,
                null,
                values);
    }


    public SQLiteDatabase connectForRead() {
        if (db != null && !db.isReadOnly())
            db.close();
        if (db == null || !db.isOpen())
            db = helper.getReadableDatabase();
        return db;
    }


    public SQLiteDatabase connectForUpdate() {
        if (db != null && db.isReadOnly())
            db.close();
        if (db == null || !db.isOpen())
            db = helper.getWritableDatabase();
        return db;
    }


    public void disconnect() {
        if (db != null && db.isOpen())
            db.close();
    }


    private SQLiteDatabase db;
    private String dbName;
    private SQLiteOpenHelper helper;
    private static final String clazz =
            FlickrDatabaseHelper.class.getSimpleName();
}
