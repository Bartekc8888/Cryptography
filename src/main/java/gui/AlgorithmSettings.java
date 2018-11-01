package gui;

import java.io.IOException;
import java.net.URL;
import java.util.Objects;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class AlgorithmSettings implements Initializable {

    private final String AES_SETTINGS_PANE_URI = "fxml/AesSettings.fxml";

    @FXML
    public Pane settingsPane;
    public ToggleButton AESTabButton;
    public ToggleButton ElGamalTabButton;
    public ToggleButton DSATabButton;
    public ToggleGroup AlgorithmSettingsTabButtonGroup;

    private String previousTab;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        AESTabButton.setSelected(true);
        setScreen(AES_SETTINGS_PANE_URI);

        disallowUnselectingAllToggleButtons();
    }

    public void OpenDsaScreen(MouseEvent mouseEvent) {

    }

    public void openElGamalScreen(MouseEvent mouseEvent) {

    }

    public void openAesScreen(MouseEvent mouseEvent) {
        setScreen(AES_SETTINGS_PANE_URI);
    }

    private void setScreen(String uri) {
        if (Objects.equals(previousTab, uri)) {
            return;
        }

        try {
            URL resource = getClass().getClassLoader().getResource(uri);
            if (resource == null) {
                throw new IllegalArgumentException("Given resource not found.");
            }

            Parent settingsScreen = FXMLLoader.load(resource);
            settingsPane.getChildren().removeAll();
            settingsPane.getChildren().setAll(settingsScreen);

            previousTab = uri;
        } catch (IOException e) {
            throw new RuntimeException("Settings fxml initialization error.", e);
        }
    }

    private void disallowUnselectingAllToggleButtons() {
        AlgorithmSettingsTabButtonGroup.selectedToggleProperty().addListener((obsVal, oldVal, newVal) -> {
            if (newVal == null)
                oldVal.setSelected(true);
        });
    }
}
