package gui;

import Algorithms.AlgorithmFactory;
import Algorithms.AlgorithmSettingsDto;
import dsa.DsaAlgorithm;
import dsa.DsaKeyGenerator;
import dsa.DsaKeyConverter;
import javafx.application.Platform;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.text.Text;
import javafx.stage.FileChooser;
import javafx.stage.Window;
import main.AlgorithmExecutor;
import org.controlsfx.control.ToggleSwitch;

import javax.swing.filechooser.FileSystemView;
import java.io.File;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.ResourceBundle;

public class DsaSettings implements Initializable {
    public TextField inputFileField;
    public TextField signatureFileField;
    public TextField publicComponentsFileField;
    public AnchorPane DsaTabAnchor;
    public ToggleSwitch modeToggle;
    private Window currentWindow;
    public dsa.DsaKeys dsaKeys;
    public TextField passwordFieldPublic;
    public TextField passwordFieldPrivate;
    public String gener;

    public void onModeToggle(MouseEvent mouseEvent) {
    }

    public void onInputPathButtonClick(MouseEvent mouseEvent) {
        File selectedDirectory = getFileChooser().showOpenDialog(currentWindow);

        if (selectedDirectory != null) {
            inputFileField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    public void onSignaturePathButtonClick(MouseEvent mouseEvent) {
        File selectedDirectory = getFileChooser().showSaveDialog(currentWindow);

        if (selectedDirectory != null) {
            signatureFileField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    public void onPublicComponentsPathButtonClick(MouseEvent mouseEvent) {
        File selectedDirectory = getFileChooser().showSaveDialog(currentWindow);

        if (selectedDirectory != null) {
            publicComponentsFileField.setText(selectedDirectory.getAbsolutePath());
        }
    }

    public void onCalculateClick(MouseEvent mouseEvent) {
        if (!modeToggle.selectedProperty().get()) {
            DsaAlgorithm dsaAlgorithm = new DsaAlgorithm();
            dsaAlgorithm.sign(dsaKeys, new File(inputFileField.getText()), new File(signatureFileField.getText()), new File(publicComponentsFileField.getText()));
        } else {
            DsaAlgorithm dsaAlgorithm = new DsaAlgorithm();
            boolean v = dsaAlgorithm.veryfy(new File(inputFileField.getText()), new File(signatureFileField.getText()), new File(publicComponentsFileField.getText()));
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Sygnatura" + (v ? " poprawna" : " niepoprawna"));
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
            currentWindow = DsaTabAnchor.getScene().getWindow();
        }

        FileChooser directoryChooser = new FileChooser();
        File home = FileSystemView.getFileSystemView().getHomeDirectory();
        directoryChooser.setInitialDirectory(home);

        return directoryChooser;
    }

    private AlgorithmSettingsDto getSettingsDto() {
        String key;
        if (!modeToggle.selectedProperty().get()) {
            key = passwordFieldPublic.getText();
        } else {
            key = passwordFieldPrivate.getText();
        }
        return AlgorithmSettingsDto.builder()
                .algorithm(AlgorithmFactory.createDsaAlgorithm())
                .encryptingMode(!modeToggle.selectedProperty().get())
                .inputFile(new File(inputFileField.getText()))
                .signatureFile(new File(signatureFileField.getText()))
                .publicComponentsFile(new File(publicComponentsFileField.getText()))
                .password(key)
                .build();
    }

    public void onGenerateClick(MouseEvent mouseEvent) {
        dsaKeys = DsaKeyGenerator.generateKeys();
        byte[] publicKeyBytes = DsaKeyConverter.convertToData(dsaKeys.getPublicKey().getPublicKeyPart());
        byte[] privateKeyBytes = DsaKeyConverter.convertToData(dsaKeys.getPrivateKey());
        passwordFieldPublic.setText(new String(publicKeyBytes, StandardCharsets.UTF_8));
        passwordFieldPrivate.setText(new String(privateKeyBytes, StandardCharsets.UTF_8));
    }

}
