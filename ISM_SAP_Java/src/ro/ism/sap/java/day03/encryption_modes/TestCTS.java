package ro.ism.sap.java.day03.encryption_modes;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.io.*;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class TestCTS {

    // CTS doesn't need padding

    public static void encrypt(byte[] key, String algorithm, String inputFilePath, String outputFilePath)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        File input = new File(inputFilePath);
        File output = new File(outputFilePath);

        if(!input.exists()) throw new FileNotFoundException("NO input file!");
        if(!output.exists()) output.createNewFile();

        FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(output);

        // create a cipher object
        Cipher cipher = Cipher.getInstance(algorithm + "/CTS/NoPadding");

        // we decided to generate a random IV and store it in the output file
        byte[] IV = new byte[cipher.getBlockSize()];
        new SecureRandom().nextBytes(IV);
        fos.write(IV);  // put it in the output file

        cipher.init(Cipher.ENCRYPT_MODE, new SecretKeySpec(key, algorithm), new IvParameterSpec(IV));

        // the block size is set and different for each algorithm
        byte[] block = new byte[cipher.getBlockSize()];
        int onOfBytesWeGot = 0;

        while ((onOfBytesWeGot = fis.read(block)) > 0) {    // remember -1 is returned when you're past eof
            // encrypt current block
            byte[] cipherBlock = cipher.update(block, 0, onOfBytesWeGot);
            fos.write(cipherBlock);
        }

        // don't forget the last one
        fos.write(cipher.doFinal());
        fis.close();
        fos.close();
    }

    public static void decrypt(byte[] key, String algorithm, String inputFilePath, String outputFilePath)
            throws IOException, NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, IllegalBlockSizeException, BadPaddingException, InvalidAlgorithmParameterException {
        File input = new File(inputFilePath);
        File output = new File(outputFilePath);

        if(!input.exists()) throw new FileNotFoundException("NO input file!");
        if(!output.exists()) output.createNewFile();

        FileInputStream fis = new FileInputStream(input);
        FileOutputStream fos = new FileOutputStream(output);


        Cipher cipher = Cipher.getInstance(algorithm + "/CTS/NoPadding");

        // get the IV from the file - first block
        byte[] IV = new byte[cipher.getBlockSize()];
        byte[] block = new byte[cipher.getBlockSize()];
        fis.read(IV);

        cipher.init(Cipher.DECRYPT_MODE, new SecretKeySpec(key, algorithm), new IvParameterSpec(IV));


        int noBytes = 0;
        while((noBytes = fis.read(block)) > 0) {
            byte[] plaintext = cipher.update(block, 0, noBytes);
            fos.write(plaintext);
        }

        fos.write(cipher.doFinal());
        fis.close();
        fos.close();
    }

    public static void main(String[] args)
            throws InvalidAlgorithmParameterException, NoSuchPaddingException, IllegalBlockSizeException, IOException, NoSuchAlgorithmException, BadPaddingException, InvalidKeyException {
        String key = "password";
        encrypt(key.getBytes(), "DES", "message.txt", "message.cts");
        decrypt(key.getBytes(), "DES", "message.cts", "message4.txt");
        System.out.println("Done");
    }
}

// DES 8B; AES 16B for key sizes