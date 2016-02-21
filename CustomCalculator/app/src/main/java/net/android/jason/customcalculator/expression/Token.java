/*------------------------------------------------------------------------------
 -  File       : Token.java
 -  Revision   : $Id$
 -  Course     : app
 -  Date       : 02/19/2016
 -  Author     : Jason
 -  Description: This file contains a class to extract tokens from an
                 expression.
 -----------------------------------------------------------------------------*/

package net.android.jason.customcalculator.expression;


import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Iterator;

/**
 * A class that implements methods to extract tokens from an arithmetic
 * expression.
 */
public class Token {


    /**
     * A {@code String} which stores the possible operator characters.
     */
    public static final String OPERATORS = "+-/x";


    /**
     * The definition of operator precedences. This precedence array should be
     * used with {@code OPERATORS}
     */
    public static final int[] OP_PRECEDENCE = {1, 1, 2, 2};


    /**
     * A regexp pattern to split an expression. Such regexp supports an
     * alternative way to get tokens from an expression.
     */
    public static final String SPLIT_PATTERN = "(?<=[\\+\\-/x])|(?=[\\+\\-/x])";


    /**
     * Determine whether a token is an operator.
     *
     * @param token A token extracted from an expression.
     * @return Returns true if {@code token} is an operator; returns false if
     * otherwise.
     */
    public static boolean isOperator(final String token) {
        return OPERATORS.contains(token);
    }


    /**
     * Get the precedence definition of the given {@code operator}.
     *
     * @param operator An operator
     * @return An {@code int} which represents the precedence of the {@code
     * operator}. If the given {@code operator} is illegal, returns {@link
     * Integer#MAX_VALUE}.
     * @see {@link Token#OPERATORS}
     * @see {@link Token#OP_PRECEDENCE}
     */
    public static int precedence(final char operator) {
        if (OPERATORS.indexOf(operator) < 0)
            return Integer.MAX_VALUE;

        return OP_PRECEDENCE[OPERATORS.indexOf(operator)];
    }


    /**
     * Create and initialize an instance of class {@code Token}.
     *
     * @param input     A {@code String} which contains an expression.
     * @param separator A {@code String} of {@code RegEx} pattern which
     *                  represents the separator used in the expression in
     *                  between either operands or operators. The default
     *                  pattern is an empty {@code String} which means there is
     *                  no separator.
     */
    public Token(String input, String separator) {
        this(input);
        if (separator != null && !separator.equals(""))
            this.separator = separator;
    }


    public Token(String input) {
        if (input == null)
            throw new NullPointerException("The given input is null.");
        separator = null;
        raw = input.trim();
    }


    /**
     * Get all tokens in an array of {@code String}.
     *
     * @return An array of {@code String} which contains all tokens extracted
     * from the expression.
     */
    public String[] getTokens() {
        return (separator == null) ?
                raw.split(SPLIT_PATTERN) : raw.split(separator);
    }


    /**
     * Get the separator used in the expression to separate the tokens.
     *
     * @return A {@code String} of {@code RegEx} pattern which represents the
     * separator used in the expression in between either operands or operators.
     * The default pattern is {@code null} which means there is no separator.
     */
    public String getSeparator() {
        return separator;
    }


    /**
     * Get the next token in the expression. The method returns an empty {@code
     * String} if the processor reaches the end of the expression.
     * <pre>
     * Ex: 2 + 3 / 5 * 2
     *  1st call: 2
     *  2nd call: +
     *  3rd call: 3
     * </pre>
     *
     * @return A {@code String} that contains the next token.
     */
    public String nextToken() {
        if (separator != null) {
            if (tokenIter == null)
                tokenIter = new ArrayDeque<>(
                        Arrays.asList(raw.split(separator))).iterator();
            if (tokenIter.hasNext())
                currentToken = tokenIter.next();
            if (!tokenIter.hasNext())
                tokenStart = raw.length();
            return currentToken;
        }

        // 2     +     3      +       5     +     156     -        7
        // (1) ts=0, i=0, charAt(i) = 2; charAt(i+1) = +
        //     currentToken=substring(0,1)=2, ts=1;
        // (2) ts=1,i=1, charAt(i)=+; charAt(i+1)=3
        //     currentToken=substring(1,2)=+, ts=2;
        // ...
        // (3) ts=6,i=6,charAt(i)=1,charAt(i+1)=5, i++
        //     ts=6,i=7,charAt(i)=5,charAt(i+1)=6.i++
        //     ts=6,i=8,charAt(i)=6,charAt(i+1)=-,i++
        //     currentToken=substring(6,9)=156,ts=9
        // (4) ts=9,i=9,charAt(i)=-,charAt(i+1)=7,i++
        //     currentToken=substring(9,10)=-,ts=10
        // (5) ts=10,i=10,charAt(i)=7, i+1==length, i++
        //     currentToken=substring(10,11)=7,ts=11
        for (int i = tokenStart; i < raw.length(); i++) {
            currentToken = raw.substring(tokenStart, i + 1);
            // if cursor i is still inside one non-operator token
            // keep forwarding the cursor
            if (i + 1 < raw.length() && !isOperator(currentToken)
                    && !isOperator(String.valueOf(raw.charAt(i + 1))))
                continue;

            tokenStart = i + 1;
            return currentToken;
        }

        return null;
    }


    /**
     * Reset the token processor to the beginning of the input.
     */
    public void restart() {
        currentToken = "";
        tokenStart = 0;
    }


    /**
     * Reset the source input to the given new input.
     *
     * @param input A {@code String} which contains an expression.
     */
    public void reset(String input) {
        raw = input.trim();
        restart();
    }


    /**
     * Get the current token extracted from the input.
     *
     * @return A {@code String} which contains a token of a expression.
     */
    public String getCurrentToken() {
        return currentToken;
    }


    /**
     * Determine whether token parsing is done for the given input.
     *
     * @return Returns {@code true} if the current token is the last one in the
     * expression; returns {@code false} if otherwise.
     */
    public boolean finished() {
        return tokenStart == raw.length();
    }

    private int tokenStart = 0;
    private String currentToken = "";
    private String raw;
    private String separator;
    private Iterator<String> tokenIter;
}
