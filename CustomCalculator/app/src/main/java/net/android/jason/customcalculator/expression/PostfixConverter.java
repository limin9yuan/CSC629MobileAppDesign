/*------------------------------------------------------------------------------
 -  File       : PostfixConverter.java
 -  Revision   : $Id$
 -  Course     : app
 -  Date       : 02/19/2016
 -  Author     : Jason
 -  Description: This file contains a class which supports to convert an
 infix expression to a postfix expression
 -----------------------------------------------------------------------------*/

package net.android.jason.customcalculator.expression;

import java.util.ArrayDeque;


/**
 * A class that helps to convert an infix arithmetic expression to a postfix
 * expression.
 */
class PostfixConverter {


    /**
     * Create and initialize an instance of class {@code PostfixConverter}.
     *
     * @param infix An infix arithmetic expression.
     */
    PostfixConverter(String infix) {
        if (infix == null)
            throw new NullPointerException(
                    "The given infix expression is null");
        raw = infix;
        post = null;
    }


    /**
     * Convert the infix expression to a postfix expression in which operands
     * and operators are separated by a space.
     *
     * @param force Given true, this method ignores the existing postfix
     *              expression converted and does the conversion.
     * @return A {@code String} which contains the converted postfix expression.
     */
    String convert(boolean force) {
        // no need to do it again and again.
        if (force || post == null || post.equals("")) {
            ArrayDeque<String> operatorStack = new ArrayDeque<>(5);
            Token token = new Token(raw);
            // a queue of tokens
            String t, op;

            post = "";
            while (!token.finished()) {
                t = token.nextToken();
                if (!Token.isOperator(t)) {
                    post += " " + t;
                    continue;
                }
                // t is an op
                while (!operatorStack.isEmpty()
                        && !higherPrecedence(t, operatorStack.peek()))
                    post += " " + operatorStack.pop();
                operatorStack.push(t);
            }
            while (!operatorStack.isEmpty())
                post += " " + operatorStack.pop();
        }

        return post.trim();
    }


    /**
     * Convert the infix expression to stack which represents a postfix
     * expression stack.
     *
     * @param force Given true, this method ignores the existing postfix
     *              expression converted and does the conversion.
     * @return A {@code ArrayDeque} of String which represents a stack and which
     * contains the converted postfix expression.
     */
    ArrayDeque<String> convertToStack(boolean force) {
        if (force || postfixStack == null || postfixStack.isEmpty()) {
            ArrayDeque<String> operatorStack = new ArrayDeque<>(5);
            Token token = new Token(raw);
            // a queue of tokens
            String t, op;

            postfixStack.clear();
            while (!token.finished()) {
                t = token.nextToken();
                if (!Token.isOperator(t)) {
                    postfixStack.push(t);
                    continue;
                }
                // t is an op
                while (!operatorStack.isEmpty()
                        && !higherPrecedence(t, operatorStack.peek()))
                    postfixStack.push(operatorStack.pop());
                operatorStack.push(t);
            }
            while (!operatorStack.isEmpty())
                postfixStack.push(operatorStack.pop());
        }

        return postfixStack;
    }


    /**
     * Get the converted postfix expression.
     *
     * @return A {@code String} which contains the converted postfix expression.
     */
    String getPostfix() {
        return post;
    }


    /**
     * Determin whether {@code op} has higher precedence than {@code opOther}.
     *
     * @param op      An operator
     * @param opOther Another operator
     * @return Returns true if {@code op} has higher precedence than {@code
     * opOther}; returns false if otherwise.
     */
    private boolean higherPrecedence(String op, String opOther) {
        int opPred = Token.precedence(op.charAt(0));
        int opPredOther = Token.precedence(opOther.charAt(0));

        return opPred > opPredOther;
    }


    private String post;
    private String raw;
    private ArrayDeque<String> postfixStack;
}
