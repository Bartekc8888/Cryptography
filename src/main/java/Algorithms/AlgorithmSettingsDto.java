package Algorithms;

import java.io.File;

import api.CryptographyAlgorithm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class AlgorithmSettingsDto {
    File inputFile;
    File outputFile;

    String inputText;
    String outputText;

    CryptographyAlgorithm algorithm;
    String password;
    boolean encryptingMode;
}
