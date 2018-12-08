package gui;

import Algorithms.AlgorithmFactory;
import Algorithms.AlgorithmSettingsDto;
import elgamal.ElGamalKeyGenerator;
import elgamal.ElGamalKeys;
import elgamal.KeyConverter;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import main.AlgorithmExecutor;
import org.controlsfx.control.ToggleSwitch;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class ElGamalSettings implements Initializable {
    public TextField inputFileField;
    public TextField outputFileField;
    public AnchorPane ElGamalTabAnchor;
    public ToggleSwitch modeToggle;
    private Window currentWindow;
    public ElGamalKeys elGamalKeys;
    public TextField passwordField;

    public void onModeToggle(MouseEvent mouseEvent) {
    }

    public void onInputPathButtonClick(MouseEvent mouseEvent) {
        File selectedDirectory = getFileChooser().showOpenDialog(currentWindow);

        if (selectedDirectory != null) {
            inputFileField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    public void onOutputPathButtonClick(MouseEvent mouseEvent) {
        File selectedDirectory = getFileChooser().showSaveDialog(currentWindow);

        if (selectedDirectory != null) {
            outputFileField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    public void onCalculateClick(MouseEvent mouseEvent) {
        onGenerateClick(mouseEvent);
        AlgorithmSettingsDto result;
        try {
            result = AlgorithmExecutor.execute(getSettingsDto());
        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Wystąpił błąd! " + e.getMessage());
            alert.setResizable(true);
            alert.showAndWait();
        }
    }

    public void onCancelClick(MouseEvent mouseEvent) {
        Platform.exit();
        System.exit(0);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
    }

    private FileChooser getFileChooser() {
        if (currentWindow == null) {
            currentWindow = ElGamalTabAnchor.getScene().getWindow();
        }

        FileChooser directoryChooser = new FileChooser();
        File home = FileSystemView.getFileSystemView().getHomeDirectory();
        directoryChooser.setInitialDirectory(home);

        return directoryChooser;
    }

    private AlgorithmSettingsDto getSettingsDto() {
        String key;
        if(!modeToggle.selectedProperty().get()){
            key = passwordField.getText();
        } else {
            byte[] privateKeyBytes = KeyConverter.convertToData(elGamalKeys.getPrivateKey());
            key = new String(privateKeyBytes, StandardCharsets.UTF_8);
        }
        return AlgorithmSettingsDto.builder()
                .algorithm(AlgorithmFactory.createElGamalAlgorithm())
                .encryptingMode(!modeToggle.selectedProperty().get())
                .inputFile(new File(inputFileField.getText()))
                .outputFile(new File(outputFileField.getText()))
                .password(key)
                .build();
    }

    public void onGenerateClick(MouseEvent mouseEvent) {
        elGamalKeys = ElGamalKeyGenerator.generateKeys();
        byte[] publicKeyBytes = KeyConverter.convertToData(elGamalKeys.getPublicKey());
        passwordField.setText(new String(publicKeyBytes, StandardCharsets.UTF_8));
    }
}
