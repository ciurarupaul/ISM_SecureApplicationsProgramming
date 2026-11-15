package ro.ism.sap.java.day02;

import ro.ism.sap.java.utils.Utiliy;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class TestMessageDigest {

    public static byte[] getHash(String password) throws NoSuchAlgorithmException {
        MessageDigest hash = MessageDigest.getInstance("SHA-1");
        byte[] result = hash.digest(password.getBytes());
        return result;
    }

    public static byte[] getFileHash(String filePath) throws IOException, NoSuchAlgorithmException {
        File file = new File(filePath);
        if(!file.exists()) {
            System.out.println("******** where is the file?! ********");
            return null;
        }

        MessageDigest md = MessageDigest.getInstance("MD5");

        FileInputStream fis = new FileInputStream(file);
        byte[] buffer = new byte[8];
        int noOfBytesWeGot = 0;
        while((noOfBytesWeGot = fis.read(buffer)) != -1) {
            // md.update(buffer);    -- never, this pads with residual data when you don't get a full 8B/buffer
            md.update(buffer, 0, noOfBytesWeGot);
        }

        fis.close();

        return md.digest();
    }

    public static void main (String[] args) throws NoSuchAlgorithmException, IOException {
        String password = "12345678";
        byte[] passHash = getHash(password);
        System.out.println("SHA-1 hash: " + Utiliy.getHexString(passHash));

        byte[] md5FileHash = getFileHash("Files\\MyData.bin");
        System.out.println("MD5 file hash: " + Utiliy.getHexString(md5FileHash));
    }
}
