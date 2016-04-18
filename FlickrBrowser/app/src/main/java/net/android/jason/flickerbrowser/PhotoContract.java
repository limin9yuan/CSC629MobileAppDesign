//------------------------------------------------------------------------------
//  File       : PhotoContract.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 04/15/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.flickerbrowser;


import android.provider.BaseColumns;
import android.util.Log;

/**
 * This class defines the database schema
 */
public final class PhotoContract implements SchemaContract {
    @Override
    public String createSchemaSQL() {

        /*
        CREATE TABLE flickrphoto (
            _id INTEGER NOT NULL AUTOINCREMENT,
            title TEXT NOT NULL,
            author_id TEXT NOT NULL,
            author TEXT,
            thumb_link TEXT NOT NULL,
            link TEXT NOT NULL,
            tags TEXT,
            thumbnail BLOB,
            picture BLOB
        )
         */

        StringBuffer b = new StringBuffer(20);
        String s = String.format("%1$s %2$s (", FlickrEntry.DDL_CREATE_TABLE,
                FlickrEntry.TABLE_NAME);
        b.append(s);
        s = String.format("%1$s %2$s %3$s %4$s %5$s,",
                FlickrEntry._ID, FlickrEntry.INT_TYPE, FlickrEntry.NOT_NULL,
                FlickrEntry.PRIMARY_KEY, FlickrEntry.AUTO_INCREMENTAL);
        b.append(s);
        s = String.format("%1$s %2$s %3$s,", FlickrEntry._TITLE,
                FlickrEntry.TEXT_TYPE, FlickrEntry.NOT_NULL);
        b.append(s);
        s = String.format("%1$s %2$s %3$s,", FlickrEntry._AUTHOR_ID,
                FlickrEntry.TEXT_TYPE, FlickrEntry.NOT_NULL);
        b.append(s);
        s = String.format("%1$s %2$s,", FlickrEntry._AUTHOR,
                FlickrEntry.TEXT_TYPE);
        b.append(s);
        s = String.format("%1$s %2$s %3$s,", FlickrEntry._THUMB_LINK,
                FlickrEntry.TEXT_TYPE, FlickrEntry.NOT_NULL);
        b.append(s);
        s = String.format("%1$s %2$s %3$s,", FlickrEntry._LINK,
                FlickrEntry.TEXT_TYPE, FlickrEntry.NOT_NULL);
        b.append(s);
        s = String.format("%1$s %2$s,", FlickrEntry._TAGS, FlickrEntry.TEXT_TYPE);
        b.append(s);
        s = String.format("%1$s %2$s,", FlickrEntry._THUMBNAIL,
                FlickrEntry.BLOB_TYPE);
        b.append(s);
        s = String.format("%1$s %2$s", FlickrEntry._PICTURE, FlickrEntry.BLOB_TYPE);
        b.append(s);
        b.append(")");
        final String sql = b.toString();
        Log.d(clazz, "create table: " + sql);
        return b.toString();
    }


    @Override
    public String dropSchemaSQL() {
        final String sql = String.format("%1$s %2$s", FlickrEntry.DDL_DROP_TABLE,
                FlickrEntry.TABLE_NAME);
        Log.d(clazz, "drop table: " + sql);
        return sql;
    }


    @Override
    public String createIndexSQL() {
        // CREATE INDEX photo_id_idx ON flickrphoto(title, author_id)
        final String sql =
                String.format("CREATE INDEX photo_id_idx ON %1$s(%2$s, %3$s)",
                        FlickrEntry.TABLE_NAME,
                        FlickrEntry._TITLE,
                        FlickrEntry._AUTHOR_ID);
        Log.d(clazz, "create index: " + sql);
        return sql;
    }


    public static abstract class FlickrEntry implements BaseColumns {

        /**
         * Seal this class from being instantiated
         */
        private FlickrEntry() {
        }


        public static final String TABLE_NAME;
        public static final String _TITLE;
        public static final String _AUTHOR;
        public static final String _AUTHOR_ID;
        public static final String _LINK;
        public static final String _THUMB_LINK;
        public static final String _TAGS;
        public static final String _THUMBNAIL;
        public static final String _PICTURE;

        public static final String NULL_REPLACEMENT;

        private static final String TEXT_TYPE;
        private static final String NOT_NULL;
        private static final String AUTO_INCREMENTAL;
        private static final String PRIMARY_KEY;
        private static final String INT_TYPE;
        private static final String BLOB_TYPE;

        private static final String DDL_CREATE_TABLE;
        private static final String DDL_DROP_TABLE;


        /**
         * Static initializer to ensure the static fields are initialized
         * before the first invocation comes to any one of them.
         */
        static {
            TABLE_NAME = "flickrphoto";
            _TITLE = "title";
            _AUTHOR = "author";
            _AUTHOR_ID = "author_id";
            _LINK = "link";
            _THUMB_LINK = "thumb_link";
            _TAGS = "tags";
            _THUMBNAIL = "thumbnail";
            _PICTURE = "picture";

            NULL_REPLACEMENT = "null";

            TEXT_TYPE = "TEXT";
            INT_TYPE = "INTEGER";
            BLOB_TYPE = "BLOB";
            NOT_NULL = "NOT NULL";
            AUTO_INCREMENTAL = "AUTOINCREMENT";
            PRIMARY_KEY = "PRIMARY KEY";

            DDL_CREATE_TABLE = "CREATE TABLE";
            DDL_DROP_TABLE = "DROP TABLE IF EXISTS";
        }
    }

    private static final String clazz = PhotoContract.class.getSimpleName();
}
