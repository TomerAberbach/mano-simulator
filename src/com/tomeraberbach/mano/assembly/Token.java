package com.tomeraberbach.mano.assembly;

/**
 * Class representing the smallest semantic element in the assembly code of Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class Token {
    /**
     * Line number from which the {@link Token} was parsed.
     */
    private int line;

    /**
     * Token number in the line from which the {@link Token} was parsed.
     */
    private int position;

    /**
     * {@link String} which represented the {@link Token} in source code.
     */
    private String lexeme;


    /**
     * @param line     Line number from which the {@link Token} was parsed.
     * @param position Token number in the line from which the {@link Token} was parsed.
     * @param lexeme   {@link String} which represented the {@link Token} in source code.
     */
    Token(int line, int position, String lexeme) {
        this.line = line;
        this.position = position;
        this.lexeme = lexeme;
    }

    /**
     * @return {@link Token#lexeme}.
     */
    public String lexeme() {
        return lexeme;
    }


    /**
     * @return {@link String} describing the {@link Token} for {@link Compiler} error messages.
     */
    @Override
    public String toString() {
        return "'" + lexeme + "' at line " + line + " token position " + position;
    }
}
