package com.tomeraberbach.mano.assembly;

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
        return pad(Integer.toHexString((int)hex), '0', 4).toUpperCase();
    }


    public char hex() {
        return hex;
    }

    public char address() {
        return address;
    }

    public Token[] tokens() {
        return tokens;
    }

    public void setHex(char hex) {
        this.hex = hex;
    }

    public static String pad(String text, char c, int size) {
        StringBuilder builder = new StringBuilder();

        while (builder.length() + text.length() < size) {
            builder.append(c);
        }

        builder.append(text);

        return builder.toString();
    }
}
