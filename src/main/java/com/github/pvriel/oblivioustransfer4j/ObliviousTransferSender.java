package com.github.pvriel.oblivioustransfer4j;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Interface representing the senders from the oblivious transfer protocols.
 */
public interface ObliviousTransferSender {

    /**
     * Method to execute the oblivious transfer protocol.
     * @param   x
     *          The provided values from which the receiver can choose.
     *          <br>This array should have the same length as the choices array at the sender's side. The second dimension should be two.
     *          <br>This array (and the sub arrays) should not contain any null values, and all the provided BigInteger instances should have the same bit length.
     * @param   inputStream
     *          The input from the receiver.
     *          <br>This stream will not be closed after invoking this method.
     * @param   outputStream
     *          The output to the receiver.
     *          <br>This stream will not be closed after invoking this method.
     */
    void execute(BigInteger[][] x, InputStream inputStream, OutputStream outputStream);
}
