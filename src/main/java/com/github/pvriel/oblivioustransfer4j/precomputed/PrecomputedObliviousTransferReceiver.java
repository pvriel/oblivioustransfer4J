package com.github.pvriel.oblivioustransfer4j.precomputed;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public abstract class PrecomputedObliviousTransferReceiver<ReturnTypeOfflinePhase> implements ObliviousTransferReceiver {
    @Override
    public BigInteger[] execute(boolean[] choices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        var resultOfflinePhase = executeOfflinePhase(choices, bitLength, inputStream, outputStream);
        return executeOnlinePhase(resultOfflinePhase, choices, bitLength, inputStream, outputStream);
    }

    public abstract ReturnTypeOfflinePhase executeOfflinePhase(boolean[] choices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;

    public abstract BigInteger[] executeOnlinePhase(ReturnTypeOfflinePhase resultOfflinePhase, boolean[] choices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;
}
