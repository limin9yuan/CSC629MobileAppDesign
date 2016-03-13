package net.android.jason.songscharttop10;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;


public class FrontpageActivity extends AppCompatActivity {

    private ListView top10ListView;
    private String songs;
    private String albums;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frontpage);
        top10ListView = (ListView)findViewById(R.id.top10ListView);
        setListView();
    }


    private String downloadTop10Songs() {
        return downloadData(
                getResources().getString(R.string.apple_top10_songs_url),
                false, null);
    }


    private String downloadTop10Albums() {
        return downloadData(
                getResources().getString(R.string.apple_top10_album_url),
                false, null);
    }


    private String downloadData(String url, boolean display,
                                TextView displayView) {
        XMLDownloader downloader = new XMLDownloader();
        downloader.setDisplay(display);
        downloader.setDisplayView(displayView);
        downloader.execute(url);
        return downloader.getXmlData();
    }


    private void setListView() {
        Top10CrossChecker top10 = new Top10CrossChecker();
        top10.setDisplay(true);
        top10.setContext(FrontpageActivity.this);
        top10.setDisplayView(top10ListView);
        top10.execute(getResources().getString(R.string.apple_top10_album_url),
                getResources().getString(R.string.apple_top10_songs_url));
    }

}
