package com.tomeraberbach.mano.assembly;

import com.tomeraberbach.mano.Utilities;
import com.tomeraberbach.mano.simulation.RAM;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * Class representing a program for Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class Program {
    /**
     * The address in {@link RAM} where this {@link Program} starts.
     */
    private int start;

    /**
     * The {@link Instruction} instances of this {@link Program}.
     */
    private ArrayList<Instruction> instructions;

    /**
     * The {@link Label} instances used in the source code of this {@link Program}.
     */
    private ArrayList<Label> labels;

    /**
     * The errors encountered when compiling the source code of this {@link Program}.
     */
    private ArrayList<String> errors;


    /**
     * @param start        The address in {@link RAM} where this {@link Program} starts.
     * @param instructions The {@link Instruction} instances of this {@link Program}.
     * @param labels       The {@link Label} instances used in the source code of this {@link Program}.
     * @param errors       The errors encountered when compiling the source code of this {@link Program}.
     */
    public Program(int start, ArrayList<Instruction> instructions, ArrayList<Label> labels, ArrayList<String> errors) {
        this.start = start;
        this.instructions = instructions;
        this.labels = labels;
        this.errors = errors;
    }

    /**
     * @param start    The address in {@link RAM} where the unioned {@link Program} should start.
     * @param programs Array of {@link Program} instances to combine.
     * @return {@link Program} which contains the instances of every {@link Program} in {@code programs} and which starts at address {@code start}.
     */
    public static Program union(int start, Program... programs) {
        return new Program(
            start,
            Utilities.union(Arrays.stream(programs).map(program -> program.instructions).collect(Collectors.toList())),
            Utilities.union(Arrays.stream(programs).map(program -> program.labels).collect(Collectors.toList())),
            Utilities.union(Arrays.stream(programs).map(program -> program.errors).collect(Collectors.toList()))
        );
    }

    /**
     * @return {@link Program#start}.
     */
    public int start() {
        return start;
    }

    /**
     * @return {@link Program#instructions}.
     */
    public ArrayList<Instruction> instructions() {
        return instructions;
    }

    /**
     * @return {@link Program#labels}.
     */
    public ArrayList<Label> labels() {
        return labels;
    }

    /**
     * @return {@link Program#errors}.
     */
    public ArrayList<String> errors() {
        return errors;
    }

    /**
     * @return boolean representing if there are {@link Instruction} instances at identical addresses.
     */
    public boolean conflicts() {
        return instructions.stream()
            .map(Instruction::address)
            .distinct()
            .count() != instructions.size();
    }
}
