package main;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import Algorithms.AlgorithmSettingsDto;
import api.CryptographyAlgorithm;
import org.apache.commons.lang3.StringUtils;

public class AlgorithmExecutor {

    public static AlgorithmSettingsDto execute(AlgorithmSettingsDto algorithmSettingsDto) throws IOException {
        CryptographyAlgorithm algorithm = algorithmSettingsDto.getAlgorithm();
        String password = algorithmSettingsDto.getPassword();
        boolean isEncryptingMode = algorithmSettingsDto.isEncryptingMode();

        if (algorithm == null || StringUtils.isBlank(password)) {
            throw new IllegalArgumentException("Arguments cannot be null / empty.");
        }

        File inputFile = algorithmSettingsDto.getInputFile();
        File outputFile = algorithmSettingsDto.getOutputFile();

        if (inputFile != null && inputFile.exists() && outputFile != null &&
            (outputFile.exists() || outputFile.createNewFile())) {

            if (isEncryptingMode) {
                algorithm.encrypt(password, inputFile, outputFile);
            } else {
                algorithm.decrypt(password, inputFile, outputFile);
            }
        }

        String inputText = algorithmSettingsDto.getInputText();
        if (StringUtils.isNotBlank(inputText)) {
            if (isEncryptingMode) {
                byte[] encrypt = algorithm.encrypt(password, inputText);
                byte[] encoded = Base64.getEncoder().encode(encrypt);
                algorithmSettingsDto.setOutputText(new String(encoded, StandardCharsets.UTF_8));
            } else {
                byte[] decoded = Base64.getDecoder().decode(inputText);
                byte[] decrypted = algorithm.decrypt(password, decoded);
                algorithmSettingsDto.setOutputText(new String(decrypted, StandardCharsets.UTF_8));
            }
        }

        System.out.println("Done!");
        return algorithmSettingsDto;
    }
}
