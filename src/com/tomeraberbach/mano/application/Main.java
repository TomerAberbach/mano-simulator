package com.tomeraberbach.mano.application;

import com.tomeraberbach.mano.assembly.Compiler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Font;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

public class Main extends Application {
    private static final String TITLE = "Mano Assembler - Tomer Aberbach";
    private static final Font CODE_FONT = new Font("Courier New", 13.0);


    private Stage stage;
    private ArrayList<Code> codes;
    @FXML private TextArea errorFX;
    @FXML private TabPane tabsFX;


    public Main() {
        codes = new ArrayList<>();
    }


    @FXML
    private void assembleOnAction() {
        errorFX.setText("");
        StringBuilder errorBuilder = new StringBuilder();
        boolean error = false;

        Compiler[] compilers = new Compiler[tabsFX.getTabs().size()];

        for (int i = 0; i < codes.size(); i++) {
            compilers[i] = new Compiler(((TextArea)codes.get(i).tab().getContent()).getText());
            compilers[i].compile();

            if (compilers[i].errors().size() > 0) {
                error = true;
                errorBuilder.append(codes.get(i).tab().getText()).append(":\n");
                compilers[i].errors().forEach(s -> errorBuilder.append(s).append("\n"));
                errorBuilder.append("\n");
            }
        }

        if (error) {
            errorFX.setText(errorBuilder.toString());
        } else {
            String[] memory = new Compiler(Arrays.asList(compilers)).memory();

            if (memory == null) {
                errorFX.setText("Conflicting memory addresses between files.");
            } else {
                StringBuilder memoryBuilder = new StringBuilder();
                Arrays.stream(memory).forEach(s -> memoryBuilder.append(s).append(" "));

                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("memory.fxml"));
                    Parent root = loader.load();
                    ((Memory)loader.getController()).setMemory(memoryBuilder.toString());

                    Scene scene = new Scene(root, 200, 200);
                    Stage stage = new Stage();

                    stage.setTitle(TITLE);
                    stage.initModality(Modality.APPLICATION_MODAL);
                    stage.setScene(scene);
                    stage.showAndWait();
                } catch (IOException ignored) { }
            }
        }
    }

    @FXML
    private void newOnAction() {
        Code code = new Code();
        codes.add(code);
        tabsFX.getTabs().add(code.tab());
    }

    @FXML
    private void openOnAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open...");

        File file = fileChooser.showOpenDialog(stage);

        if (file != null) {
            try {
                Code code = new Code(file);
                codes.add(code);
                tabsFX.getTabs().add(code.tab());
            } catch (FileNotFoundException e) {
                errorFX.setText("Couldn't open " + file + ".");
            }
        }
    }

    @FXML
    private void saveOnAction() {
        if (tabsFX.getTabs().size() > 0) {
            Code code = codes.get(tabsFX.getSelectionModel().getSelectedIndex());

            if (code.file().exists()) {
                try {
                    code.save();
                } catch (FileNotFoundException e) {
                    errorFX.setText(errorFX.getText() + "\nCouldn't save the " + code.file() + ".");
                }
            } else {
                saveAsOnAction();
            }
        }
    }

    @FXML
    private void saveAsOnAction() {
        if (tabsFX.getTabs().size() > 0) {
            Code code = codes.get(tabsFX.getSelectionModel().getSelectedIndex());

            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save As...");

            File file = fileChooser.showSaveDialog(stage);

            if (file != null) {
                code.setFile(file);

                try {
                    code.save();
                } catch (FileNotFoundException e) {
                    errorFX.setText("Couldn't save " + file + ".");
                }
            }
        }
    }

    @FXML
    private void closeOnAction() {
        if (tabsFX.getTabs().size() > 0) {
            Code code = codes.get(tabsFX.getSelectionModel().getSelectedIndex());

            if (code.tab().getText().endsWith("*")) {
                showUnsavedAlert(getUnsavedAlert(), code);
            } else {
                codes.remove(tabsFX.getSelectionModel().getSelectedIndex());
                tabsFX.getTabs().remove(tabsFX.getSelectionModel().getSelectedIndex());
            }
        }
    }


    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        ((Main)loader.getController()).stage = stage;


        Scene scene = new Scene(root, 900, 500);

        stage.setTitle(TITLE);
        stage.setScene(scene);

        stage.show();
    }

    private void showUnsavedAlert(Alert alert, Code code) {
        alert.showAndWait().ifPresent(t -> {
            if (t == ButtonType.YES) {
                saveOnAction();

                if (!code.tab().getText().endsWith("*")) {
                    codes.remove(tabsFX.getSelectionModel().getSelectedIndex());
                    tabsFX.getTabs().remove(tabsFX.getSelectionModel().getSelectedIndex());
                }
            } else if (t == ButtonType.NO) {
                codes.remove(tabsFX.getSelectionModel().getSelectedIndex());
                tabsFX.getTabs().remove(tabsFX.getSelectionModel().getSelectedIndex());
            }
        });
    }


    private static Alert getUnsavedAlert() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Unsaved Code");
        alert.setContentText("Would you like to save before closing?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        return alert;
    }

    static Tab getTab(String title, String string, ChangeListener<String> listener) {
        TextArea text = new TextArea();
        text.setText(string);
        text.setFont(CODE_FONT);
        text.textProperty().addListener(listener);

        Tab tab = new Tab();
        tab.setText(title);
        tab.setContent(text);

        return tab;
    }

    public static void main(String[] args) {
        launch(args);
    }
}
