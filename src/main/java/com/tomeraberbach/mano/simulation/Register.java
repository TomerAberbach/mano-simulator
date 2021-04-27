package com.tomeraberbach.mano.simulation;

import com.tomeraberbach.mano.Utilities;
import javafx.beans.binding.StringBinding;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

import java.util.stream.IntStream;

/**
 * Class representing a register in Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class Register {
    /**
     * The integer number of bits this {@link Register} supports.
     */
    private int size;

    /**
     * The value currently in this {@link Register}.
     */
    private SimpleIntegerProperty value;


    /**
     * Constructor which is the equivalent of calling {@link Register#Register(int)} with an argument of {@code 1}.
     */
    public Register() {
        this(1);
    }

    /**
     * {@code size} must pass {@link Computer#validateSize(int)} or an {@link IllegalArgumentException} is thrown.
     *
     * @param size The integer number of bits this {@link Register} supports.
     */
    public Register(int size) {
        Computer.validateSize(size);

        this.size = size;
        value = new SimpleIntegerProperty(0);
    }


    /**
     * @return {@link Register#size}.
     */
    public int size() {
        return size;
    }

    /**
     * @return {@link Register#value}.
     */
    public SimpleIntegerProperty valueProperty() {
        return value;
    }

    /**
     * @return {@link IntegerProperty#get()} of {@link Register#value}.
     */
    public int value() {
        return value.get();
    }

    /**
     * @param a The least to most significant zero-based binary digit index of this {@link Register#value} to start at.
     * @param b The least to most significant zero-based binary digit index of this {@link Register#value} to end at.
     * @return integer whose binary representation consists of binary digits on the interval [a, b] of this {@link Register#value}.
     */
    public int value(int a, int b) {
        Computer.validateDigit(a, size);
        Computer.validateDigit(b, size);

        int value = 0;

        for (int i = a; i <= b; i++) {
            value += value(i) ? Math.pow(2, i - a) : 0;
        }

        return value;
    }

    /**
     * {@code digit} must pass {@link Computer#validateDigit(int, int)} or an {@link IllegalArgumentException} is thrown.
     *
     * @param digit The least to most significant zero-based binary digit index of this {@link Register#value} to get.
     * @return boolean representing if the binary digit at index {@code digit} is a {@code 1}.
     */
    public boolean value(int digit) {
        Computer.validateDigit(digit, size);
        return Computer.bit(digit, value.get());
    }

    /**
     * @return {@link StringBinding} representing {@link Register#value} as a hexadecimal {@link String}.
     */
    public StringBinding hexadecimalStringBinding() {
        return new StringBinding() {
            { super.bind(value); }

            @Override
            protected String computeValue() {
                return Utilities.hex(value.get(), size / 4);
            }
        };
    }

    /**
     * @return {@link StringBinding} representing {@link Register#value} as character {@link String}.
     */
    public StringBinding characterStringBinding() {
        return new StringBinding() {
            { super.bind(value); }

            @Override
            protected String computeValue() {
                int v = value.get();
                return v == 0 ? "" : String.valueOf((char)v);
            }
        };
    }


    /**
     * Loads this {@link Register} with {@code 0}.
     */
    public void clear() {
        load(0);
    }

    /**
     * {@code value} must pass {@link Register#validate(int)} or an {@link IllegalArgumentException} is thrown.
     *
     * @param value Integer value to load into this {@link Register}.
     */
    public void load(int value) {
        validate(value);
        this.value.setValue(value);
    }

    /**
     * Validates that this {@link Register} could hold {@code value}.
     *
     * @param value Integer value to test if this {@link Register} can hold.
     */
    private void validate(int value) {
        Computer.validateValue(value, size);
    }

    /**
     * {@code value} must pass {@link Register#validate(int)} or an {@link IllegalArgumentException} is thrown.
     *
     * @param value Integer value to and ({@code &}) this {@link Register} with.
     */
    public void and(int value) {
        validate(value);
        this.value.setValue(this.value.get() & value);
    }

    /**
     * Complements this {@link Register}.
     */
    public void complement() {
        boolean[] bits = new boolean[size];
        IntStream.range(0, size).sequential().forEach(i -> bits[i] = !value(i));
        load(Computer.value(bits));
    }

    /**
     * {@code value} must pass {@link Register#validate(int)} or an {@link IllegalArgumentException} is thrown.
     *
     * @param value Integer value to add to this {@link Register}.
     * @param e     {@link Register} to load with the carry out of this addition operation.
     */
    public void add(int value, Register e) {
        validate(value);
        e.value.setValue(this.value.get() + value > max() ? 1 : 0);
        this.value.setValue((this.value.get() + value) % (max() + 1));
    }

    /**
     * @return Integer representing the maximum unsigned value this {@link Register} can hold.
     */
    public int max() {
        return Computer.maxValue(size);
    }

    /**
     * Increments this {@link Register}.
     */
    public void increment() {
        add(1);
    }

    /**
     * {@code value} must pass {@link Register#validate(int)} or an {@link IllegalArgumentException} is thrown.
     *
     * @param value Integer value to add to this {@link Register}.
     */
    public void add(int value) {
        validate(value);
        this.value.setValue((this.value.get() + value) % (max() + 1));
    }

    /**
     * Shifts the bits in this {@link Register} and {@code e} left where {@code e} is considered to be the most significant bit.
     *
     * @param e {@link Register} to load with the last bit in this {@link Register} and whose first bit will be loaded into the first bit of this {@link Register}.
     */
    public void shiftLeft(Register e) {
        int value = e.value.get();

        for (int i = 0; i < size - 1; i++) {
            value += value(i) ? Math.pow(2, i + 1.0) : 0;
        }

        e.value.setValue(value(size - 1) ? 1 : 0);
        this.value.setValue(value);
    }

    /**
     * Shifts the bits in this {@link Register} and {@code e} right where {@code e} is considered to be the most significant bit.
     *
     * @param e {@link Register} to load with the first bit in this {@link Register} and whose first bit will be loaded into the last bit of this {@link Register}.
     */
    public void shiftRight(Register e) {
        int value = e.value.get() * (int)Math.pow(2, size - 1.0);

        for (int i = 1; i < size; i++) {
            value += value(i) ? Math.pow(2, i - 1.0) : 0;
        }

        e.value.setValue(value(0) ? 1 : 0);
        this.value.setValue(value);
    }


    /**
     * @return The four character hexadecimal representation of this {@link Register#value}.
     */
    @Override
    public String toString() {
        return Utilities.hex(value.get(), 4);
    }
}
