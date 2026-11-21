package ro.ism.sap.java.day02;

import ro.ism.sap.java.utils.Utility;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TestMAC {

    public static byte[] getHashMAC(String message, String key) throws NoSuchAlgorithmException, InvalidKeyException {
        // a MAC requires a key
        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        mac.init(secretKey);
        return mac.doFinal(message.getBytes());
    }

    public static byte[] getFileHashMAC(String filePath, String key) throws IOException, NoSuchAlgorithmException, InvalidKeyException {
        Path path = Path.of(filePath);
        if (!path.toFile().exists()) {
            System.out.println(" ************* NO FILE?!");
        }

        Mac mac = Mac.getInstance("HmacSHA1");
        SecretKey secretKey = new SecretKeySpec(key.getBytes(), "HmacSHA1");
        mac.init(secretKey);

        InputStream fis = Files.newInputStream(path);
        int noOfBytesWeGot = 0;
        byte[] buffer = new byte[5];
        while((noOfBytesWeGot = fis.read(buffer)) != -1){
            mac.update(buffer, 0, noOfBytesWeGot);
        }

        fis.close();

        return mac.doFinal();
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException, IOException {
        String message = "The SAP simultation will be next weekend";
        String key = "ismtopsecret";
        byte[] hmacTag = getHashMAC(message, key);
        System.out.println("MAC tag: " + Utility.getHexString(hmacTag));

        byte[] fileMacTag = getFileHashMAC("Files\\MyData.bin", key);
        System.out.println("File MAC: " + Utility.getHexString(fileMacTag));
    }
}
