package com.tomeraberbach.mano.application;

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
        ClipboardContent content = new ClipboardContent();
        content.putString(text.getText());
        Clipboard.getSystemClipboard().setContent(content);
    }
}
