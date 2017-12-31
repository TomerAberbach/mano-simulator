/*
 * Tomer Aberbach
 * aberbat1@tcnj.edu
 * 12/30/2017
 * Students at The College of New Jersey are granted
 * unlimited use and access to this application and its code.
 */

package com.tomeraberbach.mano.application;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Tab;
import javafx.scene.control.TextArea;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * Class used to represent assembly code documents in the {@link Main} application.
 */
public class Code implements ChangeListener<String> {
    /**
     * {@link Font} to use for the assembly code.
     */
    public static final Font CODE_FONT = new Font("Courier New", 13.0);

    /**
     * {@link File} which this code was opened from or saved to.
     */
    private File file;

    /**
     * {@link String} which was last saved to {@link Code#file}.
     */
    private String saved;

    /**
     * JavaFX {@link Tab} which this code is displayed in.
     */
    private Tab tab;


    /**
     * Initializes an empty code document titled 'Untitled'.
     */
    public Code() {
        this(new File("Untitled"), "");
    }

    /**
     * @param file {@link File} which this code was opened from or saved to.
     * @param text {@link String} which was last saved to {@link Code#file}.
     */
    public Code(File file, String text) {
        this.file = file;
        saved = text;

        // Creates a tab with a text area in it
        TextArea textArea = new TextArea();
        textArea.setText(saved);
        textArea.setFont(CODE_FONT);
        textArea.textProperty().addListener(this);

        tab = new Tab();
        tab.setText(file.getName());
        tab.setContent(textArea);
    }

    /**
     * Initializes a code document from a {@link File}.
     *
     * @param file {@link File} to load the code document from.
     * @throws FileNotFoundException Occurs if {@code file} could not be accessed.
     */
    public Code(File file) throws FileNotFoundException {
        this(file, text(file));
    }

    /**
     * @param file {@link File} to get text from.
     * @return {@link String} representing text from {@code file}.
     * @throws FileNotFoundException Thrown if {@code file} could not be accessed.
     */
    private static String text(File file) throws FileNotFoundException {
        Scanner scanner = new Scanner(file);
        StringBuilder builder = new StringBuilder();

        if (System.getProperty("os.name").toLowerCase().contains("windows")) {
            // Loops to get all of the text in the given file
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine()).append("\n\r");
            }
        } else {
            // Loops to get all of the text in the given file
            while (scanner.hasNextLine()) {
                builder.append(scanner.nextLine()).append("\n");
            }
        }

        return builder.toString();
    }

    /**
     * @return {@link Code#file}.
     */
    public File file() {
        return file;
    }

    /**
     * @return {@link Code#tab}.
     */
    public Tab tab() {
        return tab;
    }

    /**
     * Saves the code document text found in the text area in {@link Code#tab} to {@link Code#file}.
     *
     * @param stage {@link Stage} where this {@link Code} is being displayed.
     * @return boolean representing if this {@link Code} was successfully saved.
     */
    public boolean save(Stage stage) {
        if (file.exists()) {
            // Saves the content in the code editor window into its file
            try {
                PrintWriter writer = new PrintWriter(file);
                writer.write(((TextArea)tab.getContent()).getText());
                writer.close();
            } catch (FileNotFoundException e) {
                return false;
            }

            saved = ((TextArea)tab.getContent()).getText();
            tab.setText(file.getName());
            return true;
        } else {
            return saveAs(stage);
        }
    }

    /**
     * Saves the code document text found in the text area in {@link Code#tab} to a {@link File} choosen by the user.
     *
     * @param stage {@link Stage} where this {@link Code} is being displayed.
     * @return boolean representing if this {@link Code} was successfully saved.
     */
    public boolean saveAs(Stage stage) {
        // Brings up a file dialog for choosing a save location
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Save As...");

        // Gets the new file path
        File file = fileChooser.showSaveDialog(stage);

        // Checks if a new file was chosen
        if (file != null) {
            try {
                if (file.createNewFile()) {
                    // Sets the code document's file
                    this.file = file;

                    // Saves the code
                    return save(stage);
                }
            } catch (IOException ignored) {
            }
        }

        return false;
    }

    /**
     * @param stage {@link Stage} where this {@link Code} is being displayed.
     * @return boolean representing if this {@link Code} should be closed.
     */
    public boolean close(Stage stage) {
        if (tab.getText().endsWith("*")) {
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.setTitle("Unsaved File (" + file.getName() + ")");
            alert.setContentText("Would you like to save before closing?");
            alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);

            BooleanProperty property = new SimpleBooleanProperty(false);

            alert.showAndWait().ifPresent(t -> {
                if (t == ButtonType.YES) {
                    property.setValue(save(stage));
                } else if (t == ButtonType.NO) {
                    property.setValue(true);
                }
            });

            return property.get();
        } else {
            return true;
        }
    }

    /**
     * Called when text in the code document's {@link Code#tab} was changed.
     * Adds an asterisk to the code document's {@link Tab} title to indicate it has unsaved changes.
     *
     * @param observableValue {@link String} value observed in the text area.
     * @param old             {@link String} representing the text prior to the change.
     * @param current         {@link String} representing the text after the change.
     */
    @Override
    public void changed(ObservableValue<? extends String> observableValue, String old, String current) {
        // Adds an asterisk to the code tab if the code was altered
        tab.setText(file.getName() + (old.equals(current) ? "" : "*"));
    }
}