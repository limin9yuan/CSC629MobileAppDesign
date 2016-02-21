/*------------------------------------------------------------------------------
 -  File       : CalculatorActivity.java
 -  Revision   : $Id$
 -  Course     : CSC629
 -  Date       : 02/18/2016
 -  Author     : Jason
 -  Description: This file contains the implementation of expression handling.
 -----------------------------------------------------------------------------*/

package net.android.jason.customcalculator.expression;


import java.util.ArrayDeque;

/**
 * This class implements a parser to translate row input to legal postfix and
 * infix expressions.
 */
public class Expression {


    /**
     * Create and initialize an instance of class {@code Expression}.
     *
     * @param raw A string which is the raw input from user.
     */
    public Expression(String raw) {
        rawInput = raw;
        validator = new Validator(rawInput);
        postfix = "";
    }


    /**
     * Get the user raw input.
     *
     * @return A string which is the raw input received from user.
     */
    public String getRawInput() {
        return rawInput;
    }


    /**
     * Evaluate and calculate the arithmetic expression and return the result.
     *
     * @return A {@code String} that represents the calculated result of the
     * expression.
     * @throws IllegalStateException
     */
    public String evaluate() throws IllegalStateException {
        // normalize
        String norm = validator.normalize();
        String t, left, right;

        PostfixConverter pc = new PostfixConverter(norm);
        if (postfix == null || postfix.equals(""))
            postfix = pc.convert(false);
        ArrayDeque<String> stack = new ArrayDeque<>(5);
        Token token = new Token(postfix, "\\s+");
        while (!token.finished()) {
            t = token.nextToken();
            if (!Token.isOperator(t)) {
                stack.push(t);
                continue;
            }
            right = stack.pop();
            left = stack.pop();
            stack.push(evaluate(t.charAt(0), left, right));
        }
        return stack.pop();
    }


    /**
     * Validate whether user input is legal
     *
     * @throws IllegalStateException
     */
    public void validate() throws IllegalStateException {
        if (!validator.validate())
            throw new IllegalStateException(validator.getMessage());
    }


    /**
     * Compute a simple expression which consists of 2 operands and 1 operator.
     * Note: Only +, -, x and / are implemented.
     *
     * @param op    The operator
     * @param left  The left operand
     * @param right The right operand
     * @return A {@code String} converted from the evaluated {@code double}
     * result.
     * @throws ArithmeticException When {@code op} is unknown or {@code op} is
     *                             division and {@code right} is 0.
     */
    private String evaluate(final char op, final String left,
                            final String right) throws ArithmeticException {
        double value, r;
        value = Double.parseDouble(left);
        r = Double.parseDouble(right);
        switch (op) {
            case '+':
                value += r;
                break;
            case '-':
                value -= r;
                break;
            case 'x':
                value *= r;
                break;
            case '/':
                if (r == 0.0d)
                    throw new ArithmeticException("Divided by 0.");
                value /= r;
                break;
            default:
                throw new ArithmeticException(
                        String.format("Unknown operator: %c", op));
        }
        return Double.toString(value);
    }


    /**
     * A string to store the raw input received from user.
     */
    private String rawInput;
    private Validator validator;
    private String postfix;
}
