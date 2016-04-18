package net.android.jason.flickerbrowser;

import android.content.Intent;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;


public class ViewPhotoDetailsActivity extends BaseActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photo_details);
        activateToolbarWithHomeEnabled();

        Intent intent = getIntent();
        int id = intent.getIntExtra(PHOTO_ID, 0);
        PhotoLoader loader = new PhotoLoader();
        loader.execute(id);
    }

    private class PhotoLoader extends AsyncTask<Integer, Void, Photo> {

        /**
         * <p>Runs on the UI thread after {@link #doInBackground}. The specified
         * result is the value returned by {@link #doInBackground}.</p> <p/>
         * <p>This method won't be invoked if the task was cancelled.</p>
         *
         * @param photo The result of the operation computed by {@link
         *              #doInBackground}.
         * @see #onPreExecute
         * @see #doInBackground
         * @see #onCancelled(Object)
         */
        @Override
        protected void onPostExecute(Photo photo) {
            super.onPostExecute(photo);
            ImageView photoImage = (ImageView)findViewById(R.id.photo_image);
            photoImage.setImageBitmap(
                    BitmapUtility.getImage(photo.getPicture()));
            TextView photoTitle = (TextView)findViewById(R.id.photo_title);
            photoTitle.setText("Title: " + photo.getTitle());

            TextView photoTags = (TextView)findViewById(R.id.photo_tags);
            photoTags.setText("Tags: " + photo.getTags());

            TextView photoAuthor = (TextView)findViewById(R.id.photo_author);
            photoAuthor.setText(photo.getAuthor());
        }

        /**
         * Override this method to perform a computation on a background thread.
         * The specified parameters are the parameters passed to {@link
         * #execute} by the caller of this task.
         * <p/>
         * This method can call {@link #publishProgress} to publish updates on
         * the UI thread.
         *
         * @param params The parameters of the task.
         * @return A result, defined by the subclass of this task.
         * @see #onPreExecute()
         * @see #onPostExecute
         * @see #publishProgress
         */
        @Override
        protected Photo doInBackground(Integer... params) {
            FlickrDatabaseHelper helper = new FlickrDatabaseHelper(getBaseContext());
            helper.connectForRead();
            Cursor cur =  helper.fetchPhoto(params[0]);
            Photo p = Photo.getInstance(cur);
            cur.close();
            helper.disconnect();
            return p;
        }
    }

}
