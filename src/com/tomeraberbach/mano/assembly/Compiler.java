package com.tomeraberbach.mano.assembly;

import java.util.*;

public class Compiler {
    private static final Map<String, Character> MEMORY_REFERENCE_INSTRUCTIONS;
    private static final char INDIRECT_ADDRESSING_OFFSET = 0x8000;
    private static final Map<String, Character> IMPLICIT_REFERENCE_INSTRUCTIONS;
    private static final char ADDRESS_SIZE = 0x0FFF;
    private static final char WORD_SIZE = 0xFFFF;
    static {
        // Memory Reference
        Map<String, Character> map = new HashMap<>();
        map.put("AND", (char)0x0000);
        map.put("ADD", (char)0x1000);
        map.put("LDA", (char)0x2000);
        map.put("STA", (char)0x3000);
        map.put("BUN", (char)0x4000);
        map.put("BSA", (char)0x5000);
        map.put("ISZ", (char)0x6000);
        MEMORY_REFERENCE_INSTRUCTIONS = Collections.unmodifiableMap(map);

        // Register Reference
        map = new HashMap<>();
        map.put("CLA", (char)0x7800);
        map.put("CLE", (char)0x7400);
        map.put("CMA", (char)0x7200);
        map.put("CME", (char)0x7100);
        map.put("CIR", (char)0x7080);
        map.put("CIL", (char)0x7040);
        map.put("INC", (char)0x7020);
        map.put("SPA", (char)0x7010);
        map.put("SNA", (char)0x7008);
        map.put("SZA", (char)0x7004);
        map.put("SZE", (char)0x7002);
        map.put("HLT", (char)0x7001);

        // Input Output
        map.put("INP", (char)0xF800);
        map.put("OUT", (char)0xF400);
        map.put("SKI", (char)0xF200);
        map.put("SKO", (char)0xF100);
        map.put("ION", (char)0xF080);
        map.put("IOF", (char)0xF040);
        IMPLICIT_REFERENCE_INSTRUCTIONS = Collections.unmodifiableMap(map);
    }


    private String source;

    private ArrayDeque<Token> tokens;
    private Map<String, Character> symbolTable;
    private ArrayList<Instruction> instructions;
    private ArrayList<String> errors;

    private char address;
    private Token token;


    public Compiler(String source) {
        this.source = source;

        tokens = new ArrayDeque<>();
        symbolTable = new HashMap<>();
        instructions = new ArrayList<>();
        errors = new ArrayList<>();

        address = 0;
        token = null;
    }

    public Compiler(Collection<Compiler> compilers) {
        tokens = new ArrayDeque<>();
        symbolTable = new HashMap<>();
        instructions = new ArrayList<>();
        errors = new ArrayList<>();

        StringBuilder builder = new StringBuilder();

        for (Compiler compiler : compilers) {
            builder.append(compiler.source).append("\n");
            errors.addAll(compiler.errors);
            instructions.addAll(compiler.instructions);
        }

        source = builder.toString();
    }


    public ArrayList<String> errors() {
        return errors;
    }

    public String[] memory() {
        if (instructions.stream().map(Instruction::address).distinct().count() == instructions.size()) {
            String[] memory = new String[ADDRESS_SIZE + 1];
            Arrays.fill(memory, "0000");

            for (Instruction instruction : instructions) {
                memory[instruction.address()] = instruction.toString();
            }

            return memory;
        } else {
            return null;
        }
    }


    private void lexAndParse() {
        // Gets the lines
        String[] lines = source.split("\n");

        // Loops through the lines of the source code
        for (int line = 0; line < lines.length; line++) {
            // Splits the line into lexemes at spaces and word boundaries
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

    private void analyzeAndGenerate() {
        ArrayList<String> labels = new ArrayList<>();

        label:
        while (!tokens.isEmpty()) {
            // Gets the next token
            token = tokens.poll();

            if (isLabel()) {
                label();
            }

            switch (token.lexeme()) {
                case "ORG":
                    org();
                    break;

                case "END":
                    if (!tokens.isEmpty()) {
                        errors.add("Encountered directive, " + token + ", before end of code.");
                    }

                    break label;

                default:
                    // Sets the label which the current instruction will be using to none
                    labels.add(null);

                    // Checks if the argument is a decimal or hexadecimal number literal
                    if (token.lexeme().equals("DEC") || token.lexeme().equals("HEX")) {
                        // Checks if no number literal follows the decimal or hexadecimal label
                        if (tokens.isEmpty()) {
                            errors.add("Missing " + (token.lexeme().equals("HEX") ? "hexadecimal" : "decimal") + " number literal after " + token + ".");
                        } else {
                            Token argument = tokens.poll();

                            // Gets the argument as a 12 bit number
                            int number = get12BitNumber((token.lexeme().equals("HEX") ? "0x" : "") + argument.lexeme(), WORD_SIZE);

                            // Checks if the argument was a valid address
                            if (number >= 0) {
                                // Adds the *instruction*
                                instructions.add(new Instruction(address, (char)number, token, argument));
                            } else {
                                errors.add("Invalid address, " + argument + ".");
                            }
                        }
                    } else if (MEMORY_REFERENCE_INSTRUCTIONS.containsKey(token.lexeme())) {
                        // Checks if there is no argument following the memory reference instruction
                        if (tokens.isEmpty()) {
                            errors.add("Missing argument after memory address instruction, " + token + ".");
                        } else {
                            // Gets the argument
                            Token argument = tokens.poll();

                            // Initializes an instruction
                            Instruction instruction = new Instruction(address, MEMORY_REFERENCE_INSTRUCTIONS.get(token.lexeme()), token, argument);

                            // Saves the label argument to resolve later once the symbol table is full
                            labels.set(labels.size() - 1, argument.lexeme());

                            // Checks if indirect addressing is being used
                            if (!tokens.isEmpty() && tokens.peek().lexeme().equals("I")) {
                                // Skips the I token
                                tokens.poll();

                                // Offsets the hex
                                instruction.setHex((char)(instruction.hex() + INDIRECT_ADDRESSING_OFFSET));
                            }

                            instructions.add(instruction);
                        }
                    } else if (IMPLICIT_REFERENCE_INSTRUCTIONS.containsKey(token.lexeme())) {
                        instructions.add(new Instruction(address, IMPLICIT_REFERENCE_INSTRUCTIONS.get(token.lexeme()), token));
                    } else {
                        errors.add("Invalid instruction token, " + token + (instructions.size() > 0 ? " or potentially unneeded argument after " + instructions.get(instructions.size() - 1).tokens()[0] +"." : "") + ".");
                    }

                    // Increments the address and checks that it did not go outside the bounds of the RAM
                    if (++address == 0) {
                        errors.add("Instruction address overflow from 4096 to 0 at token, " + token + ".");
                    }

                    break;
            }
        }

        replaceLabels(labels);
    }

    public void compile() {
        lexAndParse();
        analyzeAndGenerate();
    }

    private boolean isLabel() {
        // Checks if the next token is a comma
        return !tokens.isEmpty() && tokens.peek().lexeme().equals(",");
    }

    private void label() {
        // Adds the label to the symbol table
        symbolTable.put(token.lexeme(), address);

        // Skips the comma
        tokens.poll();

        // Sets the current token to the token after the comma
        token = tokens.poll();
    }

    private void org() {
        // Checks if there is no argument after ORG
        if (tokens.isEmpty()) {
            errors.add("Missing argument after directive, " + token + ".");
        } else {
            // Gets the ORG's argument
            int number = get12BitNumber("0x" + tokens.peek().lexeme(), ADDRESS_SIZE);

            // Checks if the argument was a valid address
            if (number >= 0) {
                // Sets the current address and skips the number
                address = (char)number;
                tokens.poll();
            } else {
                errors.add("Invalid address, ORG " + tokens.peek() + ".");
            }
        }
    }

    private void replaceLabels(ArrayList<String> labels) {
        // Loops through the instructions to substitute labels for hex
        for (int i = 0; i < instructions.size(); i++) {
            // Checks if there was a label argument for the current instruction
            if (labels.get(i) != null) {
                // Checks if the label was ever defined
                if (symbolTable.containsKey(labels.get(i))) {
                    // Adds the address of the label to the instruction hex
                    instructions.get(i).setHex((char)(instructions.get(i).hex() + symbolTable.get(labels.get(i))));
                } else {
                    errors.add("Unrecognized label name, " + instructions.get(i).tokens()[1] + " or potentially missing argument after " + instructions.get(i).tokens()[0] + ".");
                }
            }
        }
    }

    private static int get12BitNumber(String lexeme, char range) {
        try {
            int number = Integer.decode(lexeme);

            // Checks that the number is within the specified range
            if (number >= 0 && number <= range) {
                return number;
            }
        } catch (NumberFormatException ignored) { }

        return -1;
    }
}
