package net.android.jason.helloworld;

import android.app.AlertDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private String newText = "The new topic: How many words you can say in one breath?";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        final Button changeTextButton = (Button)findViewById(R.id.changeTopic);
        changeTextButton.setOnClickListener(new View.OnClickListener() {
            public void onClick (View view){
                changeTopicText(view, newText);
            }
        });
    }

    /**
     * Change the text displayed by the text view. This method can only be invoked
     * By an event handler.
     *
     * @param v The view which represents the component that triggered the event.
     */
    private void changeTopicText(View v, String whatever) {
        //if (v == null) showAlert("View is null");
        //else showAlert("Got the button view");
        // here it's not v.findViewById because param v stands for the button
        showAlert("Text will be changed to:\n" + whatever);
        final TextView topic = (TextView)findViewById(R.id.topic);
        //if (topic == null) showAlert("Failed to get TextView");
        //else showAlert("Got the text view!");
        topic.setText(whatever);
    }

    /**
     * Construct a pop-up alert message box.
     *
     * @param msg The message text that should be displayed.
     */
    private void showAlert(String msg) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(msg).setTitle("Message Box").create().show();
    }
}
