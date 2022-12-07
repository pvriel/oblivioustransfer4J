package com.github.pvriel.oblivioustransfer4j.ot;

import java.io.IOException;
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
     * @param   bitLength
     *          The maximum bit length of the BigIntegers this sender is sending.
     *          <br>Should be the same (greater than zero) value as the one used by the receiver.
     * @param   inputStream
     *          The input from the receiver.
     *          <br>This stream will not be closed after invoking this method.
     * @param   outputStream
     *          The output to the receiver.
     *          <br>This stream will not be closed after invoking this method.
     * @throws  IOException
     *          If something went wrong during the communication with the {@link ObliviousTransferReceiver}.
     */
    void execute(BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;
}
