package app.integrations.utils;

public class Primitives {

    public static int littleEndianByteArrayToInt(byte[] buffer, int position, int size) {
        int value = 0;
        for (int i = 0; i < size; i++) {
            int byteValue = buffer[position + i];
            if (i != size - 1) {
                byteValue &= 0xFF;
            }
            value += byteValue << i * 8;
        }
        return value;
    }

    public static byte[] intToBigEndianByteArray(int value, int sizeInBytes) {
        byte[] array = new byte[sizeInBytes];
        for (int i = 0; i < sizeInBytes; i++) {
            array[i] = (byte) ((value >> ((sizeInBytes - i) * 8)) & 0xFF);
        }
        return array;
    }

    public static byte[] intToLittleEndianByteArray(int value, int sizeInBytes) {
        byte[] array = new byte[sizeInBytes];
        for (int i = 0; i < sizeInBytes; i++) {
            array[i] = (byte) ((value >> (i * 8)) & 0xFF);
        }
        return array;
    }
}
