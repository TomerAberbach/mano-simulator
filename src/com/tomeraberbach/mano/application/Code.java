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

public class Code implements ChangeListener<String> {
    private File file;
    private String saved;
    private Tab tab;


    Code() {
        // Initializes an empty code document
        this.file = new File("Untitled");
        saved = "";
        tab = getTab(file.getName(), "", this);
    }

    Code(File file) throws FileNotFoundException {
        // Initializes a code document from a file
        this.file = file;
        saved = getFileText(file);
        tab = getTab(file.getName(), saved, this);
    }


    File file() {
        return file;
    }

    void setFile(File file) {
        this.file = file;
    }

    Tab tab() {
        return tab;
    }


    void save() throws FileNotFoundException {
        // Saves the content in the code editor window into its file
        setFileText(file, ((TextArea)tab.getContent()).getText());
        saved = ((TextArea)tab.getContent()).getText();
        tab.setText(file.getName());
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String old, String current) {
        // Adds an asterisk to the code tab if the code was altered
        tab.setText(file.getName() + (old.equals(current) ? "" : "*"));
    }


    private static String getFileText(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        StringBuilder string = new StringBuilder();

        // Loops to get all of the text in the given file
        while (scanner.hasNextLine()) {
            string.append(scanner.nextLine()).append("\n");
        }

        return string.toString();
    }

    private static void setFileText(File file, String text) throws FileNotFoundException {
        PrintWriter writer = new PrintWriter(file);
        writer.write(text);
        writer.close();
    }
}