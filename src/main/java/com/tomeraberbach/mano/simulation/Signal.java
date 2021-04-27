package com.tomeraberbach.mano.simulation;

/**
 * Class representing a signal in Mano's computer as detailed in:<br>
 * Computer System Architecture, 3rd edition<br>
 * By M. Morris Mano<br>
 * Published by Prentice-Hall, c 1993<br>
 * Chapter 5, pp 123-172.
 */
public class Signal {
    /**
     * The control function of this {@link Signal}.
     */
    private Control control;

    /**
     * The microoperation which is triggered by this {@link Signal#control}.
     */
    private Microoperation microoperation;


    /**
     * @param control        The control function of this {@link Signal}.
     * @param microoperation The microoperation which is triggered by this {@link Signal#control}.
     */
    public Signal(Control control, Microoperation microoperation) {
        this.control = control;
        this.microoperation = microoperation;
    }


    /**
     * @return {@link Signal#control}.
     */
    public Control control() {
        return control;
    }

    /**
     * @return {@link Signal#microoperation}.
     */
    public Microoperation microoperation() {
        return microoperation;
    }


    /**
     * @param computer {@link Computer} that will be assessed by the {@link Control#test(Computer)} of this {@link Signal#control}.
     * @return boolean representing if the {@code computer} meets the conditions of {@link Signal#control}.
     */
    public boolean test(Computer computer) {
        return control.test(computer);
    }


    /**
     * @return {@code control + ": " + microoperation}
     */
    @Override
    public String toString() {
        return control + ": " + microoperation;
    }
}
