/*
 * Tomer Aberbach
 * aberbat1@tcnj.edu
 * 12/30/2017
 * Students at The College of New Jersey are granted
 * unlimited use and access to this application and its code.
 */

package com.tomeraberbach.mano.application;

import com.tomeraberbach.mano.assembly.Compiler;
import com.tomeraberbach.mano.assembly.Program;
import com.tomeraberbach.mano.simulation.Computer;
import com.tomeraberbach.mano.simulation.Memory;
import com.tomeraberbach.mano.simulation.Microoperation;
import com.tomeraberbach.mano.simulation.RAM;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.Clipboard;
import javafx.scene.input.ClipboardContent;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;

/**
 * JavaFX controller and starting point for the main application window.
 */
public class Main extends Application {
    /**
     * Title of the application.
     */
    private static final String TITLE = "Mano Simulator - Tomer Aberbach";

    /**
     * Text displayed in the 'About' dialog.
     */
    private static final String ABOUT = "Tomer Aberbach\n" +
        "aberbat1@tcnj.edu\n" +
        "12/30/2017\n" +
        "Students at The College of New Jersey are granted\n" +
        "unlimited use and access to this application and\n" +
        "its code.";
    /**
     * {@link ArrayList} of currently open {@link Code} documents.
     */
    private final ArrayList<Code> codes;
    /**
     * {@link Computer} used for running simulations.
     */
    private final Computer computer;
    /**
     * {@link TabPane} where the {@link Code} documents in this {@link Main#codes} will be displayed.
     */
    @FXML private TabPane codesFX;
    /**
     * {@link TabPane} where instructions, the console, and the simulation are shown.
     */
    @FXML private TabPane tabsFX;
    /**
     * {@link TextArea} in the application window where messages will be logged.
     */
    @FXML private TextArea consoleFX;
    /**
     * {@link TextField} where the value of the sequence counter will be displayed.
     */
    @FXML private TextField scFX;
    /**
     * {@link TextField} where the value of the program counter will be displayed.
     */
    @FXML private TextField pcFX;
    /**
     * {@link TextField} where the value of the address register will be displayed.
     */
    @FXML private TextField arFX;
    /**
     * {@link TextField} where the value of the instruction register will be displayed.
     */
    @FXML private TextField irFX;
    /**
     * {@link TextField} where the value of the data register will be displayed.
     */
    @FXML private TextField drFX;
    /**
     * {@link TextField} where the value of the accumulator will be displayed.
     */
    @FXML private TextField acFX;
    /**
     * {@link TextField} where the value of the temporary register will be displayed.
     */
    @FXML private TextField trFX;
    /**
     * {@link TextField} where the value of the input register will be displayed.
     */
    @FXML private TextField inprFX;
    /**
     * {@link TextField} where the value of the output register will be displayed.
     */
    @FXML private TextField outrFX;
    /**
     * {@link TextField} where the value of the indirect addressing flip-flop will be displayed.
     */
    @FXML private TextField iFX;
    /**
     * {@link TextField} where the value of the s flip-flop will be displayed.
     */
    @FXML private TextField sFX;
    /**
     * {@link TextField} where the value of the carry bit flip-flop will be displayed.
     */
    @FXML private TextField eFX;
    /**
     * {@link TextField} where the value of the interrupt flip-flop will be displayed.
     */
    @FXML private TextField rFX;
    /**
     * {@link TextField} where the value of the interrupt enable flip-flop will be displayed.
     */
    @FXML private TextField ienFX;
    /**
     * {@link TextField} where the value of the input flag flip-flop will be displayed.
     */
    @FXML private TextField fgiFX;
    /**
     * {@link TextField} where the value of the output flag flip-flop will be displayed.
     */
    @FXML private TextField fgoFX;
    /**
     * {@link TextField} where the user's input will be displayed.
     */
    @FXML private TextField inputFX;
    /**
     * {@link ToggleButton} for running and pausing the simulation.
     */
    @FXML private ToggleButton runFX;
    /**
     * {@link Slider} for adjusting the speed of the simulation.
     */
    @FXML private Slider speedFX;
    /**
     * {@link TextField} where the currently executing microoperation will be displayed.
     */
    @FXML private TextField microoperationFX;
    /**
     * {@link TableView} where the current state of {@link RAM} will be displayed.
     */
    @FXML private TableView<Memory> ramFX;
    /**
     * The contents of the application window.
     */
    private Stage stage;
    /**
     * The most recently compiled program.
     */
    private Program program;

    /**
     * The {@link Task} for running the simulation.
     */
    private Task<Void> task;


    /**
     * Initializes the application window with an empty {@link ArrayList} of {@link Code} documents.
     */
    public Main() {
        codes = new ArrayList<>();
        computer = new Computer();
        program = new Program(0, new ArrayList<>(), new ArrayList<>(), new ArrayList<>());
    }

    /**
     * Starting point for the application.
     *
     * @param args Ignored.
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Called when the 'New' button is pressed.
     * Initializes a new instance of {@link Code} with {@link Code#Code()}, adds it to {@link Main#codes}, and displays it in {@link Main#codesFX}.
     */
    @FXML
    private void newOnAction() {
        // Creates a new code document
        Code code = new Code();
        codes.add(code);
        codesFX.getTabs().add(code.tab());
    }

    /**
     * Called when the 'Open...' button is pressed.
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
                codesFX.getTabs().add(code.tab());
            } catch (FileNotFoundException e) {
                consoleFX.setText("Couldn't open " + file + ".");
            }
        }
    }

    /**
     * Called when the 'Close' button is pressed.
     * If {@link Main#codesFX} contains any tabs it closes the currently selected {@link Code} document.
     * It will prompt the user to save the {@link Code} document if there have been unsaved changes.
     */
    @FXML
    private void closeOnAction() {
        // Checks if there are any tabs present
        if (codesFX.getTabs().size() > 0) {
            // Gets the code in the current tab
            Code code = codes.get(codesFX.getSelectionModel().getSelectedIndex());

            if (code.close(stage)) {
                // Closes the code document
                codes.remove(codesFX.getSelectionModel().getSelectedIndex());
                codesFX.getTabs().remove(codesFX.getSelectionModel().getSelectedIndex());
            }
        }
    }

    /**
     * Called when the 'Save' button is pressed.
     * If {@link Main#codesFX} contains any tabs it saves the currently selected {@link Code} document.
     * If the selected {@link Code} document does not have a file it passes off the job to {@link Main#saveAsOnAction()}.
     * Otherwise it saves the {@link Code} document with {@link Code#save(Stage)}.
     */
    @FXML
    private void saveOnAction() {
        // Checks if there are any tabs present
        if (codesFX.getTabs().size() > 0) {
            Code code = codes.get(codesFX.getSelectionModel().getSelectedIndex());
            if (!code.save(stage)) {
                consoleFX.setText(consoleFX.getText() + "\nCouldn't save the " + code.file() + ".");
            }
        }
    }

    /**
     * Called when the 'Save All' button is pressed.
     * If {@link Main#codesFX} contains any tabs it saves all of the {@link Code} documents.
     * Otherwise it saves the {@link Code} document with {@link Code#save(Stage)}.
     */
    @FXML
    public void saveAllOnAction() {
        for (Code code : codes) {
            if (!code.save(stage)) {
                consoleFX.setText(consoleFX.getText() + "\nCouldn't save the " + code.file() + ".");
            }
        }
    }

    /**
     * Called when the 'Save as...' button is pressed.
     * If {@link Main#codesFX} contains any tabs it displays a prompt for choosing where to save the current {@link Code} document.
     * If a {@link File} is chosen will be called followed by a call to {@link Code#saveAs(Stage)}.
     */
    @FXML
    private void saveAsOnAction() {
        // Checks if there are any tabs present
        if (codesFX.getTabs().size() > 0) {
            Code code = codes.get(codesFX.getSelectionModel().getSelectedIndex());
            if (!code.saveAs(stage)) {
                consoleFX.setText(consoleFX.getText() + "\nCouldn't save the " + code.file() + ".");
            }
        }
    }

    /**
     * Called when the 'Assemble' button is pressed.
     * Compiles the currently selected tab's assembly code in {@link Main#codes} using {@link Compiler}.
     * Logs any errors in {@link Main#consoleFX}.
     */
    @FXML
    private void assembleOnAction() {
        if (codesFX.getTabs().size() > 0) {
            resetOnAction();

            StringBuilder builder = new StringBuilder();

            Tab tab = codes.get(codesFX.getSelectionModel().getSelectedIndex()).tab();
            Program program = Compiler.compile(((TextArea)tab.getContent()).getText());

            if (program.errors().size() > 0) {
                builder.append(tab.getText()).append(":\n");
                program.errors().forEach(s -> builder.append(s).append("\n"));
            }

            consoleFX.setText(builder.toString());
            updateSimulation(program);
        }
    }

    /**
     * Called when the 'Reset' button is pressed.
     */
    @FXML
    private void resetOnAction() {
        if (task != null && task.isRunning()) {
            task.cancel();
        }

        runFX.setSelected(false);
        microoperationFX.setText("");
        computer.load(program);
        ramFX.refresh();
    }

    /**
     * @param program {@link Program} to load into the {@link Computer} simulation.
     */
    private void updateSimulation(Program program) {
        if (program.errors().size() > 0) {
            tabsFX.getSelectionModel().select(1);
        } else {
            this.program = program;
            computer.load(program);
            ramFX.refresh();
            tabsFX.getSelectionModel().select(2);
            new Alert(Alert.AlertType.INFORMATION, "Compilation Successful.").showAndWait();
        }
    }

    /**
     * Called when the 'Assemble All' button is pressed.
     * Compiles all the assembly code in {@link Main#codes} using {@link Compiler}.
     * Logs any errors in {@link Main#consoleFX}.
     */
    @FXML
    private void assembleAllOnAction() {
        if (codesFX.getTabs().size() > 0) {
            resetOnAction();
            StringBuilder builder = new StringBuilder();

            Program[] programs = new Program[codesFX.getTabs().size()];

            for (int i = 0; i < codes.size(); i++) {
                programs[i] = Compiler.compile(((TextArea)codes.get(i).tab().getContent()).getText());

                if (programs[i].errors().size() > 0) {
                    builder.append(codes.get(i).tab().getText()).append(":\n");
                    programs[i].errors().forEach(s -> builder.append(s).append("\n"));
                }
            }

            Program program = Program.union(programs[0].start(), programs);

            if (program.conflicts()) {
                builder.append("Conflicting memory addresses between files.\n");
            }

            consoleFX.setText(builder.toString());
            updateSimulation(program);
        }
    }

    /**
     * Called when the 'About Mano Simulator' button is pressed.
     * Opens an about window.
     */
    @FXML
    private void aboutOnAction() {
        Alert alert = new Alert(Alert.AlertType.INFORMATION, ABOUT);
        alert.setTitle("About");
        alert.setHeaderText("About");
        alert.showAndWait();
    }

    /**
     * Called when the 'Input Enable' button is pressed.
     */
    @FXML
    private void inputEnableOnAction() {
        computer.fgi().load(1);

        if (inputFX.getText().matches("0x[0-9a-fA-F][0-9a-fA-F]?")) {
            computer.inpr().load(Integer.parseInt(inputFX.getText()));
        } else if (inputFX.getText().length() == 1) {
            computer.inpr().load(inputFX.getText().charAt(0));
        }
    }

    /**
     * Called when the 'Output Enable' button is pressed.
     */
    @FXML
    private void outputEnableOnAction() {
        computer.fgo().load(1);
    }

    /**
     * Called when the 'Run' button is toggled.
     */
    @FXML
    private void runOnAction() {
        if (task != null && task.isRunning()) {
            task.cancel();
        }

        if (runFX.isSelected()) {
            task = new Task<Void>() {
                @Override
                protected Void call() throws Exception {
                    while (computer.s().value(0)) {
                        if (computer.microoperations().isEmpty()) {
                            computer.tick();
                        }

                        while (!computer.microoperations().isEmpty()) {
                            Microoperation microoperation = computer.microoperations().poll();

                            Platform.runLater(() -> {
                                microoperationFX.setText(microoperation.toString());
                                microoperation.execute(computer);
                                ramFX.refresh();
                            });

                            Thread.sleep(Math.round(800 / speedFX.getValue()));
                        }
                    }

                    return null;
                }
            };

            Thread thread = new Thread(task);
            thread.setDaemon(true);
            thread.start();
        }
    }

    /**
     * Called when the 'Step' button is pressed.
     */
    @FXML
    private void stepOnAction() {
        if (task != null && task.isRunning()) {
            task.cancel();
        }

        runFX.setSelected(false);
        if (computer.s().value(0)) {
            if (computer.microoperations().isEmpty()) {
                computer.tick();
            }

            Platform.runLater(() -> {
                if (!computer.microoperations().isEmpty()) {
                    Microoperation microoperation = computer.microoperations().poll();
                    microoperationFX.setText(microoperation.toString());
                    microoperation.execute(computer);
                    ramFX.refresh();
                }
            });
        }
    }

    /**
     * Called when the 'Export' button is pressed.
     */
    @FXML
    private void exportOnAction() {
        TextArea textArea = new TextArea(computer.ram().toString());
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setFont(Code.CODE_FONT);

        GridPane gridPane = new GridPane();
        gridPane.setMaxWidth(Double.MAX_VALUE);
        gridPane.add(textArea, 0, 0);

        Alert alert = new Alert(Alert.AlertType.INFORMATION, "", new ButtonType("Copy to Clipboard", ButtonBar.ButtonData.OTHER));
        alert.setTitle("RAM");
        alert.setHeaderText("RAM");
        alert.getDialogPane().setContent(gridPane);
        alert.showAndWait().ifPresent(type -> {
            if (type.getButtonData() == ButtonBar.ButtonData.OTHER) {
                final ClipboardContent content = new ClipboardContent();
                content.putString(computer.ram().toString());
                Clipboard.getSystemClipboard().setContent(content);
            }
        });
    }

    /**
     * Used to launch the application.
     *
     * @param stage Essentially the window to display the application in.
     * @throws IOException Occurs when the file 'main.fxml' representing the layout of the application could not be accessed.
     */
    @Override
    public void start(Stage stage) throws IOException {
        // Loads the main application and starts it
        FXMLLoader loader = new FXMLLoader(getClass().getResource("main.fxml"));
        Parent root = loader.load();

        Main main = loader.getController();
        main.stage = stage;
        main.bind();

        Scene scene = new Scene(root, 1200, 700);

        stage.setTitle(TITLE);
        stage.setScene(scene);
        stage.setOnCloseRequest((event) -> main.quitOnAction());
        stage.show();
    }

    /**
     * Binds the application controls to simulation values.
     */
    private void bind() {
        scFX.textProperty().bindBidirectional(computer.scProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return number.toString();
            }

            @Override
            public Number fromString(String s) {
                return Integer.decode(s);
            }
        });
        pcFX.textProperty().bindBidirectional(computer.pc().valueProperty(), computer.pc().converter());
        arFX.textProperty().bindBidirectional(computer.ar().valueProperty(), computer.ar().converter());
        irFX.textProperty().bindBidirectional(computer.ir().valueProperty(), computer.ir().converter());
        drFX.textProperty().bindBidirectional(computer.dr().valueProperty(), computer.dr().converter());
        acFX.textProperty().bindBidirectional(computer.ac().valueProperty(), computer.ac().converter());
        trFX.textProperty().bindBidirectional(computer.tr().valueProperty(), computer.tr().converter());
        inprFX.textProperty().bindBidirectional(computer.inpr().valueProperty(), computer.inpr().converter());
        outrFX.textProperty().bindBidirectional(computer.outr().valueProperty(), new StringConverter<Number>() {
            @Override
            public String toString(Number number) {
                return Character.toString((char)number.intValue());
            }

            @Override
            public Number fromString(String s) {
                return (int)s.charAt(0);
            }
        });
        iFX.textProperty().bindBidirectional(computer.i().valueProperty(), computer.i().converter());
        sFX.textProperty().bindBidirectional(computer.s().valueProperty(), computer.s().converter());
        eFX.textProperty().bindBidirectional(computer.e().valueProperty(), computer.e().converter());
        rFX.textProperty().bindBidirectional(computer.r().valueProperty(), computer.r().converter());
        ienFX.textProperty().bindBidirectional(computer.ien().valueProperty(), computer.ien().converter());
        fgiFX.textProperty().bindBidirectional(computer.fgi().valueProperty(), computer.fgi().converter());
        fgoFX.textProperty().bindBidirectional(computer.fgo().valueProperty(), computer.fgo().converter());

        ramFX.itemsProperty().bindBidirectional(computer.ram().valuesProperty());

        computer.pc().valueProperty().addListener((observableValue, number, t1) -> {
            ramFX.getSelectionModel().select(t1.intValue());
            ramFX.scrollTo(Math.max(0, t1.intValue() - 7));
        });
    }

    /**
     * Called when the 'Quit' button is pressed.
     * Alerts the user about any unsaved code.
     */
    @FXML
    public void quitOnAction() {
        for (Code code : codes) {
            if (!code.close(stage)) {
                return;
            }
        }

        Platform.exit();
    }
}
