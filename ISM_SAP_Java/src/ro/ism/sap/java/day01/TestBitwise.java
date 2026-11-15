package ro.ism.sap.java.day01;

import java.util.Arrays;
import java.util.Base64;

public class TestBitwise
{
    public static String getHexString(byte[] value) {
        StringBuilder sb = new StringBuilder();
        for(byte byteValue : value) {
            sb.append(String.format("%02X ", byteValue));
        }
        return sb.toString();
    }

    public static void main(String[] agrs) {
        // define a 1 byte key with bit 5 = 1 ;; bit 5 is the 6th bit from the right
        byte key1 = 32;
        byte key2 = (byte) 0b00100000;  // write directly in binary
        byte key3 = 1 << 5; // start from 00000001 and shift the '1' 5 bits to the left

        System.out.println("Key = " + key1);
        System.out.println("Key = " + key2);
        System.out.println("Key = " + key3);

        // define a 1 byte key with bit 6 = 1 and bit 3 = 1
        byte key4 = (byte) 0b01001000;
        byte key5 = 1 << 6 | 1 << 3;

        // defining values in hex
        byte nounce = (byte)0xA3;

        // byte arrays
        // define a DES key on 8 bytes where byte 3 has all bits 1 and the rest are 0
        byte[] desKey = {
                // 0, 0, 0, 0, (byte)0b11111111, 0, 0, 0
                0, 0, 0, 0, (byte)0xff, 0, 0, 0
        };

        // define an AES key on 128 bites where byte 5 = AA and byte 7 is 3F
        // BIG MISTAKE -- aesKey[5] - the index is moving from left to right, so it's actually referencing the 120th byte
        byte[] aesKey = new byte[16];
        aesKey[10] = (byte)0xAA;
        aesKey[8] = (byte)0x3F;

        // printing values
        System.out.printf("The 1b key is: %x\n", key4);
        System.out.printf("The 1b nounce is: %x\n", nounce);

        System.out.println("The aesKey is: " + getHexString(aesKey));

        // string conversions from byte representation -- DON'T do this!!
        byte[] desKey3 = { 0x30, (byte)0x89, 0x00, 0x01, 0x02, (byte)0xD3, 0x13, (byte)0xFF };
        System.out.println("\ndesKey3 is: " + getHexString(desKey3));

        /*
         STUPID below!!
         this converts the key into unprintable characters; most values that fit in to a byte don't have an equivalent in the ascii table
            FROM 30 89 00 01 02 D3 13 FF
            TO   0� ��

         String desPass = new String(desKey3);
         System.out.println("The desKey is: " + desPass);

         In order to display binary strngs, always go for the getHexString helper
        */

        // storing binary values as base64 - this is ENCODING, not ENCRYPTION
        // alternative is a hex string
        String base64Representation = Base64.getEncoder().encodeToString(desKey3);
        System.out.println("desKey as a Base64 string: " + base64Representation);   // THIS IS REVERSABLE; you can simply decode this and get the original key back

        byte[] desKey3Copy = Base64.getDecoder().decode(base64Representation);
        System.out.println("desKey decoded from Base64: " + getHexString(desKey3Copy));

        // comparing byte arrays
        byte[] desKey4 = { 0x30, (byte)0x89, 0x00, 0x01, 0x02, (byte)0xD3, 0x13, (byte)0xFF };

        /*
        DON'T DO THIS
        if(desKey3 == desKey4) {
            System.out.println("The keys are EQUAL");
        }
        else {
            System.out.println("The keys are DIFFERENT");
            // you will ALWAYS see this
            // you are comparing memory addresses
        }

        NEVER CROSSCHECK WITH ==
        DO A DEEP CHECK - CHECK THE VALUES, NOT THE REFERENCES
        */

        if(Arrays.equals(desKey3, desKey4)) {
            System.out.println("The keys are EQUAL");
        }

        // 'something = ""' is not the same as 'something = new String("")'
        // same issue as above - value vs reference

        // -------------------
        //  bit level shifting << >> >>>
        // -------------------
        byte anotherKey = 0b00110111;
        System.out.println('\n' + Integer.toBinaryString(anotherKey));
        anotherKey = (byte) (anotherKey << 1);
        System.out.println(Integer.toBinaryString(anotherKey));
        anotherKey = (byte) (anotherKey << 2);
        System.out.println(Integer.toBinaryString(anotherKey));

        /*
        * by shiffting like this, you overflow into the sign bit (MSB)
        * 0011.0111 becomes 1101.1100
        * java always converts to integer, seems that it's a negative value and applies the complement
        * if by shifting you get the MSB to 1, you will get the complement
        * 110111
        * 1101110
        * 11111111111111111111111110111000
        * */

        byte changedKey = 0b00110111;

        // this is how you reset Java's behaviour - this eliminates all the
        System.out.println('\n' + Integer.toBinaryString(changedKey));
        changedKey = (byte) (changedKey << 3);
        // always apply the 0xFF mask after a byte shift
        System.out.println(Integer.toBinaryString(0xFF & changedKey));

        // this is the fix: Integer.toBinaryString(0xFF & changedKey)
        // 0011.0111 << 3 --> 1011.1000 (you lose the first one from the left, as expected)
        // the rest is just sandbox bullshitting

        // ---------------------
        // shifting to the right
        // ---------------------
        changedKey = 0b00110111;
        System.out.println('\n' + "Initial key: " + changedKey);

        changedKey = (byte) (changedKey >> 1);
        System.out.println(Integer.toBinaryString(changedKey));
        changedKey = (byte) (changedKey >> 4);
        System.out.println(Integer.toBinaryString(changedKey));
        // Java will always try to preserve the negative value and will fill everything with 1s

        changedKey = 0b00110111;
        changedKey = (byte) ((0xFF & changedKey) >>> 4);
        System.out.println(Integer.toBinaryString(changedKey));
        changedKey = 0b00110111;
        changedKey = (byte) ((0xFF & changedKey) >> 4);
        System.out.println(Integer.toBinaryString(changedKey));

        // whatever
        // always use (byte)((0xFF & changedKey) >>> 4) on the input when shiftting to the right
        // always use Integer.toBinaryString(0xFF & changedKey) on the output when shiftting to the left

        // ------------------------------------------
        //                 rotations
        // ------------------------------------------

        // 1100 1111 rotated to left is
        // 0111 1110

        // manual way

        // how to check a bit's value
        byte bitValue = (byte) 0b00110111;

        // check bit 6
        byte bitMask = (byte) 0b01000000;
        byte bitResult = (byte) (bitValue & bitMask);
        byte bit6Value = 0;

        if(bitResult == 0){
            System.out.println("Bit 6 is 0");
        } else {
            System.out.println("Bit 6 is 1");
            bit6Value = 1;
        }

        // add the bit value into another byte on any position
        byte outputResult = 0x00;
        // add the previous bit's value on bit2
        outputResult = (byte) (outputResult | bit6Value << 2);

        // alternative
        // get bit 6 value
        bitValue = (byte) 0b01110111;
        bit6Value = (byte) (0xFF & (bitValue << 1) >>> 7);
        System.out.println('\n' + Integer.toBinaryString(bit6Value));

        // alternative 2
        bitValue = (byte) 0b01110111;
        bit6Value = (byte) ((0xFF & bitValue) >>> 6);
        bit6Value = (byte) (bit6Value & 1);
        System.out.println('\n' + Integer.toBinaryString(bit6Value));

        /*
             Values | ^ XOR | & AND | |  OR |
             0   0  |   0   |   0   |   0   |
             0   1  |   1   |   0   |   1   |
             1   0  |   1   |   0   |   1   |
             1   1  |   0   |   1   |   1   |
        */

        // ------------------------------------------
        //               flipping bits
        // ------------------------------------------
        /*
            flipping - from 0 to 1 and from 1 to 0
            reset - 0
            set   - 1
        */
        bitValue = (byte) 0b01110111;
        byte flippedValue = (byte) ~bitValue;
        System.out.println('\n' + "From 0"
                + Integer.toBinaryString(0xFF & bitValue) + " to "
                + Integer.toBinaryString(0xFF & flippedValue) + " -- flip everything");

        // flip bit 2
        bitValue = (byte) 0b10110011;
        flippedValue = (byte) (bitValue ^ (1 << 2)); // (1 << 2) is 0000 0001 shifted to the left by 2
        System.out.println("From "
                + Integer.toBinaryString(0xFF & bitValue) + " to "
                + Integer.toBinaryString(0xFF & flippedValue) + " -- flip bit 2");

        // ------------------------------------------
        //            test the LFSR class
        // ------------------------------------------
        byte[] seed = { (byte)0xF0, 0x1A, 0x05, 0x15 };
        LFSR lfsr = new LFSR();
        lfsr.init(seed);

        // generate pseudo-random bits
        byte random = lfsr.cycle();
        System.out.print(random);
        random = lfsr.cycle();
        System.out.print(random);
        random = lfsr.cycle();
        System.out.print(random);
        random = lfsr.cycle();
        System.out.print(random);
    }
}
