package com.github.pvriel.oblivioustransfer4j.precomputed.semihonest.bea95;

import com.github.pvriel.mpcutils4j.random.RandomUtils;
import com.github.pvriel.mpcutils4j.stream.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.precomputed.PrecomputedObliviousTransferSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.stream.IntStream;

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

    @Override
    public BigInteger[][] executeOfflinePhase(BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        BigInteger[][] randomValues = RandomUtils.generateRandomBigInteger2DArrayOfLengths(x.length, 2, bitLength);
        underlyingObliviousTransferSender.execute(randomValues, bitLength, inputStream, outputStream);
        return randomValues;
    }

    @Override
    public void executeOnlinePhase(BigInteger[][] resultOfflinePhase, BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        BigInteger b_adjusted = StreamUtils.readBigIntegerFromInputStream( inputStream);
        BigInteger[] s0_adjusted = new BigInteger[x.length];
        BigInteger[] s1_adjusted = new BigInteger[x.length];
        IntStream.range(0, x.length).parallel().forEach(i -> {
            boolean b_adjusted_i = b_adjusted.testBit(i);
            s0_adjusted[i] = (x[i][0]).xor(resultOfflinePhase[i][b_adjusted_i? 1 : 0]);
            s1_adjusted[i] = (x[i][1]).xor(resultOfflinePhase[i][b_adjusted_i? 0 : 1]);
        });

        for (int i = 0; i < x.length; i ++) {
            StreamUtils.writeBigIntegerToOutputStream(s0_adjusted[i], outputStream);
            StreamUtils.writeBigIntegerToOutputStream(s1_adjusted[i], outputStream);
        }
        outputStream.flush();
    }
}
