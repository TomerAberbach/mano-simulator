package com.tomeraberbach.mano.simulation;

import java.util.function.Consumer;

/**
 * Class representing a microoperation in Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class Microoperation {
    /**
     * The description of this {@link Microoperation} in register transfer language.
     */
    private String description;

    /**
     * The {@link Consumer} which performs a microoperation on a {@link Computer}.
     */
    private Consumer<Computer> consumer;


    /**
     * @param description The description of this {@link Microoperation} in register transfer language.
     * @param consumer    The {@link Consumer} which performs a microoperation on a {@link Computer}.
     */
    public Microoperation(String description, Consumer<Computer> consumer) {
        this.description = description;
        this.consumer = consumer;
    }


    /**
     * @param computer {@link Computer} which will be passed into {@link Microoperation#consumer}.
     */
    public void execute(Computer computer) {
        consumer.accept(computer);
    }


    /**
     * @return {@link Microoperation#description}.
     */
    @Override
    public String toString() {
        return description;
    }
}
