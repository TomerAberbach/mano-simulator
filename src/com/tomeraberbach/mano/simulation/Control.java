package com.tomeraberbach.mano.simulation;

import java.util.function.Predicate;

/**
 * Class representing a control function in Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class Control {
    /**
     * The description of this {@link Control} in register transfer language.
     */
    private String description;

    /**
     * The {@link Predicate} which tests a {@link Computer} for the conditions of this {@link Control}.
     */
    private Predicate<Computer> predicate;


    /**
     * @param description The description of this {@link Control} in register transfer language.
     * @param predicate   The {@link Predicate} which tests a {@link Computer} for the conditions of this {@link Control}.
     */
    public Control(String description, Predicate<Computer> predicate) {
        this.description = description;
        this.predicate = predicate;
    }


    /**
     * @param computer {@link Computer} whose conditions will be tested.
     * @return boolean representing if {@code computer} meets this {@link Control#predicate}.
     */
    public boolean test(Computer computer) {
        return predicate.test(computer);
    }


    /**
     * @return {@link Control#description}.
     */
    @Override
    public String toString() {
        return description;
    }
}
