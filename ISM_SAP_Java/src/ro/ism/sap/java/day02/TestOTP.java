package ro.ism.sap.java.day02;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.NoSuchAlgorithmException;

public class TestOTP {

    public static byte[] encryptOrDecrypt(byte[] key, byte[] message) {

        if(key.length != message.length) {
            throw new UnsupportedOperationException("MUST have same size!");
        }

        byte[] cipher = new byte[key.length];
        for (int i = 0; i < key.length; i++) {
            cipher[i] = (byte) (key[i] ^ message[i]);
        }

        return cipher;
    }

    public static void encryptFileOTP(String filePath, byte[] key, String outputFilePath) throws IOException {
        Path inputFile = Path.of(filePath);
        if(!inputFile.toFile().exists()) {
            System.out.println(" ************ NO INPUT FILE?!");
        }

        byte[] fileContent = Files.readAllBytes(inputFile);
        byte[] cipherText = encryptOrDecrypt(key, fileContent);

        Path outputFile = Path.of(outputFilePath);
        if(!outputFile.toFile().exists()) {
            outputFile.toFile().createNewFile();
        }

        OutputStream os = Files.newOutputStream(outputFile);
        os.write(cipherText);
        os.close();
    }

    public static void main (String[] args) throws NoSuchAlgorithmException, IOException {
        File file = new File("Files\\MyData.bin");
        long  fileSize = file.length();
        System.out.println("File size: " + fileSize);

        // we didn't save the key here, so good luck decrypting this; it has a required brute force effort of 2^200
        byte[] randomKey = TestSecureRandom.getRandomValue((int) fileSize);
        encryptFileOTP("Files\\MyData.bin", randomKey, "Files\\MyData.otp");

        // we have the key in memory now, so let's also decrypt
        encryptFileOTP("Files\\MyData.otp", randomKey, "Files\\MyData2.bin");
        System.out.println("done");
    }
}