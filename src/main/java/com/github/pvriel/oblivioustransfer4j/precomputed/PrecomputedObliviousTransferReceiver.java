package com.github.pvriel.oblivioustransfer4j.precomputed;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Interface, representing {@link ObliviousTransferReceiver}s, which can perform a part of their computation offline/in advance.
 * @param   <ReturnTypeOfflinePhase>
 *          The (part of the) result of the offline phase that can be reused to execute the online phase with.
 */
public interface PrecomputedObliviousTransferReceiver<ReturnTypeOfflinePhase> extends ObliviousTransferReceiver {
    /**
     * Method to execute the complete oblivious transfer protocol.
     * <br>Invoking this method has the same effect as invoking {@link #executeOfflinePhase(boolean[], int, InputStream, OutputStream)} and {@link #executeOnlinePhase(Object, boolean[], int, InputStream, OutputStream)} in sequence.
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
     * @return  The chosen, received BigIntegers.
     * @throws  IOException
     *          If the input or output streams throw an IOException during the execution.
     */
    @Override
    default BigInteger[] execute(boolean[] choices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        var resultOfflinePhase = executeOfflinePhase(choices, bitLength, inputStream, outputStream);
        return executeOnlinePhase(resultOfflinePhase, choices, bitLength, inputStream, outputStream);
    }

    /**
     * Method to execute the offline phase of the precomputed oblivious transfer protocol.
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
     * @return  The result of the offline phase.
     * @throws  IOException
     *          If the input or output streams throw an IOException during the execution.
     */
    ReturnTypeOfflinePhase executeOfflinePhase(boolean[] choices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;

    /**
     * Method to execute the online phase of the precomputed oblivious transfer protocol.
     * @param   resultOfflinePhase
     *          The result of the offline phase.
     *          <br>Should be the same (non-null) value as the one returned by {@link #executeOfflinePhase(boolean[], int, InputStream, OutputStream)}.
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
     * @return  The chosen, received BigIntegers.
     * @throws  IOException
     *          If the input or output streams throw an IOException during the execution.
     */
    BigInteger[] executeOnlinePhase(ReturnTypeOfflinePhase resultOfflinePhase, boolean[] choices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;
}
