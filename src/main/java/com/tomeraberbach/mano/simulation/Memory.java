package com.tomeraberbach.mano.simulation;

import com.tomeraberbach.mano.Utilities;

/**
 * Class representing memory in {@link RAM} in Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class Memory {
    /**
     * The label at this memory location if any.
     */
    private String label;

    /**
     * The address of this memory location in three digit hexadecimal.
     */
    private String address;

    /**
     * The value of at this memory location.
     */
    private int value;

    /**
     * The corresponding source code instruction at this memory location if any.
     */
    private String instruction;

    /**
     * @param label       The label at this memory location if any.
     * @param address     The address of this memory location in three digit hexadecimal.
     * @param value       The value of at this memory location.
     * @param instruction The corresponding source code instruction at this memory location if any.
     */
    public Memory(String label, String address, int value, String instruction) {
        this.label = label;
        this.address = address;
        this.value = value;
        this.instruction = instruction;
    }


    /**
     * @return {@link Memory#label}.
     */
    public String getLabel() {
        return label;
    }

    /**
     * @param label {@link Memory#label}
     */
    public void setLabel(String label) {
        this.label = label;
    }

    /**
     * @return {@link Memory#address}.
     */
    public String getAddress() {
        return address;
    }

    /**
     * @param address {@link Memory#address}.
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * @return {@link Memory#value}.
     */
    public int value() {
        return value;
    }

    /**
     * @param value {@link Memory#value}.
     */
    public void setValue(int value) {
        this.value = value;
    }

    /**
     * @return {@link Memory#instruction}.
     */
    public String getInstruction() {
        return instruction;
    }

    /**
     * @param instruction {@link Memory#instruction}.
     */
    public void setInstruction(String instruction) {
        this.instruction = instruction;
    }

    /**
     * @return The value at this memory location in four digit hexadecimal.
     */
    public String getHex() {
        return Utilities.hex(value, 4);
    }
}
