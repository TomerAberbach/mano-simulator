package com.tomeraberbach.mano.assembly;

/* Tomer Aberbach
 * aberbat1@tcnj.edu
 * 11/12/2017
 * This code may be accessed and used by students at The College of New Jersey.
 */

/**
 * Class representing the smallest semantic element in assembly code.
 */
public class Token {
    /**
     * Line number which the {@link Token} was parsed from.
     */
    private int line;

    /**
     * Token number in the line which the {@link Token} was parsed from.
     */
    private int position;

    /**
     * {@link String} which represented the {@link Token} in source code.
     */
    private String lexeme;


    /**
     * @param line Line number which the {@link Token} was parsed from.
     * @param position Token number in the line which the {@link Token} was parsed from.
     * @param lexeme {@link String} which represented the {@link Token} in source code.
     */
    Token(int line, int position, String lexeme) {
        this.line = line;
        this.position = position;
        this.lexeme = lexeme;
    }


    /**
     * @return {@link String} describing the {@link Token} for {@link Compiler} error messages.
     */
    @Override
    public String toString() {
        return "'" + lexeme + "' at line " + line + " token position " + position;
    }

    /**
     * @return {@link Token#lexeme}.
     */
    String lexeme() {
        return lexeme;
    }
}
