package com.github.pvriel.oblivioustransfer4j.srot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Interface representing the senders-part of random oblivious transfer protocols.
 */
public interface RandomObliviousTransferSender {

    /**
     * Method to execute the sender-part of the random oblivious transfer protocol.
     * @param   amountOfChoices
     *          The amount of random choices the receiver makes.
     *          <br>Should be a strictly positive value and match the value used at the receiver side.
     * @param   bitLength
     *          The bit length of the values the receiver will receive.
     *          <br>Should be a strictly positive value and match the value used at the receiver side.
     * @param   inputStream
     *          The (not-null) inputStream to communicate with the receiver-part.
     *          <br>This inputStream will not be closed after the execution of the protocol.
     * @param   outputStream
     *          The (not-null) outputStream to communicate with the receiver-part.
     *          <br>This outputStream will not be closed after the execution of the protocol.
     * @return  An amountOfChoices x 2 array, consisting of the values the receiver could choose from during
     *          the execution of the protocol.
     *          <br>The return value is not null, and does not contain any null values.
     * @throws  IOException
     *          If something went wrong with the communication with the receiver during the execution of the protocol.
     */
    BigInteger[][] execute(int amountOfChoices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;
}
