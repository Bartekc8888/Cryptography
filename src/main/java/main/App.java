package main;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.charset.StandardCharsets;

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
        String filePath = "/home/bartekc8/programming/cryptography/Lenna.png";
        String encryptedFilePath = "/home/bartekc8/programming/cryptography/encodedLenna.png";
        String decryptedFilePath = "/home/bartekc8/programming/cryptography/decodedLenna.png";

        ElGamalKeys elGamalKeys = ElGamalKeyGenerator.generateKeys();
        ElGamalAlgorithm algorithm = new ElGamalAlgorithm();
        byte[] publicKeyBytes = KeyConverter.convertToData(elGamalKeys.getPublicKey());
        byte[] privateKeyBytes = KeyConverter.convertToData(elGamalKeys.getPrivateKey());

        File baseFile = new File(filePath);
        File encodedFile = new File(encryptedFilePath);
        File decodedFile = new File(decryptedFilePath);

        algorithm.encrypt(new String(publicKeyBytes, StandardCharsets.UTF_8), baseFile, encodedFile);
        algorithm.decrypt(new String(privateKeyBytes, StandardCharsets.UTF_8), encodedFile, decodedFile);
        System.exit(0);
//        launch(args);
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
