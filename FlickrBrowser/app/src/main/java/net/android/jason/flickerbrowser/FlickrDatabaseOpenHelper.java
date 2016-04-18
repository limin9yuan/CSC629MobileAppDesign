//------------------------------------------------------------------------------
//  File       : FlickrDatabaseOpenHelper.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 04/15/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.flickerbrowser;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FlickrDatabaseOpenHelper extends SQLiteOpenHelper {

    public static final int DB_VERSION = 1;

    public FlickrDatabaseOpenHelper(Context context, String dbName) {
        super(context, dbName, null, DB_VERSION);
        this.context = context;
        contract = new PhotoContract();
    }


    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(clazz, "DB " + getDatabaseName() + " on creation");
        db.execSQL(contract.createSchemaSQL());
        db.execSQL(contract.createIndexSQL());
    }


    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(clazz, "DB " + getDatabaseName() + " on upgrade");
        db.execSQL(contract.dropSchemaSQL());
        onCreate(db);
    }


    public Context getContext() {
        return context;
    }


    private Context context;
    private PhotoContract contract;

    private static final String clazz = FlickrDatabaseOpenHelper.class.getSimpleName();
}
