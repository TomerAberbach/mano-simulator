package com.tomeraberbach.mano.simulation;

import com.tomeraberbach.mano.assembly.Program;
import com.tomeraberbach.mano.assembly.Token;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Class representing Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class Computer {
    /**
     * The {@link Signal} instances used for determining the microoperations required for an instruction.
     */
    public static final Set<Signal> SIGNALS;

    /**
     * The minimum number of bits of a binary representation.
     */
    public static final int MIN_SIZE = 1;

    /**
     * The maximum number of bits of a binary representation.
     */
    public static final int MAX_SIZE = (int)Math.floor(Math.log(Integer.MAX_VALUE) / Math.log(2.0)) - 1;

    /**
     * The minimum value of an unsigned binary representation.
     */
    public static final int MIN_VALUE = 0;

    /**
     * The number of bits required to represent an address.
     */
    public static final int ADDRESS_SIZE = 12;

    /**
     * The number of bits required to represent a value.
     */
    public static final int VALUE_SIZE = 16;

    /**
     * The number of bits required to represent an IO value.
     */
    public static final int IO_SIZE = 8;

    static {
        Set<Signal> set = new HashSet<>();

        set.add(new Signal(
            new Control("R'T0",
                computer -> !computer.r.value(0) && computer.sc() ==
                    0
            ),
            new Microoperation("AR <- PC",
                computer -> computer.ar.load(computer.pc.value())
            )
        ));

        set.add(new Signal(
            new Control("R'T1",
                computer -> !computer.r.value(0) && computer.sc() ==
                    1
            ),
            new Microoperation("IR <- M[AR]",
                computer -> computer.ir.load(computer.ram.read(computer.ar.value()))
            )
        ));

        set.add(new Signal(
            new Control("R'T1 + RT2 + D6T6(DR)' + D7I'T3(B4(AC(15))' + B3(AC(15)) + B2(AC)' + B1E') + D7IT3(B9(FGI) + B8(FGO))",
                computer -> // R'T1
                    (!computer.r.value(0) && computer.sc() == 1) ||
                        // RT2
                        (computer.r.value(0) && computer.sc() == 2) ||
                        // D6T6(DR)'
                        (computer.decoder.value(6) && computer.sc() == 6 && computer.dr.value() == 0) ||
                        // r = D7I'T3, BX = IR(X)
                        ((computer.decoder.value(7) && !computer.i.value(0) && computer.sc() == 3) && (
                            // rB4(AC(15))'
                            (computer.ir.value(4) && !computer.ac.value(15)) ||
                                // rB3(AC(15))
                                (computer.ir.value(3) && computer.ac.value(15)) ||
                                // rB2(AC)'
                                (computer.ir.value(2) && computer.ac.value() == 0) ||
                                // rB1E'
                                (computer.ir.value(1) && !computer.e.value(0)))) ||
                        // p = D7IT3
                        ((computer.decoder.value(7) && computer.i.value(0) && computer.sc() == 3) && (
                            // pB9(FGI)
                            (computer.ir.value(9) && computer.fgi.value(0)) ||
                                // pB8(FGO)
                                (computer.ir.value(8) && computer.fgo.value(0))))
            ),
            new Microoperation("PC <- PC + 1",
                computer -> computer.pc.increment()
            )
        ));

        set.add(new Signal(
            new Control("R'T2",
                computer -> !computer.r.value(0) && computer.sc() == 2
            ),
            new Microoperation("D0, ..., D7 <- Decode IR(12-14)",
                computer -> {
                    boolean[] bits = new boolean[8];
                    bits[computer.ir.value(12, 14)] = true;
                    computer.decoder.load(Computer.value(bits));
                }
            )
        ));

        set.add(new Signal(
            new Control("R'T2",
                computer -> !computer.r.value(0) && computer.sc() == 2
            ),
            new Microoperation("AR <- IR(0-11)",
                computer -> computer.ar.load(computer.ir.value(0, 11))
            )
        ));

        set.add(new Signal(
            new Control("R'T2",
                computer -> !computer.r.value(0) && computer.sc() == 2
            ),
            new Microoperation("I <- IR(15)",
                computer -> computer.i.load(computer.ir.value(15) ? 1 : 0)
            )
        ));

        set.add(new Signal(
            new Control("D7'IT3",
                computer -> !computer.decoder.value(7) && computer.i.value(0) && computer.sc() == 3
            ),
            new Microoperation("AR <- M[AR]",
                computer -> computer.ar.load(computer.ram.read(computer.ar.value()))
            )
        ));

        set.add(new Signal(
            new Control("T0'T1'T2'(IEN)(FGI + FGO)",
                computer -> computer.sc() != 0 && computer.sc() != 1 && computer.sc() != 2 &&
                    computer.ien.value(0) && (computer.fgi.value(0) || computer.fgo.value(0))
            ),
            new Microoperation("R <- 1",
                computer -> computer.r.load(1)
            )
        ));

        set.add(new Signal(
            new Control("RT0",
                computer -> computer.r.value(0) && computer.sc() == 0
            ),
            new Microoperation("AR <- 0",
                computer -> computer.ar.clear()
            )
        ));

        set.add(new Signal(
            new Control("RT0",
                computer -> computer.r.value(0) && computer.sc() == 0
            ),
            new Microoperation("TR <- PC",
                computer -> computer.tr.load(computer.pc.value())
            )
        ));

        set.add(new Signal(
            new Control("RT1",
                computer -> computer.r.value(0) && computer.sc() == 1
            ),
            new Microoperation("M[AR] <- TR",
                computer -> computer.ram.write(computer.ar.value(), computer.tr.value())
            )
        ));

        set.add(new Signal(
            new Control("RT1",
                computer -> computer.r.value(0) && computer.sc() == 1
            ),
            new Microoperation("PC <- 0",
                computer -> computer.pc.clear()
            )
        ));

        set.add(new Signal(
            new Control("RT2 + D7IT3B6",
                computer -> // RT2
                    (computer.r.value(0) && computer.sc() == 2) ||
                        // p = D7IT3, pB6
                        (computer.decoder.value(7) && computer.i.value(0) && computer.sc() == 3 && computer.ir.value(6))
            ),
            new Microoperation("IEN <- 0",
                computer -> computer.ien.clear()
            )
        ));

        set.add(new Signal(
            new Control("RT2",
                computer -> computer.r.value(0) && computer.sc() == 2
            ),
            new Microoperation("R <- 0",
                computer -> computer.r.clear()
            )
        ));

        set.add(new Signal(
            new Control("RT2 + D0T5 + D1T5 + D2T5 + D3T4 + D4T4 + D5T6 + D6T6 + D7I'T3 + D7IT3",
                computer -> // RT2
                    (computer.r.value(0) && computer.sc() == 2) ||
                        // D0T5
                        (computer.decoder.value(0) && computer.sc() == 5) ||
                        // D1T5
                        (computer.decoder.value(1) && computer.sc() == 5) ||
                        // D2T5
                        (computer.decoder.value(2) && computer.sc() == 5) ||
                        // D3T4
                        (computer.decoder.value(3) && computer.sc() == 4) ||
                        // D4T4
                        (computer.decoder.value(4) && computer.sc() == 4) ||
                        // D5T6
                        (computer.decoder.value(5) && computer.sc() == 6) ||
                        // D6T6
                        (computer.decoder.value(6) && computer.sc() == 6) ||
                        // r = D7I'T3
                        (computer.decoder.value(7) && !computer.i.value(0) && computer.sc() == 3) ||
                        // p = D7IT3
                        (computer.decoder.value(7) && computer.i.value(0) && computer.sc() == 3)
            ),
            new Microoperation("SC <- 0",
                computer -> computer.sc.setValue(0)
            )
        ));

        set.add(new Signal(
            new Control("D0T4 + D1T4 + D2T4 + D6T4",
                computer -> // D0T4
                    (computer.decoder.value(0) && computer.sc() == 4) ||
                        // D1T4
                        (computer.decoder.value(1) && computer.sc() == 4) ||
                        // D2T4
                        (computer.decoder.value(2) && computer.sc() == 4) ||
                        // D6T4
                        (computer.decoder.value(6) && computer.sc() == 4)
            ),
            new Microoperation("DR <- M[AR]",
                computer -> computer.dr.load(computer.ram.read(computer.ar.value()))
            )
        ));

        set.add(new Signal(
            new Control("D0T5",
                computer -> computer.decoder.value(0) && computer.sc() == 5
            ),
            new Microoperation("AC <- AC ^ DR",
                computer -> computer.ac.and(computer.dr.value())
            )
        ));

        set.add(new Signal(
            new Control("D1T5",
                computer -> computer.decoder.value(1) && computer.sc() == 5
            ),
            new Microoperation("AC <- AC + DR",
                computer -> computer.ac.add(computer.dr.value(), computer.e)
            )
        ));

        set.add(new Signal(
            new Control("D2T5",
                computer -> computer.decoder.value(2) && computer.sc() == 5
            ),
            new Microoperation("AC <- DR",
                computer -> computer.ac.load(computer.dr.value())
            )
        ));

        set.add(new Signal(
            new Control("D3T4",
                computer -> computer.decoder.value(3) && computer.sc() == 4
            ),
            new Microoperation("M[AR] <- AC",
                computer -> computer.ram.write(computer.ar.value(), computer.ac.value())
            )
        ));

        set.add(new Signal(
            new Control("D4T4 + D5T6",
                computer -> // D4T4
                    (computer.decoder.value(4) && computer.sc() == 4) ||
                        // D5T6
                        (computer.decoder.value(5) && computer.sc() == 6)
            ),
            new Microoperation("PC <- AR",
                computer -> computer.pc.load(computer.ar.value())
            )
        ));

        set.add(new Signal(
            new Control("D5T4",
                computer -> computer.decoder.value(5) && computer.sc() == 4
            ),
            new Microoperation("M[AR] <- PC",
                computer -> computer.ram.write(computer.ar.value(), computer.pc.value())
            )
        ));

        set.add(new Signal(
            new Control("D5T5",
                computer -> computer.decoder.value(5) && computer.sc() == 5
            ),
            new Microoperation("AR <- AR + 1",
                computer -> computer.ar.increment()
            )
        ));

        set.add(new Signal(
            new Control("D6T5",
                computer -> computer.decoder.value(6) && computer.sc() == 5
            ),
            new Microoperation("DR <- DR + 1",
                computer -> computer.dr.increment()
            )
        ));

        set.add(new Signal(
            new Control("D6T6",
                computer -> computer.decoder.value(6) && computer.sc() == 6
            ),
            new Microoperation("M[AR] <- DR",
                computer -> computer.ram.write(computer.ar.value(), computer.dr.value())
            )
        ));

        set.add(new Signal(
            new Control("rB11",
                computer -> computer.decoder.value(7) && !computer.i.value(0) && computer.sc() == 3 && computer.ir.value(11)
            ),
            new Microoperation("AC <- 0",
                computer -> computer.ac.clear()
            )
        ));

        set.add(new Signal(
            new Control("rB10",
                computer -> computer.decoder.value(7) && !computer.i.value(0) && computer.sc() == 3 && computer.ir.value(10)
            ),
            new Microoperation("E <- 0",
                computer -> computer.e.clear()
            )
        ));

        set.add(new Signal(
            new Control("rB9",
                computer -> computer.decoder.value(7) && !computer.i.value(0) && computer.sc() == 3 && computer.ir.value(9)
            ),
            new Microoperation("AC <- (AC)'",
                computer -> computer.ac.complement()
            )
        ));

        set.add(new Signal(
            new Control("rB8",
                computer -> computer.decoder.value(7) && !computer.i.value(0) && computer.sc() == 3 && computer.ir.value(8)
            ),
            new Microoperation("E <- E'",
                computer -> computer.e.complement()
            )
        ));

        set.add(new Signal(
            new Control("rB7",
                computer -> computer.decoder.value(7) && !computer.i.value(0) && computer.sc() == 3 && computer.ir.value(7)
            ),
            new Microoperation("AC <- shr AC",
                computer -> computer.ac.shiftRight(computer.e)
            )
        ));

        set.add(new Signal(
            new Control("rB6",
                computer -> computer.decoder.value(7) && !computer.i.value(0) && computer.sc() == 3 && computer.ir.value(6)
            ),
            new Microoperation("AC <- shl AC",
                computer -> computer.ac.shiftLeft(computer.e)
            )
        ));

        set.add(new Signal(
            new Control("rB5",
                computer -> computer.decoder.value(7) && !computer.i.value(0) && computer.sc() == 3 && computer.ir.value(5)
            ),
            new Microoperation("AC <- AC + 1",
                computer -> computer.ac.increment()
            )
        ));

        set.add(new Signal(
            new Control("rB0",
                computer -> computer.decoder.value(7) && !computer.i.value(0) && computer.sc() == 3 && computer.ir.value(0)
            ),
            new Microoperation("S <- 0",
                computer -> computer.s.clear()
            )
        ));

        set.add(new Signal(
            new Control("pB11",
                computer -> computer.decoder.value(7) && computer.i.value(0) && computer.sc() == 3 && computer.ir.value(11)
            ),
            new Microoperation("AC(0-7) <- INPR",
                computer -> {
                    boolean[] bits = new boolean[computer.ac.size()];

                    for (int i = 0; i <= 7; i++) {
                        bits[i] = computer.inpr.value(i);
                    }

                    for (int i = 8; i < computer.ac.size(); i++) {
                        bits[i] = computer.ac.value(i);
                    }

                    computer.ac.load(Computer.value(bits));
                }
            )
        ));

        set.add(new Signal(
            new Control("pB11",
                computer -> computer.decoder.value(7) && computer.i.value(0) && computer.sc() == 3 && computer.ir.value(11)
            ),
            new Microoperation("FGI <- 0",
                computer -> computer.fgi.clear()
            )
        ));

        set.add(new Signal(
            new Control("pB10",
                computer -> computer.decoder.value(7) && computer.i.value(0) && computer.sc() == 3 && computer.ir.value(10)
            ),
            new Microoperation("OUTR <- AC(0-7)",
                computer -> computer.outr.load(computer.ac.value(0, 7))
            )
        ));

        set.add(new Signal(
            new Control("pB10",
                computer -> computer.decoder.value(7) && computer.i.value(0) && computer.sc() == 3 && computer.ir.value(10)
            ),
            new Microoperation("FGO <- 0",
                computer -> computer.fgo.clear()
            )
        ));

        set.add(new Signal(
            new Control("pB7",
                computer -> computer.decoder.value(7) && computer.i.value(0) && computer.sc() == 3 && computer.ir.value(7)
            ),
            new Microoperation("IEN <- 1",
                computer -> computer.ien.load(1)
            )
        ));

        SIGNALS = Collections.unmodifiableSet(set);
    }


    /**
     * The current T state of the sequence counter in this {@link Computer}.
     */
    private SimpleIntegerProperty sc;


    /**
     * The {@link RAM} of this {@link Computer}.
     */
    private RAM ram;

    /**
     * The result of decoding IR(12-14) in this {@link Computer}.
     */
    private Register decoder;

    /**
     * The program counter of this {@link Computer}.
     */
    private Register pc;

    /**
     * The address register of this {@link Computer}.
     */
    private Register ar;

    /**
     * The instruction register of this {@link Computer}.
     */
    private Register ir;

    /**
     * The data register of this {@link Computer}.
     */
    private Register dr;

    /**
     * The accumulator of this {@link Computer}.
     */
    private Register ac;

    /**
     * The temporary register of this {@link Computer}.
     */
    private Register tr;

    /**
     * The input register of this {@link Computer}.
     */
    private Register inpr;

    /**
     * The output register of this {@link Computer}.
     */
    private Register outr;

    /**
     * The indirect addressing flip-flop of this {@link Computer}.
     */
    private Register i;

    /**
     * The interrupt raised flip-flop of this {@link Computer}.
     */
    private Register r;

    /**
     * The interrupt enable flip-flop of this {@link Computer}.
     */
    private Register ien;

    /**
     * The carry bit flip-flop of this {@link Computer}.
     */
    private Register e;

    /**
     * The input flag flip-flop of this {@link Computer}.
     */
    private Register fgi;

    /**
     * The output flag flip-flop of this {@link Computer}.
     */
    private Register fgo;

    /**
     * The stop or go flip-flop of this {@link Computer}.
     */
    private Register s;

    /**
     * {@link ArrayDeque} {@link Microoperation} instances which are next in line to execute.
     */
    private ArrayDeque<Microoperation> microoperations;


    /**
     * Constructor which initializes this {@link Computer} with T = 0, cleared {@link RAM}, and all {@link Register} instances cleared.
     */
    public Computer() {
        sc = new SimpleIntegerProperty(0);
        this.ram = new RAM();
        decoder = new Register(8);
        pc = new Register(ADDRESS_SIZE);
        ar = new Register(ADDRESS_SIZE);
        ir = new Register(VALUE_SIZE);
        tr = new Register(VALUE_SIZE);
        dr = new Register(VALUE_SIZE);
        ac = new Register(VALUE_SIZE);
        inpr = new Register(IO_SIZE);
        outr = new Register(IO_SIZE);
        i = new Register();
        r = new Register();
        ien = new Register();
        e = new Register();
        fgi = new Register();
        fgo = new Register();
        s = new Register();
        microoperations = new ArrayDeque<>();
    }

    /**
     * @param value Unsigned integer value to check if it can be represented with {@code size} bits.
     * @param size  Integer number of bits to check if {@code value} can be represented by.
     * @throws IllegalArgumentException Thrown if calling {@link Computer#isValidValue(int, int)} results in {@code false}.
     */
    public static void validateValue(int value, int size) {
        if (!isValidValue(value, size)) {
            throw new IllegalArgumentException("Invalid value " + value + " for size of " + size + ". It must be on the interval [" + MIN_VALUE + ", " + maxValue(size) + "].");
        }
    }

    /**
     * @param value Unsigned integer value to check if it can be represented with {@code size} bits.
     * @param size  Integer number of bits to check if {@code value} can be represented by.
     * @return boolean representing if {@code value} can be represented with {@code size} bits.
     */
    private static boolean isValidValue(int value, int size) {
        return value >= MIN_VALUE && value <= maxValue(size);
    }

    /**
     * @param size Integer number of bits to compute the maximum possible unsigned value of.
     * @return Unsigned integer representing the maximum possible value representable by {@code size} bits.
     */
    public static int maxValue(int size) {
        return (int)Math.pow(2, size) - 1;
    }

    /**
     * @param size Integer number bits to check the validity of.
     * @throws IllegalArgumentException Thrown if calling {@link Computer#isValidSize(int)} results in {@code false}.
     */
    public static void validateSize(int size) {
        if (!isValidSize(size)) {
            throw new IllegalArgumentException("Invalid size " + size + ". It must be on the interval [" + (MIN_SIZE - 1) + ", " + MAX_SIZE + "].");
        }
    }

    /**
     * @param size Integer number bits to check the validity of.
     * @return boolean representing if {@code size} is a valid number of bits.
     */
    private static boolean isValidSize(int size) {
        return size >= MIN_SIZE - 1 && size <= MAX_SIZE;
    }

    /**
     * @param digit Binary digit index to check the validity of in a binary representation consisting of {@code size} bits.
     * @param size  Integer number of bits for which to check {@code digit} is a valid index of.
     * @throws IllegalArgumentException Thrown if calling {@link Computer#isValidDigit(int, int)} results in {@code false}.
     */
    public static void validateDigit(int digit, int size) {
        if (!isValidDigit(digit, size)) {
            throw new IllegalArgumentException("Invalid digit " + digit + " for size of " + size + ". It must be on the interval [" + MIN_SIZE + ", " + size + ").");
        }
    }

    /**
     * @param digit Binary digit index to check the validity of in a binary representation consisting of {@code size} bits.
     * @param size  Integer number of bits for which to check {@code digit} is a valid index of.
     * @return boolean representing if {@code digit} is a valid binary digit index in a binary representation consisting of {@code size} bits.
     */
    private static boolean isValidDigit(int digit, int size) {
        return digit >= MIN_SIZE - 1 && digit < size;
    }

    /**
     * @param bits Array of booleans representing the bits of an unsigned integer.
     * @return Integer represented by {@code bits}.
     */
    public static int value(boolean[] bits) {
        int number = 0;

        for (int i = 0; i < bits.length; i++) {
            number += bits[i] ? Math.pow(2, i) : 0;
        }

        return number;
    }

    /**
     * @param digit The least to most significant zero-based binary digit index of {@code value} to get.
     * @param value Integer value to get the {@code digit}th bit of.
     * @return boolean representing if the binary digit at index {@code digit} of {@code value} is a {@code 1}.
     */
    public static boolean bit(int digit, int value) {
        return (value & (int)Math.pow(2, digit)) > 0;
    }

    /**
     * @return {@link IntegerProperty#get()} of {@link Computer#sc}.
     */
    public int sc() {
        return sc.get();
    }

    /**
     * @return {@link Computer#sc}.
     */
    public SimpleIntegerProperty scProperty() {
        return sc;
    }

    /**
     * @return {@link Computer#ram}.
     */
    public RAM ram() {
        return ram;
    }

    /**
     * @return {@link Computer#pc}.
     */
    public Register pc() {
        return pc;
    }

    /**
     * @return {@link Computer#ar}.
     */
    public Register ar() {
        return ar;
    }

    /**
     * @return {@link Computer#ir}.
     */
    public Register ir() {
        return ir;
    }

    /**
     * @return {@link Computer#dr}.
     */
    public Register dr() {
        return dr;
    }

    /**
     * @return {@link Computer#ac}.
     */
    public Register ac() {
        return ac;
    }

    /**
     * @return {@link Computer#tr}.
     */
    public Register tr() {
        return tr;
    }

    /**
     * @return {@link Computer#inpr}.
     */
    public Register inpr() {
        return inpr;
    }

    /**
     * @return {@link Computer#outr}.
     */
    public Register outr() {
        return outr;
    }

    /**
     * @return {@link Computer#i}.
     */
    public Register i() {
        return i;
    }

    /**
     * @return {@link Computer#r}.
     */
    public Register r() {
        return r;
    }

    /**
     * @return {@link Computer#ien}.
     */
    public Register ien() {
        return ien;
    }

    /**
     * @return {@link Computer#e}.
     */
    public Register e() {
        return e;
    }

    /**
     * @return {@link Computer#fgi}.
     */
    public Register fgi() {
        return fgi;
    }

    /**
     * @return {@link Computer#fgo}.
     */
    public Register fgo() {
        return fgo;
    }

    /**
     * @return {@link Computer#s}.
     */
    public Register s() {
        return s;
    }

    /**
     * @return {@link Computer#microoperations}.
     */
    public ArrayDeque<Microoperation> microoperations() {
        return microoperations;
    }

    /**
     * {@link Computer#s} will be loaded with {@code 1}.
     *
     * @param program {@link Program} to load this {@link Computer} with.
     */
    public void load(Program program) {
        reset();
        pc.load(program.start());
        program.instructions().forEach(instruction -> {
            ram.values().get(instruction.address()).setValue(instruction.code());
            ram.values().get(instruction.address()).setInstruction(Arrays.stream(instruction.tokens()).map(Token::lexeme).collect(Collectors.joining(" ")));
        });
        program.labels().forEach(label -> ram.values().get(label.address()).setLabel(label.token().lexeme()));

        s.load(1);
    }

    /**
     * Resets this {@link Computer} to its initial state which was right after its instantiation using {@link Computer#Computer()}.
     */
    public void reset() {
        sc.setValue(0);
        decoder.clear();

        for (int i = 0; i <= ram.maxAddress(); i++) {
            ram.write(i, 0);
            ram.values().get(i).setLabel("");
        }

        pc.clear();
        ar.clear();
        ir.clear();
        tr.clear();
        dr.clear();
        ac.clear();
        inpr.clear();
        outr.clear();
        i.clear();
        r.clear();
        ien.clear();
        e.clear();
        fgi.clear();
        fgo.clear();

        microoperations.clear();
    }

    /**
     * Gets the next batch {@link Microoperation} instances to be executed and adds them to this {@link Computer#microoperations}.
     * {@link Computer#sc} is incremented at the end of this method.
     *
     * @throws IllegalStateException Thrown when {@link Computer#s} is unasserted.
     */
    public void tick() throws IllegalStateException {
        if (s.value(0)) {
            SIGNALS.stream()
                .filter(signal -> signal.test(this))
                .map(Signal::microoperation)
                .forEach(microoperations::push);

            sc.setValue(sc.get() + 1);
        }
    }
}
