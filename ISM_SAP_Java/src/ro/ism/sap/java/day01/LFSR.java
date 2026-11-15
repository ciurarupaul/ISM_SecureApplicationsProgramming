package ro.ism.sap.java.day01;

// ------------------------------------------
//   Linear-Feedback Shift Register - LFSR
// ------------------------------------------

public class LFSR {
    //  32 bit LFSR -- x^31 + x^7 + x^5 + x^3 + x^2 + x^1 + 1
    // store the 32 bits in an integer OR byte array byte[4] OR BitSet library

    byte[] register = new byte[4];

    private byte getBitAtIndex(int index) {
        int byteIndex = 3 - index / 8;
        int bitIndex = index % 8;

        byte bitByte = this.register[byteIndex];
        byte bitValue = (byte) (0xFF & bitByte & (1 << bitIndex));

        return (byte) (bitValue != 0 ? 1 : 0);
    }

    // shifts a byte from the register by 1 to the left and returns the MSB
    private byte shiftAndReturn(int byteRegisterIndex) {
        byte msb = (byte) (this.register[byteRegisterIndex] & (1 << 7));
        this.register[byteRegisterIndex] = (byte)(0xFF & (this.register[byteRegisterIndex] << 1));
        return (byte) (msb != 0 ? 1 : 0);
    }

    private void addLSB(int registerIndex, byte lsb) {
        this.register[registerIndex] = (byte) (this.register[registerIndex] | lsb);
    }

    // apply a
    public byte cycle() {
        byte temp = (byte) (getBitAtIndex(31) ^ getBitAtIndex(7) ^ getBitAtIndex(5) ^ getBitAtIndex(3) ^ getBitAtIndex(2) ^ getBitAtIndex(1) ^ getBitAtIndex(0));

        byte outputBit1 = shiftAndReturn(3);
        addLSB(3, temp);
        byte outputBit2 = shiftAndReturn(2);
        addLSB(2, outputBit1);
        byte outputBit3 = shiftAndReturn(1);
        addLSB(1, outputBit2);
        byte outputBit4 = shiftAndReturn(0);
        addLSB(0, outputBit3);

        return outputBit4;
    }

    public void init(byte[] seed) {
        for(int i = 0; i < this.register.length; i++) {
            this.register[i] = seed[i];
        }
    }
}
