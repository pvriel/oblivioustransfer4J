package com.github.pvriel.oblivioustransfer4j.precomputed.semihonest.bea95;

import com.github.pvriel.mpcutils4j.random.RandomUtils;
import com.github.pvriel.mpcutils4j.stream.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.precomputed.PrecomputedObliviousTransferReceiver;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Class representing receivers in the semi-honest <a href="https://doi.org/10.1007/3-540-44750-4_8">BEA95</a> precomputed oblivious transfer protocol.
 */
public class BEA95PrecomputedObliviousTransferReceiver implements PrecomputedObliviousTransferReceiver<Pair<boolean[], BigInteger[]>> {

    private final ObliviousTransferReceiver underlyingObliviousTransferReceiver;

    /**
     * Constructor for the {@link BEA95PrecomputedObliviousTransferReceiver} class.
     * @param   underlyingObliviousTransferReceiver
     *          The (not-null) underlying oblivious transfer receiver to use.
     */
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
        StreamUtils.writeBigIntegerToOutputStream(b_adjusted, outputStream);
        outputStream.flush();

        BigInteger[] receivedValuesOfflinePhase = resultOfflinePhase.getRight();
        BigInteger[] sb_adjusted = new BigInteger[choices.length];

        for (int i = 0; i < choices.length; i ++) {
            if (choices[i]) StreamUtils.readBigIntegerFromInputStream( inputStream);
            sb_adjusted[i] = StreamUtils.readBigIntegerFromInputStream( inputStream);
            if (!choices[i]) StreamUtils.readBigIntegerFromInputStream( inputStream);
        }
        BigInteger[] returnValue = new BigInteger[choices.length];
        for (int i = 0; i < choices.length; i ++) returnValue[i] = sb_adjusted[i].xor(receivedValuesOfflinePhase[i]);
        return returnValue;
    }
}
