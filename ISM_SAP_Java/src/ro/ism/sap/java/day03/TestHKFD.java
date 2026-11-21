package ro.ism.sap.java.day03;

import ro.ism.sap.java.utils.Utility;

import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

public class TestHKFD {

    public static byte[] hkdf(byte[] ikm, byte[] salt, int sizeInBytes, String info) throws NoSuchAlgorithmException, InvalidKeyException {

        if(sizeInBytes <= 0 || sizeInBytes > 255 * 32) {
            throw new IllegalArgumentException();
        }
        if(salt == null) {
            salt = new byte[32];    // a 0 byte salt is accepted by the standard
        }

        // step1. extraction
        byte[] prk = HMAC.getHmac(ikm, salt);

        // step2. extension
        byte[] previousBlock = new byte[0];
        byte[] generatedKey = new byte[sizeInBytes];
        int position = 0;
        byte[] infoData = info.getBytes(StandardCharsets.UTF_8);

        for(int counter = 1; position < sizeInBytes; counter++) {
            byte[] input = new byte[previousBlock.length + info.getBytes().length + 1];

            // fill info with { previous + info + counter }
            System.arraycopy(previousBlock, 0, input, 0, previousBlock.length);
            System.arraycopy(infoData, 0, input, previousBlock.length, infoData.length);
            input[input.length - 1] = (byte)counter;

            System.out.println("[DEBUG] Input: " + Utility.byteToHexString(input));

            byte[] currentBlock = HMAC.getHmac(prk, input);

            int requiredSize = sizeInBytes - position;
            requiredSize = Math.min(requiredSize, 32);
            System.arraycopy(currentBlock, 0, generatedKey, position, requiredSize);
            position += requiredSize;
            previousBlock = currentBlock;
        }

        return generatedKey;
    }

    public static void main(String[] args) throws NoSuchAlgorithmException, InvalidKeyException {

        // -------------------------------------------------------------- test utils
        byte[] key = {(byte) 0xA4, 0x22, (byte) 0xF3, (byte) 0xFF};
        System.out.println("Small key: " + Utility.byteToHexString(key));

        String keyBase64 = Utility.encodeBase64(key);
        System.out.println("Base64 encoded key: " + keyBase64);
        // System.out.println("Base64 decoded key: " + Utiliy.decodeBase64(keyBase64));
        // Utiliy.decodeBase64(keyBase64) returns the object's reference; everything in Java is an object
        System.out.println("Base64 decoded key: " + Utility.byteToHexString(Utility.decodeBase64(keyBase64)));

        // -------------------------------------------------------------- test hkdf
        // generate a random salt and save it
        byte[] salt = new byte[32];
        salt[0] = (byte)0xFF;

        String info = "ISM Exam Session Key";
        byte[] derivedKey = hkdf(key, salt, 250, info);
        System.out.println("Derived key: " + Utility.byteToHexString(derivedKey));
    }
}

// cyphers that work at block level and cyphers that worh at byte/bit level