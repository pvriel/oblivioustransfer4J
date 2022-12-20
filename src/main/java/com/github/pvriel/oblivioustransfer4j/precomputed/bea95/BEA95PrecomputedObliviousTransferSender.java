package com.github.pvriel.oblivioustransfer4j.precomputed.bea95;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.precomputed.PrecomputedObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.utils.RandomUtils;
import com.github.pvriel.oblivioustransfer4j.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class BEA95PrecomputedObliviousTransferSender extends PrecomputedObliviousTransferSender<BigInteger[][]> {

    private final ObliviousTransferSender underlyingObliviousTransferSender;

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
