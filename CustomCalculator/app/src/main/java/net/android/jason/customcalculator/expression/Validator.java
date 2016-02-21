/*------------------------------------------------------------------------------
 -  File              : Validator.java
 -  Revision      : $Id$
 -  Course        : app
 -  Date            : 02/19/2016
 -  Author        : Jason
 -  Description: This file contains ...
 -----------------------------------------------------------------------------*/

package net.android.jason.customcalculator.expression;


import java.util.ArrayDeque;

/**
 * A inner class to validate the input string
 */
class Validator {

    /**
     * Create and initialize an instance of class {@code Validator}.
     *
     * @param input A {@code String} which is user's raw input.
     */
    Validator(String input) {
        if (input == null)
            throw new NullPointerException("The given input is null");
        raw = input.trim();
        message = "";
        validated = false;
    }

    /**
     * Validate the input
     *
     * @return Returns true if validation passes; returns false if otherwise.
     * see {@code getMessage} for the error messages.
     * @see {@link #getMessage()}
     */
    boolean validate() {
        if (raw.length() == 0) {
            message = "Empty expression";
            return !(validated = true);
        }
        // get the last char
        char ending = raw.charAt(raw.length() - 1);
        if (ending == '/' || ending == 'x') {
            message = "More operands needed";
            return !(validated = true);
        }
        // put other validation here
        Token token = new Token(raw);
        String prev, curr;
        prev = token.nextToken();
        while (!token.finished()) {
            curr = token.nextToken();
            if (Token.isOperator(prev) && prev.charAt(0) == '/'
                    && Double.parseDouble(curr) == 0.0d) {
                message = String.format("Divisor is 0: %s %s", prev, curr);
                return !(validated = true);
            }
            if (Token.isOperator(prev) && Token.isOperator(curr)) {
                message = String.format(
                        "Multiple operators without operands: %s %s",
                        prev, curr);
            }
            prev = curr;
        }

        return validated = true;
    }


    /**
     * Normalize the input by adding missing 0s or removing unnecessary
     * operators.
     *
     * @return Returns an expression
     */
    String normalize() {
        if (!validated)
            throw new IllegalStateException(
                    "The raw input is not validated yet");

        String expr = "";
        ArrayDeque<String> stack = new ArrayDeque<>(10);
        // the first token
        Token token = new Token(raw);

        String t = token.nextToken();
        // if the first one is a sign, add one extra 0 in the beginning.
        if (Token.isOperator(t))
            expr += "0" + t;
        else {
            if (t.startsWith("."))
                t = "0" + t;
            if (t.endsWith("."))
                t += "0";
            expr += t;
        }
        //stack.push(t);
        while (!token.finished()) {
            t = token.nextToken();
            if (t.startsWith("."))
                t = "0" + t;
            if (t.endsWith("."))
                t += "0";
            //stack.push(t);
            expr += t;
        }
        // is the last token an operator?
        // true --> take it out
        t = expr.substring(expr.length() - 1, expr.length());
        return (Token.isOperator(t)) ?
                expr.substring(0, expr.length() - 1) : expr;
    }


    /**
     * Get the error message after the validation.
     *
     * @return A {@code String} which contains the message.
     */
    String getMessage() {
        return message;
    }


    private boolean validated;
    private String message;
    private String raw;
}
