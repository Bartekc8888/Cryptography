package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

import dsa.DsaAlgorithm;
import dsa.DsaKeyGenerator;
import elgamal.ElGamalAlgorithm;
import elgamal.ElGamalKeyGenerator;
import elgamal.ElGamalKeys;
import elgamal.KeyConverter;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class App extends Application {
    private final String MAIN_SCREEN_URI = "fxml/AlgorithmSettings.fxml";

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws IOException {
        URL resource = getClass().getClassLoader().getResource(MAIN_SCREEN_URI);
        if (resource == null) {
            throw new IllegalArgumentException("Given resource not found.");
        }

        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);

        primaryStage.setTitle("Cryptography");
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(500);
        primaryStage.setMinHeight(650);
        primaryStage.show();
    }
}
