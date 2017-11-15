package com.tomeraberbach.mano.application;

/* Tomer Aberbach
 * aberbat1@tcnj.edu
 * 11/12/2017
 * This code may be accessed and used by students at The College of New Jersey.
 */

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Scanner;

import static com.tomeraberbach.mano.application.Main.getTab;

/**
 * Class used to represent assembly code documents in the {@link Main} application.
 */
public class Code implements ChangeListener<String> {
    /**
     * {@link File} which this code was opened from or saved to.
     */
    private File file;

    /**
     * {@link String} which was last saved to {@link Code#file}.
     * Used to check for unsaved changes.
     */
    private String saved;

    /**
     * JavaFX {@link Tab} which this code is displayed in.
     */
    private Tab tab;

    /**
     * Initializes an empty code document titled 'Untitled'.
     */
    Code() {
        // Initializes an empty code document
        this.file = new File("Untitled");
        saved = "";
        tab = getTab(file.getName(), "", this);
    }

    /**
     * Initializes a code document from a {@link File}.
     * @param file {@link File} to load the code document from.
     * @throws FileNotFoundException Occurs if {@param file} could not be accessed.
     */
    Code(File file) throws FileNotFoundException {
        // Initializes a code document from a file
        this.file = file;
        saved = getFileText(file);
        tab = getTab(file.getName(), saved, this);
    }


    /**
     * @return {@link Code#file}
     */
    File file() {
        return file;
    }

    /**
     * {@link Code#file} setter.
     * @param file {@link File} which the code should be saved to in the future.
     */
    void setFile(File file) {
        this.file = file;
    }

    /**
     * @return {@link Tab}
     */
    Tab tab() {
        return tab;
    }


    /**
     * Saves the code document text found in the text area in {@link Code#tab} to {@link Code#file}.
     * @throws FileNotFoundException Occurs if {@link Code#file} could not be accessed.
     */
    void save() throws FileNotFoundException {
        // Saves the content in the code editor window into its file
        setFileText(file, ((TextArea)tab.getContent()).getText());
        saved = ((TextArea)tab.getContent()).getText();
        tab.setText(file.getName());
    }

    /**
     * Called when text in the code document's {@link Code#tab} was changed.
     * Adds an asterisk to the code document's {@link Tab} title to indicate it has unsaved changes.
     * @param observableValue {@link String} value observed in the text area.
     * @param old {@link String} representing the text prior to the change.
     * @param current {@link String} representing the text after the change.
     */
    @Override
    public void changed(ObservableValue<? extends String> observableValue, String old, String current) {
        // Adds an asterisk to the code tab if the code was altered
        tab.setText(file.getName() + (old.equals(current) ? "" : "*"));
    }


    /**
     * @param file {@link File} to get text from.
     * @return {@link String} representing text from {@param file}.
     * @throws FileNotFoundException Occurs if {@param file} could not be accessed.
     */
    private static String getFileText(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        StringBuilder string = new StringBuilder();

        // Loops to get all of the text in the given file
        while (scanner.hasNextLine()) {
            string.append(scanner.nextLine()).append("\n");
        }

        return string.toString();
    }

    /**
     * Sets the text of {@param file} to {@param text}.
     * @param file {@link File} to overwrite.
     * @param text {@link String} to overwrite {@link File} with.
     * @throws FileNotFoundException Occurs if {@param file} could not be accessed.
     */
    private static void setFileText(File file, String text) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);
        writer.write(text);
        writer.close();
    }
}