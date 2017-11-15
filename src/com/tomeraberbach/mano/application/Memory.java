package com.tomeraberbach.mano.application;

/* Tomer Aberbach
 * aberbat1@tcnj.edu
 * 11/12/2017
 * This code may be accessed and used by students at The College of New Jersey.
 */

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;

/**
 * JavaFX controller for the RAM map application window.
 */
public class Memory {
    /**
     * The text area containing the RAM map.
     */
    @FXML private TextArea text;


    /**
     * Sets the RAM map to display in the application window
     * @param memory {@link String} representing the RAM map.
     */
    void setMemory(String memory) {
        text.setText(memory);
        text.setWrapText(true);
    }


    /**
     * Gets called when the 'Copy to Clipboard' button is pressed.
     * Simply copies the text in {@link Memory#text} to the clipboard.
     */
    @FXML
    private void clipboardOnAction() {
        // Copies the memory map to the clipboard
        ClipboardContent content = new ClipboardContent();
        content.putString(text.getText());
        Clipboard.getSystemClipboard().setContent(content);
    }
}
