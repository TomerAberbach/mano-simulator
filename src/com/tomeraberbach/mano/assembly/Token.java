package com.tomeraberbach.mano.assembly;

public class Token {
    private int line;
    private int position;
    private String lexeme;


    Token(int line, int position, String lexeme) {
        this.line = line;
        this.position = position;
        this.lexeme = lexeme;
    }


    @Override
    public String toString() {
        return "'" + lexeme + "' at line " + line + " token position " + position;
    }


    String lexeme() {
        return lexeme;
    }
}
