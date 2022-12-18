package com.github.pvriel.oblivioustransfer4j.precomputed;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public abstract class PrecomputedObliviousTransferSender<ReturnTypeOfflinePhase> implements ObliviousTransferSender {

    @Override
    public void execute(BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        var resultOfflinePhase = executeOfflinePhase(x, bitLength, inputStream, outputStream);
        executeOnlinePhase(resultOfflinePhase, x, bitLength, inputStream, outputStream);
    }

    public abstract ReturnTypeOfflinePhase executeOfflinePhase(BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;

    public abstract void executeOnlinePhase(ReturnTypeOfflinePhase resultOfflinePhase, BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;
}
