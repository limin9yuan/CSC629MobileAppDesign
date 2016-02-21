/*------------------------------------------------------------------------------
 -  File       : CalculatorActivity.java
 -  Revision   : $Id$
 -  Course     : CSC629
 -  Date       : 02/18/2016
 -  Author     : Jason
 -  Description: This file contains the implementation of a calculator app.
 -----------------------------------------------------------------------------*/


package net.android.jason.customcalculator;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import net.android.jason.customcalculator.expression.Token;
import net.android.jason.customcalculator.expression.Expression;

public class CalculatorActivity extends AppCompatActivity {


    /**
     * The event handler for the event when a number key (0..9) is pressed
     * down.
     *
     * @param view
     */
    public void onNumberClick(View view) {
        // when a number key is pressed
        Button button = (Button)view;

        // EditText edit = getExprEdit();
        EditText edit = (EditText)findViewById(R.id.editExpr);
        //edit.append(button.getText());
        edit.append(button.getText());
        // keep cursor position at the end of the text
        edit.setSelection(edit.getText().length());
    }


    /**
     * The event handler for the event when key dot is pressed down.
     *
     * @param view
     */
    public void onDotClick(View view) {
        // when the dot is pressed
        Button button = (Button)view;
        // showMessage(String.format("%s is clicked", button.getText()));
        EditText edit = (EditText)findViewById(R.id.editExpr);
        Token token = new Token(edit.getText().toString());
        String[] tokens = token.getTokens();
        if (tokens[tokens.length - 1].indexOf('.') < 0) {
            // edit.getText().append(button.getText());
            edit.append(button.getText());
            // keep cursor position at the end of the text
            edit.setSelection(edit.getText().length());
        }
    }

    public void onClearClick(View view) {
        EditText edit = (EditText)findViewById(R.id.editExpr);
        edit.setText("");
    }


    public void onEqualClick(View view) {
        // .8x5/9+3-4/2/3+10-5x7
        EditText edit = (EditText)findViewById(R.id.editExpr);
        if (edit.getText().length() == 0)
            return;
        Expression expr = new Expression(edit.getText().toString());
        try {
            expr.validate();
            String result = expr.evaluate();
            edit.setText(result);
            edit.setSelection(edit.getText().length());
        } catch (Exception e) {
            Toast.makeText(CalculatorActivity.this, e.getLocalizedMessage(), Toast.LENGTH_SHORT)
                    .show();
        }
    }


    /**
     * This method delete one char before the cursor position. Or delete the all
     * selected characters.
     *
     * @param view
     */
    public void onDeleteClick(View view) {
        // showMessage(String.format("%s is clicked", button.getText()));

        // get the edit text view
        EditText edit = (EditText)findViewById(R.id.editExpr);
        if (edit.getText().length() == 0)
            return;
        // get current cursor position
        int selectedStart = edit.getSelectionStart();
        int selectedEnd = edit.getSelectionEnd();
        // clear the selected text before cursor position
        if (selectedEnd - selectedStart > 0) {
            edit.clearFocus();
            return;
        }
        // remove the char right before cursor position
        edit.dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL));
    }


    /**
     * The event handler when an operator key is pressed down.
     *
     * @param view
     */
    public void onOperatorClick(View view) {
        Button button = (Button)view;
        // showMessage(String.format("%s is clicked", button.getText()));
        EditText edit = (EditText)findViewById(R.id.editExpr);
        String existing = edit.getText().toString();

        if (!existing.equals("")) {
            String last = existing.substring(
                    existing.length() - 1, existing.length());
            if (Token.isOperator(last))
                return;
        } else if (button.getText().equals("x") || button.getText().equals("/"))
            return;

        edit.append(button.getText());
        // keep cursor position at the end of the text
        edit.setSelection(edit.getText().length());
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calculator);
    }
}
