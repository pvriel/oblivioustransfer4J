package com.github.pvriel.oblivioustransfer4j.utils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.nio.ByteBuffer;

/**
 * Abstract class, introducing help methods to write/read objects to/from {@link OutputStream}s/{@link InputStream}s.
 * <br>This class does not introduce the same communication overhead as the {@link java.io.ObjectOutputStream}s/{@link java.io.ObjectInputStream}s do.
 */
public abstract class StreamUtils {

    /**
     * Method to write a {@link BigInteger} instance to an {@link OutputStream}.
     * This method converts the {@link BigInteger} to a byte array, and a 4-byte long length prefix at the beginning of the resulting array.
     * <br>This method is compatible with the readFromInputStream method.
     * @param   bigInteger
     *          The {@link BigInteger} instance to write.
     * @param   outputStream
     *          The {@link OutputStream} to write the instance to.
     * @throws  IOException
     *          If the {@link OutputStream} threw an exception while writing the data.
     */
    public static void writeBigIntegerToOutputStream(BigInteger bigInteger, OutputStream outputStream) throws IOException {
        outputStream.write(addLengthPrefix(bigInteger.toByteArray()));
    }

    /**
     * Method to read a {@link BigInteger} instance from an {@link InputStream}.
     * <br>This method is compatible with the writeToOutputStream method.
     * @param   inputStream
     *          The {@link InputStream} to read the data from.
     * @return  The resulting {@link BigInteger}.
     * @throws  IOException
     *          If the {@link InputStream} threw an exception while reading the data.
     */
    public static BigInteger readBigIntegerFromInputStream(InputStream inputStream) throws IOException {
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
