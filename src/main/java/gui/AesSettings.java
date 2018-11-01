package gui;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

import aes.AESVersion;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import org.controlsfx.control.ToggleSwitch;

public class AesSettings implements Initializable {
    public ComboBox<AESVersion> versionComboBox;
    public TextField passwordField;

    public TextField inputFileField;
    public TextField outputFileField;

    public TextField inputTextField;
    public TextField outputTextField;

    public ToggleSwitch modeToggle;
    public Button inputFilePathButton;
    public Button outputFilePathButton;
    public Button generatePasswordButton;
    public Button calculateButton;
    public Button cancelButton;
    public AnchorPane AESTabAnchor;

    private Window currentWindow;

    private static final String alphaNumericChars = "0123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
    private static SecureRandom random = new SecureRandom();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initVersionComboBox();
    }

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

    public void onGenerateClick(MouseEvent mouseEvent) {
        String randomString = randomString();
        passwordField.setText(randomString);
    }

    public void onCalculateClick(MouseEvent mouseEvent) {

    }

    public void onCancelClick(MouseEvent mouseEvent) {
        Platform.exit();
        System.exit(0);
    }

    private FileChooser getFileChooser() {
        if (currentWindow == null) {
            currentWindow = AESTabAnchor.getScene().getWindow();
        }

        FileChooser directoryChooser = new FileChooser();
        File home = FileSystemView.getFileSystemView().getHomeDirectory();
        directoryChooser.setInitialDirectory(home);

        return directoryChooser;
    }

    private void initVersionComboBox() {
        List<AESVersion> aesVersions = Arrays.asList(AESVersion.values());
        ObservableList<AESVersion> observableList = FXCollections.observableList(aesVersions);
        versionComboBox.setItems(observableList);
        versionComboBox.setValue(AESVersion.AES_256);
    }

    private String randomString() {
        int length = 32;
        StringBuilder sb = new StringBuilder(length);

        for (int i = 0; i < length; i++) {
            sb.append(alphaNumericChars.charAt(random.nextInt(alphaNumericChars.length())));
        }
        return sb.toString();
    }
}
