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

public class Memory {
    @FXML private TextArea text;


    void setMemory(String memory) {
        text.setText(memory);
        text.setWrapText(true);
    }


    @FXML
    private void clipboardOnAction() {
        // Copies the memory map to the clipboard
        ClipboardContent content = new ClipboardContent();
        content.putString(text.getText());
        Clipboard.getSystemClipboard().setContent(content);
    }
}
