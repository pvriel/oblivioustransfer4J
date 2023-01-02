package com.github.pvriel.oblivioustransfer4j.ote.semihonest.alsz13;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionSender;
import com.github.pvriel.oblivioustransfer4j.utils.ArrayUtils;
import com.github.pvriel.oblivioustransfer4j.utils.KDFUtils;
import com.github.pvriel.oblivioustransfer4j.utils.RandomUtils;
import com.github.pvriel.oblivioustransfer4j.utils.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.utils.math.BigIntegerMatrix;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
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
        for (int i = 0; i < t_i.length; i ++) t_i[i] = G(k_i[i], x.length);

        // OT extension phase.
        BigInteger sAsBigInteger = ArrayUtils.convertToBigInteger(s);
        BigInteger[] q_i = new BigInteger[k_i.length];
        for (int i = 0; i < q_i.length; i ++) {
            BigInteger u_i = StreamUtils.readBigIntegerFromInputStream(inputStream);
            q_i[i] = (s[i]? u_i : BigInteger.ZERO).xor(t_i[i]);
        }
        BigIntegerMatrix Q = new BigIntegerMatrix(q_i, x.length);
        BigIntegerMatrix Q_transposed = Q.transpose();
        BigInteger[] q_j = Q_transposed.getColumns();
        for (int j = 0; j < x.length; j ++) {
            BigInteger y_j_0 = x[j][0].xor(H(j, q_j[j], bitLength));
            BigInteger y_j_1 = x[j][1].xor(H(j, q_j[j].xor(sAsBigInteger), bitLength));
            StreamUtils.writeBigIntegerToOutputStream(y_j_0, outputStream);
            StreamUtils.writeBigIntegerToOutputStream(y_j_1, outputStream);
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
