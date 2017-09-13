package app.integrations.utils;

public class Primitives {

    public static byte[] intToBigEndianByteArray2(int value) {
        byte[] array = new byte[2];
        array[0] = (byte) ((value >> 8) & 0xFF);
        array[1] = (byte) (value & 0xFF);
        return array;
    }

    public static byte[] intToLittleEndianByteArray2(int value) {
        byte[] array = new byte[2];
        array[0] = (byte) (value & 0xFF);
        array[1] = (byte) ((value >> 8) & 0xFF);
        return array;
    }

    public static byte[] intToLittleEndianByteArray4(int value) {
        byte[] array = new byte[4];
        array[0] = (byte) (value & 0xFF);
        array[1] = (byte) ((value >> 8) & 0xFF);
        array[2] = (byte) ((value >> 16) & 0xFF);
        array[3] = (byte) ((value >> 24) & 0xFF);
        return array;
    }
}
