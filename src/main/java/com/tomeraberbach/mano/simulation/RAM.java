package com.tomeraberbach.mano.simulation;

import com.tomeraberbach.mano.Utilities;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.ArrayList;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

/**
 * Class representing RAM in Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class RAM {
    /**
     * The integer number of bits the addresses in this {@link RAM} support.
     */
    private int addressSize;

    /**
     * The integer number of bits the valuesProperty in this {@link RAM} support.
     */
    private int valueSize;

    /**
     * The {@link Integer} values currently in this {@link RAM}.
     */
    private ArrayList<Memory> values;

    /**
     * The {@link Integer} values property currently in this {@link RAM}.
     */
    private SimpleObjectProperty<ObservableList<Memory>> valuesProperty;

    /**
     * Constructor which is the equivalent of calling {@link RAM#RAM(int, int)} arguments {@link Computer#ADDRESS_SIZE} and {@link Computer#VALUE_SIZE}.
     */
    public RAM() {
        this(Computer.ADDRESS_SIZE, Computer.VALUE_SIZE);
    }

    /**
     * {@code addressSize} and {@code valueSize} must pass {@link Computer#validateSize(int)} or an {@link IllegalArgumentException} is thrown.
     *
     * @param addressSize The integer number of bits the addresses in this {@link RAM} support.
     * @param valueSize   The integer number of bits the valuesProperty in this {@link RAM} support.
     */
    public RAM(int addressSize, int valueSize) {
        Computer.validateSize(valueSize);
        Computer.validateSize(addressSize);

        this.addressSize = addressSize;
        this.valueSize = valueSize;

        values = IntStream.rangeClosed(0, maxAddress()).sequential().mapToObj(i -> new Memory("", Utilities.hex(i, 3), 0, "")).collect(Collectors.toCollection(ArrayList::new));
        valuesProperty = new SimpleObjectProperty<>(FXCollections.observableArrayList(values));
    }

    /**
     * @return Integer representing the maximum unsigned value an address in this {@link RAM} can be.
     */
    public int maxAddress() {
        return Computer.maxValue(addressSize);
    }


    /**
     * @return {@link RAM#values}.
     */
    public ArrayList<Memory> values() {
        return values;
    }

    /**
     * @return {@link RAM#valuesProperty}.
     */
    public SimpleObjectProperty<ObservableList<Memory>> valuesProperty() {
        return valuesProperty;
    }

    /**
     * {@code address} and {@code value} must pass {@link RAM#validate(int, int)} or an {@link IllegalArgumentException} is thrown.
     *
     * @param address Integer address to write to in this {@link RAM}.
     * @param value   Integer value to write to {@code address} in this {@link RAM}.
     */
    public void write(int address, int value) {
        validate(address, value);
        values.get(address).setValue(value);
        values.get(address).setInstruction("");
    }

    /**
     * Validates that this {@link RAM} has address {@code address} and could hold {@code value}.
     *
     * @param address Integer address to test if this {@link RAM} has.
     * @param value   Integer value to test if this {@link RAM} can hold.
     */
    private void validate(int address, int value) {
        validateAddress(address);
        validateValue(value);
    }

    /**
     * Validates that this {@link RAM} has address {@code address}.
     *
     * @param address Integer address to test if this {@link RAM} has.
     */
    private void validateAddress(int address) {
        Computer.validateValue(address, addressSize);
    }

    /**
     * Validates that this {@link RAM} could hold {@code value}.
     *
     * @param value Integer value to test if this {@link RAM} can hold.
     */
    private void validateValue(int value) {
        Computer.validateValue(value, valueSize);
    }

    /**
     * {@code address} must pass {@link RAM#validateAddress(int)} or an {@link IllegalArgumentException} is thrown.
     *
     * @param address Integer address to read from in this {@link RAM}.
     * @return Integer value which is at address {@code address} in this {@link RAM}.
     */
    public int read(int address) {
        validateAddress(address);
        return values.get(address).value();
    }


    /**
     * @return The space delimited {@link String} of the four character hexadecimal representations each value in this {@link RAM#valuesProperty}.
     */
    @Override
    public String toString() {
        return values.stream()
            .sequential()
            .map(Memory::getHex)
            .reduce((string1, string2) -> string1 + " " + string2)
            .orElse("")
            .trim();
    }
}
