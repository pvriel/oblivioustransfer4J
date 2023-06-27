package com.github.pvriel.oblivioustransfer4j.precomputed;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Interface, representing {@link ObliviousTransferSender}s, which can perform a part of their computation offline/in advance.
 * @param   <ReturnTypeOfflinePhase>
 *          The (part of the) result of the offline phase that can be reused to execute the online phase with.
 */
public interface PrecomputedObliviousTransferSender<ReturnTypeOfflinePhase> extends ObliviousTransferSender {

    /**
     * Method to execute the complete oblivious transfer protocol.
     * <br>Invoking this method has the same effect as invoking {@link #executeOfflinePhase(BigInteger[][], int, InputStream, OutputStream)} and {@link #executeOnlinePhase(Object, BigInteger[][], int, InputStream, OutputStream)} in sequence.
     * @param   x
     *          The x values of the sender.
     *          <br>This array should have the same length as the choices array at the receiver's side, with the second dimension being 2.
     *          <br>Also, no null (sub)-arrays or {@link BigInteger}s are allowed.
     * @param   bitLength
     *          The maximum bit length of the BigIntegers the receiver should receive.
     *          <br>Should be the same (greater than zero) value as the one used by the receiver.
     * @param   inputStream
     *          The input from the receiver.
     *          <br>This stream will not be closed after invoking this method.
     * @param   outputStream
     *          The output to the receiver.
     *          <br>This stream will not be closed after invoking this method.
     * @throws  IOException
     *          If the input or output streams throw an IOException during the execution.
     */
    @Override
    default void execute(BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        var resultOfflinePhase = executeOfflinePhase(x, bitLength, inputStream, outputStream);
        executeOnlinePhase(resultOfflinePhase, x, bitLength, inputStream, outputStream);
    }

    /**
     * Method to execute the offline phase of the precomputed oblivious transfer protocol.
     * @param   x
     *          The x values of the sender.
     *          <br>This array should have the same length as the choices array at the receiver's side, with the second dimension being 2.
     *          <br>Also, no null (sub)-arrays or {@link BigInteger}s are allowed.
     * @param   bitLength
     *          The maximum bit length of the BigIntegers this receiver should receive.
     *          <br>Should be the same (greater than zero) value as the one used by the receiver.
     * @param   inputStream
     *          The input from the receiver.
     *          <br>This stream will not be closed after invoking this method.
     * @param   outputStream
     *          The output to the receiver.
     *          <br>This stream will not be closed after invoking this method.
     * @return  The result of the offline phase.
     * @throws  IOException
     *          If the input or output streams throw an IOException during the execution.
     */
    ReturnTypeOfflinePhase executeOfflinePhase(BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;

    /**
     * Method to execute the online phase of the precomputed oblivious transfer protocol.
     * @param   resultOfflinePhase
     *          The result of the offline phase.
     *          <br>Should be the same (non-null) value as the one returned by {@link #executeOfflinePhase(BigInteger[][], int, InputStream, OutputStream)}.
     * @param   x
     *          The x values of the sender.
     *          <br>This array should have the same length as the choices array at the receiver's side, with the second dimension being 2.
     *          <br>Also, no null (sub)-arrays or {@link BigInteger}s are allowed.
     * @param   bitLength
     *          The maximum bit length of the BigIntegers this receiver should receive.
     *          <br>Should be the same (greater than zero) value as the one used by the receiver.
     * @param   inputStream
     *          The input from the receiver.
     *          <br>This stream will not be closed after invoking this method.
     * @param   outputStream
     *          The output to the receiver.
     *          <br>This stream will not be closed after invoking this method.
     * @throws  IOException
     *          If the input or output streams throw an IOException during the execution.
     */
    void executeOnlinePhase(ReturnTypeOfflinePhase resultOfflinePhase, BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;
}
