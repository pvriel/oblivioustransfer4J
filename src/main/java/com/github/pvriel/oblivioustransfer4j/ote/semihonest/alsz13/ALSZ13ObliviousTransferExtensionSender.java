package com.github.pvriel.oblivioustransfer4j.ote.semihonest.alsz13;

import com.github.pvriel.mpcutils4j.array.ArrayUtils;
import com.github.pvriel.mpcutils4j.crypto.KDFUtils;
import com.github.pvriel.mpcutils4j.math.BigIntegerMatrix;
import com.github.pvriel.mpcutils4j.random.RandomUtils;
import com.github.pvriel.mpcutils4j.stream.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.stream.IntStream;

/**
 * Class representing an implementation of the semi-honest <a href="https://doi.org/10.1145/2508859.2516738">ALSZ13</a> {@link ObliviousTransferExtensionSender}.
 */
public class ALSZ13ObliviousTransferExtensionSender extends ObliviousTransferExtensionSender {

    /**
     * Constructor for the {@link ALSZ13ObliviousTransferExtensionSender} class.
     * @param   amountOfBaseOTs
     *          The amount of base OTs to be executed.
     *          <br>This should be a value of at least one.
     * @param   baseOTsReceiver
     *          The (not-null) {@link ObliviousTransferReceiver} to be used for the base OTs.
     */
    public ALSZ13ObliviousTransferExtensionSender(int amountOfBaseOTs, ObliviousTransferReceiver baseOTsReceiver) {
        super(amountOfBaseOTs, baseOTsReceiver);
    }

    @Override
    public void execute(BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        // Initial OT phase.
        boolean[] s = RandomUtils.generateRandomBooleanArrayOfLength(getAmountOfBaseOTs());
        BigInteger[] k_i = getBaseOTsReceiver().execute(s, getAmountOfBaseOTs(), inputStream, outputStream);
        BigInteger[] t_i = new BigInteger[k_i.length];
        IntStream.range(0, t_i.length).parallel().forEach(i -> t_i[i] = G(k_i[i], x.length));

        // OT extension phase.
        BigInteger sAsBigInteger = ArrayUtils.convertToBigInteger(s);
        BigInteger[] u_i = new BigInteger[k_i.length];
        for (int i = 0; i < u_i.length; i ++) {
            u_i[i] = StreamUtils.readBigIntegerFromInputStream(inputStream);
        }
        BigInteger[] q_i = new BigInteger[k_i.length];
        IntStream.range(0, q_i.length).parallel().forEach(i -> q_i[i] = (s[i]? u_i[i] : BigInteger.ZERO).xor(t_i[i]));

        BigIntegerMatrix Q = new BigIntegerMatrix(q_i, x.length);
        BigIntegerMatrix Q_transposed = Q.transpose();
        BigInteger[] q_j = Q_transposed.getColumns();
        BigInteger[][] y = new BigInteger[x.length][2];
        IntStream.range(0, x.length).parallel().forEach(i -> {
            y[i][0] = x[i][0].xor(H(i, q_j[i], bitLength));
            y[i][1] = x[i][1].xor(H(i, q_j[i].xor(sAsBigInteger), bitLength));
        });
        for (int j = 0; j < x.length; j ++) {
            StreamUtils.writeBigIntegerToOutputStream(y[j][0], outputStream);
            StreamUtils.writeBigIntegerToOutputStream(y[j][1], outputStream);
            outputStream.flush();
        }
    }

    static BigInteger G(BigInteger element, int bitLength) {
        return KDFUtils.KDF(element, bitLength);
    }

    static BigInteger H(int j, BigInteger q_j, int bitLength) {
        return KDFUtils.KDF(q_j.xor(BigInteger.valueOf(j)), bitLength);
    }
}
