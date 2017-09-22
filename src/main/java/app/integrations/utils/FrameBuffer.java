package app.integrations.utils;

public class FrameBuffer {
    private int[] localBuffer = new int[1024];
    private int positionOfFirst;
    private int positionOfNext;

    public int available() {
        return positionOfNext - positionOfFirst;
    }

    public int read() {
        int value = localBuffer[positionOfFirst++];
        return value;
    }

    public int read(int[] buffer) {
        normalizeIfNeeded();
        int length = Math.min(positionOfNext, buffer.length);
        if (length == 0) {
            return 0;
        }

        System.arraycopy(localBuffer, 0, buffer, 0, length);
        System.arraycopy(localBuffer, length, localBuffer, 0, positionOfNext - length);
        positionOfNext -= length;
        return length;
    }

    public void push(int value) {
        increaseIfNeeded(1);
        localBuffer[positionOfNext++] = value;
    }

    public void push(int[] buffer) {
        increaseIfNeeded(buffer.length);
        System.arraycopy(buffer, 0, localBuffer, positionOfNext, buffer.length);
        positionOfNext += buffer.length;
    }

    private void increaseIfNeeded(int length) {
        normalizeIfNeeded();
        int requiredLength = positionOfNext + length;
        if (localBuffer.length < requiredLength) {
            int[] oldBuffer = localBuffer;
            int newLength = calculateNewLength(requiredLength);
            localBuffer = new int[newLength];
            System.arraycopy(oldBuffer, 0, localBuffer, 0, oldBuffer.length);
        }
    }

    private void normalizeIfNeeded() {
        if (positionOfFirst == 0) {
            return;
        }

        int size = positionOfNext - positionOfFirst;
        System.arraycopy(localBuffer, positionOfFirst, localBuffer, 0, size);
        positionOfFirst = 0;
        positionOfNext = size;
    }

    private int calculateNewLength(int requiredLength) {
        int newLength = localBuffer.length;
        do {
            newLength *= 2;
        } while (newLength < requiredLength);
        return newLength;
    }
}
