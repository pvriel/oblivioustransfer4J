package com.github.pvriel.oblivioustransfer4j.rrot;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Interface representing the receivers-part of random oblivious transfer protocols.
 */
public interface RandomObliviousTransferReceiver {

    /**
     * Method to execute the receiver-part of the random oblivious transfer protocol.
     * @param   amountOfChoices
     *          The amount of random choices this receiver will make.
     *          <br>Should be a strictly positive value and match the value used at the sender side.
     * @param   bitLength
     *          The bit length of the values this receiver will receive.
     *          <br>Should be a strictly positive value and match the value used at the sender side.
     * @param   inputStream
     *          The (not-null) inputStream to communicate with the sender-part.
     *          <br>This inputStream will not be closed after the execution of the protocol.
     * @param   outputStream
     *          The (not-null) outputStream to communicate with the sender-part.
     *          <br>This outputStream will not be closed after the execution of the protocol.
     * @return  A non-null pair, containing two arrays of length 'amountOfChoices'.
     *          <br>The first array contains the random choices made by the receiver, while the second
     *          array contains the received values from the sender.
     * @throws  IOException
     *          If something went wrong with the communication with the sender during the execution of the protocol.
     */
    Pair<boolean[], BigInteger[]> execute(int amountOfChoices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;
}
