//------------------------------------------------------------------------------
//  File       : SongEntryViewHolder.java
//  Revision   : $Id$
//  Course     : app
//  Date       : 04/01/2016
//  Author     : Jason
//  Description: This file contains...
//------------------------------------------------------------------------------

package net.android.jason.itunestop25songs;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class SongEntryViewHolder extends RecyclerView.ViewHolder {

    protected ImageView cover;
    protected TextView title;
    protected TextView artist;
    protected TextView album;
    protected TextView price;
    protected TextView releaseDate;


    public SongEntryViewHolder(View itemView) {
        super(itemView);

        cover = (ImageView) itemView.findViewById(R.id.cover);
        title = (TextView)itemView.findViewById(R.id.title);
        artist = (TextView)itemView.findViewById(R.id.artist);
        album  =(TextView)itemView.findViewById(R.id.album);
        price = (TextView)itemView.findViewById(R.id.price);
        releaseDate = (TextView)itemView.findViewById(R.id.release);
    }
}
