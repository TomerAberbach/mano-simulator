package com.tomeraberbach.mano.assembly;

import com.tomeraberbach.mano.simulation.RAM;

/**
 * Class representing a label in the source code for Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class Label {
    /**
     * The {@link Token} which represented this {@link Label} in source code.
     */
    private Token token;

    /**
     * The address in {@link RAM} which this {@link Label} refers to.
     */
    private int address;


    /**
     * @param token   The {@link Token} which represented this {@link Label} in source code.
     * @param address The address in {@link RAM} which this {@link Label} refers to.
     */
    public Label(Token token, int address) {
        this.token = token;
        this.address = address;
    }


    /**
     * @return {@link Label#token}.
     */
    public Token token() {
        return token;
    }

    /**
     * @return {@link Label#address}.
     */
    public int address() {
        return address;
    }


    /**
     * @return {@link String} which is the result of calling {@link Token#toString()} on this {@link Label#token}.
     */
    @Override
    public String toString() {
        return token.toString();
    }
}
