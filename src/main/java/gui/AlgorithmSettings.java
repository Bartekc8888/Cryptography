package gui;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

public class AlgorithmSettings implements Initializable {

    private final String AES_SETTINGS_PANE_URI = "fxml/AesSettings.fxml";

    @FXML
    public Pane settingsPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setScreen(AES_SETTINGS_PANE_URI);
    }

    public void OpenDsaScreen(MouseEvent mouseEvent) {

    }

    public void openElGamalScreen(MouseEvent mouseEvent) {

    }

    public void openAesScreen(MouseEvent mouseEvent) {
        setScreen(AES_SETTINGS_PANE_URI);
    }

    private void setScreen(String uri) {
        try {
            URL resource = getClass().getClassLoader().getResource(uri);
            if (resource == null) {
                throw new IllegalArgumentException("Given resource not found.");
            }

            Parent settingsScreen = FXMLLoader.load(resource);
            settingsPane.getChildren().removeAll();
            settingsPane.getChildren().setAll(settingsScreen);
        } catch (IOException e) {
            throw new RuntimeException("Settings fxml initialization error.", e);
        }
    }
}
