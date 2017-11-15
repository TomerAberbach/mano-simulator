package com.tomeraberbach.mano.application;

/* Tomer Aberbach
 * aberbat1@tcnj.edu
 * 11/12/2017
 * This code may be accessed and used by students at The College of New Jersey.
 */

import com.tomeraberbach.mano.assembly.Compiler;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
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

/**
 * JavaFX controller and starting point for the main application window.
 */
public class Main extends Application {
    /**
     * Title of the application.
     */
    private static final String TITLE = "Mano Assembler - Tomer Aberbach";

    /**
     * {@link Font} to use for the assembly code.
     */
    private static final Font CODE_FONT = new Font("Courier New", 13.0);


    /**
     * Essentially the application window.
     */
    private Stage stage;

    /**
     * {@link ArrayList} of currently open {@link Code} documents.
     */
    private ArrayList<Code> codes;

    /**
     * Text area in the application window where errors will be logged.
     */
    @FXML private TextArea errorFX;

    /**
     * Pane where the {@link Code} documents in {@link Main#codes} will be displayed.
     */
    @FXML private TabPane tabsFX;


    /**
     * Initializes the application window with an empty {@link ArrayList} of {@link Code} documents.
     */
    public Main() {
        codes = new ArrayList<>();
    }


    /**
     * Called when the 'Assemble' button is pressed.
     * Compiles all the assembly code in {@link Main#codes} using {@link Compiler}.
     * Logs any errors in {@link Main#errorFX}.
     * If no errors are encountered opens {@link Memory}.
     */
    @FXML
    private void assembleOnAction() {
        errorFX.setText("");
        StringBuilder errorBuilder = new StringBuilder();
        boolean error = false;

        Compiler[] compilers = new Compiler[tabsFX.getTabs().size()];

        // Loops to compile the code in all of the tabs
        for (int i = 0; i < codes.size(); i++) {
            compilers[i] = new Compiler(((TextArea)codes.get(i).tab().getContent()).getText());
            compilers[i].compile();

            // Prepares any compiler errors for the error log
            if (compilers[i].errors().size() > 0) {
                error = true;
                errorBuilder.append(codes.get(i).tab().getText()).append(":\n");
                compilers[i].errors().forEach(s -> errorBuilder.append(s).append("\n"));
                errorBuilder.append("\n");
            }
        }

        // Checks if any of code documents contained errors
        if (error) {
            errorFX.setText(errorBuilder.toString());
        } else {
            // Aggregates the compilers into one compiler and gets the RAM memory map
            String[] memory = new Compiler(Arrays.asList(compilers)).memory();

            // Checks if the code documents had address overlap
            if (memory == null) {
                errorFX.setText("Conflicting memory addresses between files.");
            } else {
                // Converts the memory map array to a string
                StringBuilder memoryBuilder = new StringBuilder();
                Arrays.stream(memory).forEach(s -> memoryBuilder.append(s).append(" "));

                try {
                    // Opens the memory map dialog
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

    /**
     * Called when the 'Help' button is pressed.
     * Opens a help window.
     */
    @FXML
    private void helpOnAction() {
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("help.fxml")));
            Stage stage = new Stage();

            stage.setTitle(TITLE);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.showAndWait();
        } catch (IOException ignored) { }
    }

    /**
     * Called when the 'New' button is pressed.
     * Initializes a new instance of {@link Code} with {@link Code#Code()}, adds it to {@link Main#codes}, and displays it in {@link Main#tabsFX}.
     */
    @FXML
    private void newOnAction() {
        // Creates a new code document
        Code code = new Code();
        codes.add(code);
        tabsFX.getTabs().add(code.tab());
    }

    /**
     * Called when the 'Open' button is pressed.
     * Creates a prompt to choose a file to open. If a {@link File} was picked initializes a new instance of {@link Code} with {@link Code#Code(File)}.
     */
    @FXML
    private void openOnAction() {
        // Opens a dialog to choose a file to open
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open...");

        // Gets the file which was chosen
        File file = fileChooser.showOpenDialog(stage);

        // Checks if a file was chosen
        if (file != null) {
            // Tries to open the file and creates a new code document for it
            try {
                Code code = new Code(file);
                codes.add(code);
                tabsFX.getTabs().add(code.tab());
            } catch (FileNotFoundException e) {
                errorFX.setText("Couldn't open " + file + ".");
            }
        }
    }

    /**
     * Called when the 'Save' button is pressed.
     * If {@link Main#tabsFX} contains any tabs it saves the currently selected {@link Code} document.
     * If the selected {@link Code} document does not have a file it passes off the job to {@link Main#saveAsOnAction()}.
     * Otherwise it saves the {@link Code} document with {@link Code#save()}.
     */
    @FXML
    private void saveOnAction() {
        // Checks if there are any tabs present
        if (tabsFX.getTabs().size() > 0) {
            // Gets the currently selected tab's code document
            Code code = codes.get(tabsFX.getSelectionModel().getSelectedIndex());

            // Checks if the code document was opened from a file
            if (code.file().exists()) {
                // Tries to save the code
                try {
                    code.save();
                } catch (FileNotFoundException e) {
                    errorFX.setText(errorFX.getText() + "\nCouldn't save the " + code.file() + ".");
                }
            } else {
                // Brings up a save as dialog
                saveAsOnAction();
            }
        }
    }

    /**
     * Called by {@link Main#saveOnAction()} or when the 'Save as...' button is pressed.
     * If {@link Main#tabsFX} contains any tabs it displays a prompt for choosing where to save the current {@link Code} document.
     * If a {@link File} is chosen {@link Code#setFile(File)} will be called followed by a call to {@link Code#save()}.
     */
    @FXML
    private void saveAsOnAction() {
        // Checks if there are any tabs present
        if (tabsFX.getTabs().size() > 0) {
            // Gets the currently selected tab's code document
            Code code = codes.get(tabsFX.getSelectionModel().getSelectedIndex());

            // Brings up a file dialog for choosing a save location
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Save As...");

            // Gets the new file path
            File file = fileChooser.showSaveDialog(stage);

            // Checks if a new file was chosen
            if (file != null) {
                // Sets the code document's file
                code.setFile(file);

                // Saves the code
                try {
                    code.save();
                } catch (FileNotFoundException e) {
                    errorFX.setText("Couldn't save " + file + ".");
                }
            }
        }
    }

    /**
     * Called when the 'Close' button is pressed.
     * If {@link Main#tabsFX} contains any tabs it closes the currently selected {@link Code} document.
     * It will prompt the user to save the {@link Code} document if there have been unsaved changes.
     */
    @FXML
    private void closeOnAction() {
        // Checks if there are any tabs present
        if (tabsFX.getTabs().size() > 0) {
            // Gets the code in the current tab
            Code code = codes.get(tabsFX.getSelectionModel().getSelectedIndex());

            // Checks if the code has not been saved
            if (code.tab().getText().endsWith("*")) {
                // Asks the user if they would like to save
                showUnsavedAlert(getUnsavedAlert(), code);
            } else {
                // Closes the code document
                codes.remove(tabsFX.getSelectionModel().getSelectedIndex());
                tabsFX.getTabs().remove(tabsFX.getSelectionModel().getSelectedIndex());
            }
        }
    }


    /**
     * Used to launch the application.
     * @param stage Essentially the window to display the application in.
     * @throws IOException Occurs when the file 'main.fxml' representing the layout of the application could not be accessed.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Loads the main application and starts it
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();
        ((Main)loader.getController()).stage = stage;

        Scene scene = new Scene(root, 900, 500);

        stage.setTitle(TITLE);
        stage.setScene(scene);

        stage.show();
    }

    /**
     * Shows an alert prompting the user to save a {@link Code} document.
     * @param alert {@link Alert} to display.
     * @param code The unsaved {@link Code} document.
     */
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

    /**
     * @return {@link Alert} prompting the user to save a modified {@link Code} document.
     */
    private static Alert getUnsavedAlert() {
        Alert alert = new Alert(Alert.AlertType.NONE);
        alert.setTitle("Unsaved Code");
        alert.setContentText("Would you like to save before closing?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO, ButtonType.CANCEL);
        return alert;
    }

    /**
     * @param title {@link String} to title the {@link Tab} with.
     * @param string {@link String} to place in the text area of the {@link Tab}.
     * @param listener {@link ChangeListener} which will check for changes to {@link Tab}. {@link Code} acts as a {@link ChangeListener}.
     * @return {@link Tab} titled {@param title}, which inner text {@param string} and listener {@param listener}.
     */
    static Tab getTab(String title, String string, ChangeListener<String> listener) {
        // Creates a tab with a text area in it
        TextArea text = new TextArea();
        text.setText(string);
        text.setFont(CODE_FONT);
        text.textProperty().addListener(listener);

        Tab tab = new Tab();
        tab.setText(title);
        tab.setContent(text);

        return tab;
    }

    /**
     * Starting point for the application.
     * @param args Ignore.
     */
    public static void main(String[] args) {
        launch(args);
    }
}
