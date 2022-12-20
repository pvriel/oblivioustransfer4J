package com.github.pvriel.oblivioustransfer4j.precomputed.bea95;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.precomputed.PrecomputedObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.utils.RandomUtils;
import com.github.pvriel.oblivioustransfer4j.utils.StreamUtils;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class BEA95PrecomputedObliviousTransferReceiver extends PrecomputedObliviousTransferReceiver<Pair<boolean[], BigInteger[]>> {

    private final ObliviousTransferReceiver underlyingObliviousTransferReceiver;

    public BEA95PrecomputedObliviousTransferReceiver(ObliviousTransferReceiver underlyingObliviousTransferReceiver) {
        this.underlyingObliviousTransferReceiver = underlyingObliviousTransferReceiver;
    }

    @Override
    public Pair<boolean[], BigInteger[]> executeOfflinePhase(boolean[] choices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        boolean[] randomChoices = RandomUtils.generateRandomBooleanArrayOfLength(choices.length);
        BigInteger[] receivedValues = underlyingObliviousTransferReceiver.execute(randomChoices, bitLength, inputStream, outputStream);
        return Pair.of(randomChoices, receivedValues);
    }

    @Override
    public BigInteger[] executeOnlinePhase(Pair<boolean[], BigInteger[]> resultOfflinePhase, boolean[] choices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        boolean[] choicesOfflinePhase = resultOfflinePhase.getLeft();
        BigInteger b_adjusted = BigInteger.ZERO;
        for (int i = 0; i <choices.length; i ++) if (choicesOfflinePhase[i] != choices[i]) b_adjusted = b_adjusted.setBit(i); // TODO: maybe not the most efficient method. Fix this.
        StreamUtils.writeToOutputStream(b_adjusted, outputStream);
        outputStream.flush();

        BigInteger[] receivedValuesOfflinePhase = resultOfflinePhase.getRight();
        BigInteger[] returnValue = new BigInteger[choices.length];
        for (int i = 0; i < choices.length; i ++) {
            BigInteger sb_adjusted;
            if (choices[i]) StreamUtils.readFromInputStream(BigInteger.class, inputStream);
            sb_adjusted = StreamUtils.readFromInputStream(BigInteger.class, inputStream);
            if (!choices[i]) StreamUtils.readFromInputStream(BigInteger.class, inputStream);
            returnValue[i] = sb_adjusted.xor(receivedValuesOfflinePhase[i]);
        }
        return returnValue;
    }
}
