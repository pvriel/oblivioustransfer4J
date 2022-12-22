package com.github.pvriel.oblivioustransfer4j.precomputed.semihonest.bea95;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.precomputed.PrecomputedObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.utils.RandomUtils;
import com.github.pvriel.oblivioustransfer4j.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

/**
 * Class representing senders in the semi-honest <a href="https://doi.org/10.1007/3-540-44750-4_8">BEA95</a> precomputed oblivious transfer protocol.
 */
public class BEA95PrecomputedObliviousTransferSender implements PrecomputedObliviousTransferSender<BigInteger[][]> {

    private final ObliviousTransferSender underlyingObliviousTransferSender;

    /**
     * Constructor for the {@link BEA95PrecomputedObliviousTransferSender} class.
     * @param   underlyingObliviousTransferSender
     *          The (not-null) underlying oblivious transfer sender to use.
     */
    public BEA95PrecomputedObliviousTransferSender(ObliviousTransferSender underlyingObliviousTransferSender) {
        this.underlyingObliviousTransferSender = underlyingObliviousTransferSender;
    }

    public BigInteger[][] executeOfflinePhase(BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        BigInteger[][] randomValues = RandomUtils.generateRandomBigInteger2DArrayOfLengths(x.length, 2, bitLength);
        underlyingObliviousTransferSender.execute(randomValues, bitLength, inputStream, outputStream);
        return randomValues;
    }

    public void executeOnlinePhase(BigInteger[][] resultOfflinePhase, BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        BigInteger b_adjusted = StreamUtils.readFromInputStream(BigInteger.class, inputStream);
        for (int i = 0; i < x.length; i ++) {
            boolean b_adjusted_i = b_adjusted.testBit(i);
            BigInteger s0_adjusted = (x[i][0]).xor(resultOfflinePhase[i][b_adjusted_i? 1 : 0]);
            BigInteger s1_adjusted = (x[i][1]).xor(resultOfflinePhase[i][b_adjusted_i? 0 : 1]);

            StreamUtils.writeToOutputStream(s0_adjusted, outputStream);
            StreamUtils.writeToOutputStream(s1_adjusted, outputStream);
            outputStream.flush();
        }
    }
}
