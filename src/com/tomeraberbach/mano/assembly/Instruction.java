package com.tomeraberbach.mano.assembly;

/* Tomer Aberbach
 * aberbat1@tcnj.edu
 * 11/12/2017
 * This code may be accessed and used by students at The College of New Jersey.
 */

public class Instruction {
    private char address;
    private char hex;
    private Token[] tokens;


    Instruction(char address, char hex, Token... tokens) {
        this.address = address;
        this.hex = hex;
        this.tokens = tokens;
    }


    @Override
    public String toString() {
        return pad(Integer.toHexString((int)hex)).toUpperCase();
    }


    char hex() {
        return hex;
    }

    char address() {
        return address;
    }

    Token[] tokens() {
        return tokens;
    }

    void setHex(char hex) {
        this.hex = hex;
    }

    private static String pad(String text) {
        StringBuilder builder = new StringBuilder();

        // Loops to pad the given text with zeros from the left if it is less than 4 characters in length
        while (builder.length() + text.length() < 4) {
            builder.append('0');
        }

        builder.append(text);

        return builder.toString();
    }
}
