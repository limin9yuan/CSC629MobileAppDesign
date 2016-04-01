//------------------------------------------------------------------------------
//  File       : MainActivity.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 03/21/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.itunestop25songs;

import android.content.res.Resources;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import net.android.jason.itunestop25songs.utils.ItunesRSSParser;
import net.android.jason.itunestop25songs.utils.SongData;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mRecyclerView = (RecyclerView)findViewById(R.id.songList);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        mRecyclerView.setHasFixedSize(true);

        // use a linear layout manager
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));

        SongManager manager = new SongManager(getResources());
        manager.execute(getString(R.string.apple_rss_songs_top_25));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    public class SongManager extends ItunesRSSParser {
        public SongManager(Resources res) {
            super(res);
        }


        @Override
        protected void onPostExecute(List<SongData> songs) {
            super.onPostExecute(songs);

            // specify an adapter (see also next example)
            TextView itemCount = (TextView)findViewById(R.id.itemCount);
            mAdapter = new ViewAdapter(MainActivity.this,
                    itemCount, getSongs());
            mRecyclerView.setAdapter(mAdapter);
        }
    }


    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mAdapter;

    private static final String clazz = MainActivity.class.getSimpleName();
}
