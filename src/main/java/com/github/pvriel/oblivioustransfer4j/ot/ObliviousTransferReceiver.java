package com.github.pvriel.oblivioustransfer4j.ot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Interface representing the receivers from the oblivious transfer protocols.
 */
public interface ObliviousTransferReceiver {

    /**
     * Method to execute the oblivious transfer protocol.
     * @param   choices
     *          The choices of the receiver.
     *          <br>This array should have the same length as the x array at the sender's side.
     * @param   bitLength
     *          The maximum bit length of the BigIntegers this receiver should receive.
     *          <br>Should be the same (greater than zero) value as the one used by the sender.
     * @param   inputStream
     *          The input from the sender.
     *          <br>This stream will not be closed after invoking this method.
     * @param   outputStream
     *          The output to the sender.
     *          <br>This stream will not be closed after invoking this method.
     * @return  The chosen values from the x array (for i in range(len(choices)): x[i][choices[i]]).
     * @throws  IOException
     *          If something went wrong with the communication to the {@link ObliviousTransferSender}.
     */
    BigInteger[] execute(boolean[] choices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;
}
