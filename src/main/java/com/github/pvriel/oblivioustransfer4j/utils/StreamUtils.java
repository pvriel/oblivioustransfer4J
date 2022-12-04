package com.github.pvriel.oblivioustransfer4j.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public abstract class StreamUtils {

    public static void writeToOutputStream(BigInteger bigInteger, OutputStream outputStream) throws IOException {
        outputStream.write(addLengthPrefix(bigInteger.toByteArray()));
    }

    public static BigInteger readFromInputStream(Class<BigInteger> clazz, InputStream inputStream) throws IOException {
        return new BigInteger(receiveInputWithLengthPrefix(inputStream));
    }

    private static byte[] addLengthPrefix(byte[] array) {
        return ByteBuffer.allocate(array.length + 4)
                .putInt(array.length)
                .put(array)
                .array();
    }

    private static byte[] receiveInputWithLengthPrefix(InputStream inputStream) throws IOException {
        ByteBuffer byteBuffer = ByteBuffer.wrap(inputStream.readNBytes(4));
        int length = byteBuffer.getInt();
        byteBuffer.clear();

        return inputStream.readNBytes(length);
    }
}
