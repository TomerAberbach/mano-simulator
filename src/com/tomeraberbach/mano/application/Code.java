package com.tomeraberbach.mano.application;

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
        this.file = new File("Untitled");
        saved = "";
        tab = getTab(file.getName(), "", this);
    }

    Code(File file) throws FileNotFoundException {
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
        setFileText(file, ((TextArea)tab.getContent()).getText());
        saved = ((TextArea)tab.getContent()).getText();
        tab.setText(file.getName());
    }

    @Override
    public void changed(ObservableValue<? extends String> observableValue, String old, String current) {
        tab.setText(file.getName() + (old.equals(current) ? "" : "*"));
    }


    private static String getFileText(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        StringBuilder string = new StringBuilder();

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