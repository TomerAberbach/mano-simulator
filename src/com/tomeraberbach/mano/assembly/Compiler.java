package com.tomeraberbach.mano.assembly;

import com.tomeraberbach.mano.simulation.Computer;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing a compiler which compiles assembly code for Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class Compiler {
    /**
     * The assembly source code to compile.
     */
    private String source;

    /**
     * The {@link ArrayDeque} of {@link Token} instances acquired from tokenizing this {@link Compiler#source}.
     */
    private ArrayDeque<Token> tokens;

    /**
     * {@link Map} which maps assembly label lexemes to their respective {@link Label} instances.
     */
    private Map<String, Label> labelMap;

    /**
     * The {@link ArrayList} of {@link Instruction} instances which were compiled from this {@link Compiler#source}.
     */
    private ArrayList<Instruction> instructions;

    /**
     * The {@link ArrayList} of errors encountered when attempting to compile this {@link Compiler#source}.
     */
    private ArrayList<String> errors;

    /**
     * The address where the program in this {@link Compiler#source} starts.
     */
    private int start;

    /**
     * The current address this {@link Compiler} is at.
     */
    private int address;

    /**
     * The current {@link Token} this {@link Compiler} has parsed.
     */
    private Token token;


    /**
     * @param source The assembly source code to compile.
     */
    private Compiler(String source) {
        this.source = source;

        tokens = new ArrayDeque<>();
        labelMap = new HashMap<>();
        instructions = new ArrayList<>();
        errors = new ArrayList<>();

        start = -1;
        address = 0;
        token = null;
    }

    /**
     * @param source The assembly source code to compile.
     * @return {@link Program} which is the result of compiling this {@code source}.
     */
    public static Program compile(String source) {
        return new Compiler(source).compile();
    }

    /**
     * @return {@link Program} which is the result of compiling this {@link Compiler#source}.
     */
    private Program compile() {
        tokenize();
        generate();
        return new Program(start, instructions, new ArrayList<>(labelMap.values()), errors);
    }

    /**
     * Splits this {@link Compiler#source} by line, removes comments, pads commas, and then splits by whitespace.
     * The resulting {@link String} instances in the final split will be converted to {@link Token} instances and added to this {@link Compiler#tokens}.
     */
    private void tokenize() {
        // Gets the lines
        String[] lines = source.split("\n");

        // Loops through the lines of the source code
        for (int line = 0; line < lines.length; line++) {
            // Splits the line into lexemes at spaces and value boundaries
            String[] lexemes = lines[line].replaceAll("/.*$", "").replaceAll(",", " , ").split("\\s+");

            // Loops through the lexemes to initialize tokens
            for (int position = 0; position < lexemes.length; position++) {
                if (!lexemes[position].isEmpty()) {
                    // Initializes a token for the current lexeme with its line and column
                    tokens.add(new Token(line, position, lexemes[position]));
                }
            }
        }
    }

    /**
     * Interprets {@link Token} instances created by {@link Compiler#tokenize()}:
     * <ul>
     * <li>Maps instructions to their respective codes (see {@link Instruction#MEMORY_REFERENCE_INSTRUCTIONS} and {@link Instruction#IMPLICIT_REFERENCE_INSTRUCTIONS}.</li>
     * <li>Interprets directives.</li>
     * <li>Parses decimal and hexadecimal numbers literals.</li>
     * <li>Collects labelMap in preparation for calling {@link Compiler#replaceLabels(ArrayList)}.</li>
     * </ul>
     * {@link Instruction} instances generated will be added to {@link Compiler#instructions}.
     * All errors are logged as {@link String} instances in {@link Compiler#errors}.
     */
    private void generate() {
        ArrayList<String> labels = new ArrayList<>();

        label:
        while (!tokens.isEmpty()) {
            // Gets the next token
            token = tokens.poll();

            if (isLabel()) {
                label();
            }

            String lexeme = token.lexeme().toUpperCase();

            switch (lexeme) {
                case "ORG":
                    org(false);
                    break;

                case "START":
                    org(true);
                    if (start < 0) {
                        start = address;
                    } else {
                        errors.add("Encountered directive, " + token + ", twice.");
                    }
                    break;

                case "END":
                    // Checks if there are still tokens left
                    if (!tokens.isEmpty()) {
                        errors.add("Encountered directive, " + token + ", before end of code.");
                    }

                    break label;

                default:
                    // Sets the label which the current instruction will be using to none
                    labels.add(null);

                    // Checks if the argument is a decimal or hexadecimal number literal
                    if (lexeme.equals("DEC") || lexeme.equals("HEX")) {
                        // Checks if no number literal follows the decimal or hexadecimal label
                        if (tokens.isEmpty()) {
                            errors.add("Missing " + (lexeme.equals("HEX") ? "hexadecimal" : "decimal") + " number literal after " + token + ".");
                        } else {
                            Token argument = tokens.poll();

                            // Gets the argument as a 12 bit number
                            int number = get12BitNumber((lexeme.equals("HEX") ? "0x" : "") + argument.lexeme(), Computer.maxValue(Computer.VALUE_SIZE));

                            // Checks if the argument was a valid address
                            if (number >= 0) {
                                // Adds the *instruction*
                                instructions.add(new Instruction(address, number, token, argument));
                            } else {
                                errors.add("Invalid address, " + argument + ".");
                            }
                        }
                    } else if (Instruction.MEMORY_REFERENCE_INSTRUCTIONS.containsKey(lexeme)) {
                        // Checks if there is no argument following the memory reference instruction
                        if (tokens.isEmpty()) {
                            errors.add("Missing argument after memory address instruction, " + token + ".");
                        } else {
                            // Gets the argument
                            Token argument = tokens.poll();

                            Instruction instruction;

                            // Checks if indirect addressing is being used
                            instruction = !tokens.isEmpty() && tokens.peek().lexeme().equals("I") ?
                                new Instruction(address, Instruction.MEMORY_REFERENCE_INSTRUCTIONS.get(lexeme), token, argument, tokens.poll()).indirect() :
                                new Instruction(address, Instruction.MEMORY_REFERENCE_INSTRUCTIONS.get(lexeme), token, argument);

                            // Saves the label argument to resolve later once the symbol table is full
                            labels.set(labels.size() - 1, argument.lexeme());

                            instructions.add(instruction);
                        }
                    } else if (Instruction.IMPLICIT_REFERENCE_INSTRUCTIONS.containsKey(lexeme)) {
                        instructions.add(new Instruction(address, Instruction.IMPLICIT_REFERENCE_INSTRUCTIONS.get(lexeme), token));
                    } else {
                        errors.add("Invalid instruction token, " + token + (instructions.isEmpty() ? "" : " or potentially unneeded argument after " + instructions.get(instructions.size() - 1).tokens()[0]) + ".");
                    }

                    // Increments the address and checks that it did not go outside the bounds of the RAM
                    if (++address == 0) {
                        errors.add("Instruction address overflow from 4096 to 0 at token, " + token + ".");
                    }

                    break;
            }
        }

        replaceLabels(labels);

        if (start < 0) {
            start = instructions.isEmpty() ? 0 : instructions.get(0).address();
        }
    }

    /**
     * @return boolean representing if the current {@link Token} ({@link Compiler#token}) is a {@link Label}.
     */
    private boolean isLabel() {
        // Checks if the next token is a comma
        return !tokens.isEmpty() && tokens.peek().lexeme().equals(",");
    }

    /**
     * Treats the current {@link Token} ({@link Compiler#token}) as a {@link Label} and logs it in {@link Compiler#labelMap}.
     */
    private void label() {
        if (labelMap.containsKey(token.lexeme())) {
            errors.add("Duplicate label, " + token + ".");
        } else {
            // Adds the label to the symbol table
            labelMap.put(token.lexeme(), new Label(token, address));
        }

        // Skips the comma
        tokens.poll();

        // Sets the current token to the token after the comma
        token = tokens.poll();
    }

    /**
     * Interprets an ORG directive by parsing its number argument and changing the current address ({@link Compiler#address}).
     */
    private void org(boolean start) {
        // Checks if there is no argument after ORG
        if (tokens.isEmpty()) {
            errors.add("Missing argument after directive, " + token + ".");
        } else {
            // Gets the ORG's argument
            int number = get12BitNumber("0x" + tokens.peek().lexeme(), Computer.maxValue(Computer.ADDRESS_SIZE));

            // Checks if the argument was a valid address
            if (number >= 0) {
                // Sets the current address and skips the number
                address = number;
                tokens.poll();
            } else {
                errors.add("Invalid address, " + (start ? "START" : "ORG") + " " + tokens.peek() + ".");
            }
        }
    }

    /**
     * @param lexeme {@link String} representing a number to parse.
     * @param range  The inclusive upper bound to which the parsed number has to conform.
     * @return -1 if {@code lexeme} is not a number or not on the interval [0, {@code range}], the integer represented by {@code lexeme} otherwise.
     */
    private static int get12BitNumber(String lexeme, int range) {
        try {
            int number = Integer.decode(lexeme);

            // Checks that the number is within the specified range
            if (Math.abs(number) >= 0 && Math.abs(number) <= range) {
                return number < 0 ? range + 1 + number : number;
            }
        } catch (NumberFormatException e) {
            return -1;
        }

        return -1;
    }

    /**
     * Replaces any labelMap found after calling {@link Compiler#generate()} with their hex values in their respective {@link Instruction} instances in {@link Compiler#instructions} using {@link Compiler#labelMap}.
     *
     * @param labels {@link ArrayList} of {@link String} labelMap to replace using {@link Compiler#labelMap}.
     */
    private void replaceLabels(ArrayList<String> labels) {
        // Loops through the instructions to substitute labelMap for hex
        for (int i = 0; i < instructions.size(); i++) {
            // Checks if there was a label argument for the current instruction
            if (labels.get(i) != null) {
                // Checks if the label was ever defined
                if (this.labelMap.containsKey(labels.get(i))) {
                    // Adds the address of the label to the instruction hex
                    instructions.set(i, instructions.get(i).argument(this.labelMap.get(labels.get(i))));
                } else {
                    switch (instructions.get(i).tokens().length) {
                        case 1:
                            errors.add("Unrecognized label name, " + instructions.get(i).tokens()[0] + ".");
                            break;
                        case 2:
                            errors.add("Unrecognized label name, " + instructions.get(i).tokens()[1] + " or potentially missing argument after " + instructions.get(i).tokens()[0] + ".");
                            break;
                    }
                }
            }
        }
    }
}
