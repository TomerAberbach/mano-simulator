package com.tomeraberbach.mano.assembly;

import com.tomeraberbach.mano.Utilities;
import com.tomeraberbach.mano.simulation.RAM;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Class representing an instruction for Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class Instruction {
    /**
     * Maps memory referencing assembly instruction lexemes to their respective codes.
     * This does not include directives.
     *
     * @see Instruction#INDIRECT_ADDRESSING_OFFSET for information on indirect addressing.
     */
    public static final Map<String, Integer> MEMORY_REFERENCE_INSTRUCTIONS;

    /**
     * The amount which must be added to a direct addressing assembly instruction's code so that it becomes an indirect addressing assembly instruction.
     */
    public static final int INDIRECT_ADDRESSING_OFFSET = 0x8000;

    /**
     * Maps non-memory referencing assembly instruction lexemes to their respective codes.
     */
    public static final Map<String, Integer> IMPLICIT_REFERENCE_INSTRUCTIONS;

    static {
        // Memory Reference
        Map<String, Integer> map = new HashMap<>();
        map.put("AND", 0x0000);
        map.put("ADD", 0x1000);
        map.put("LDA", 0x2000);
        map.put("STA", 0x3000);
        map.put("BUN", 0x4000);
        map.put("BSA", 0x5000);
        map.put("ISZ", 0x6000);
        MEMORY_REFERENCE_INSTRUCTIONS = Collections.unmodifiableMap(map);

        // Register Reference
        map = new HashMap<>();
        map.put("CLA", 0x7800);
        map.put("CLE", 0x7400);
        map.put("CMA", 0x7200);
        map.put("CME", 0x7100);
        map.put("CIR", 0x7080);
        map.put("CIL", 0x7040);
        map.put("INC", 0x7020);
        map.put("SPA", 0x7010);
        map.put("SNA", 0x7008);
        map.put("SZA", 0x7004);
        map.put("SZE", 0x7002);
        map.put("HLT", 0x7001);

        // Input Output
        map.put("INP", 0xF800);
        map.put("OUT", 0xF400);
        map.put("SKI", 0xF200);
        map.put("SKO", 0xF100);
        map.put("ION", 0xF080);
        map.put("IOF", 0xF040);
        IMPLICIT_REFERENCE_INSTRUCTIONS = Collections.unmodifiableMap(map);
    }


    /**
     * The address of this {@link Instruction} in {@link RAM}.
     */
    private int address;

    /**
     * The value representing this {@link Instruction} in {@link RAM}.
     */
    private int code;

    /**
     * Array of {@link Token} instances which represented this {@link Instruction} in source code.
     */
    private Token[] tokens;


    /**
     * @param address The address of this {@link Instruction} in {@link RAM}.
     * @param code    The value representing this {@link Instruction} in {@link RAM}.
     * @param tokens  Array of {@link Token} instances which represented this {@link Instruction} in source code.
     */
    Instruction(int address, int code, Token... tokens) {
        this.address = address;
        this.code = code;
        this.tokens = tokens;
    }


    /**
     * @return The four character hexadecimal representation of this {@link Instruction#code}.
     */
    @Override
    public String toString() {
        return Utilities.hex(code, 4);
    }


    /**
     * @return {@link Instruction#address}.
     */
    public int address() {
        return address;
    }

    /**
     * @return {@link Instruction#code}.
     */
    public int code() {
        return code;
    }

    /**
     * @return {@link Instruction#tokens}.
     */
    public Token[] tokens() {
        return tokens;
    }


    /**
     * @return {@link Instruction} which is identical to this {@link Instruction} except {@link Instruction#code} is offset by {@link Instruction#INDIRECT_ADDRESSING_OFFSET}.
     */
    public Instruction indirect() {
        return new Instruction(address, code + INDIRECT_ADDRESSING_OFFSET, tokens);
    }

    /**
     * @param label {@link Label} which refers to the argument of this {@link Instruction}.
     * @return {@link Instruction} which is identical to this {@link Instruction} except {@link Instruction#code} is offset by {@link Label#address}.
     */
    public Instruction argument(Label label) {
        return new Instruction(address, code + label.address(), tokens);
    }
}
