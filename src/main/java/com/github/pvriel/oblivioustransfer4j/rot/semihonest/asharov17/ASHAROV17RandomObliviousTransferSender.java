package com.github.pvriel.oblivioustransfer4j.rot.semihonest.asharov17;

import com.github.pvriel.mpcutils4j.array.ArrayUtils;
import com.github.pvriel.mpcutils4j.crypto.KDFUtils;
import com.github.pvriel.mpcutils4j.math.BigIntegerMatrix;
import com.github.pvriel.mpcutils4j.random.RandomUtils;
import com.github.pvriel.mpcutils4j.stream.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.srot.RandomObliviousTransferSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.stream.IntStream;

/**
 * Class representing an implementation of the sender part from the R-OT protocol of <a href=https://eprint.iacr.org/2016/602>ASHAROV17</a>.
 */
public class ASHAROV17RandomObliviousTransferSender implements RandomObliviousTransferSender {

    private final int amountOfBaseOTs;
    private final ObliviousTransferReceiver baseOTsReceiver;

    /**
     * Constructor for the {@link ASHAROV17RandomObliviousTransferSender} class.
     * @param   amountOfBaseOTs
     *          The amount of base OTs to use. Should be a value of at least one.
     * @param   baseOTsReceiver
     *          The (not-null) {@link ObliviousTransferReceiver} instance to use for the base OTs.
     */
    public ASHAROV17RandomObliviousTransferSender(int amountOfBaseOTs, ObliviousTransferReceiver baseOTsReceiver) {
        this.amountOfBaseOTs = amountOfBaseOTs;
        this.baseOTsReceiver = baseOTsReceiver;
    }

    @Override
    public BigInteger[][] execute(int amountOfChoices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        boolean[] s = RandomUtils.generateRandomBooleanArrayOfLength(amountOfBaseOTs);
        BigInteger[] k_i_s = baseOTsReceiver.execute(s, amountOfBaseOTs, inputStream, outputStream);
        BigInteger[] t_i = new BigInteger[k_i_s.length];
        IntStream.range(0, t_i.length).parallel().forEach(i -> t_i[i] = G(k_i_s[i], amountOfChoices));
        BigInteger sAsBigInteger = ArrayUtils.convertToBigInteger(s);

        BigInteger[] q_i = new BigInteger[amountOfBaseOTs];
        q_i[0] = G(k_i_s[0], amountOfChoices);
        BigInteger[] u_i = new BigInteger[q_i.length - 1];
        for (int i = 1; i < q_i.length; i ++) {
            u_i[i - 1] = StreamUtils.readBigIntegerFromInputStream(inputStream);
        }
        IntStream.range(1, q_i.length).parallel().forEach(i -> {
            BigInteger G_k_i = G(k_i_s[i], amountOfChoices);
            q_i[i] = s[i]? u_i[i - 1].xor(G_k_i) : G_k_i;
        });
        BigIntegerMatrix Q = new BigIntegerMatrix(q_i, amountOfChoices);
        BigIntegerMatrix Q_transposed = Q.transpose();
        BigInteger[] q_j = Q_transposed.getColumns();
        BigInteger[][] returnValue = new BigInteger[amountOfChoices][2];
        IntStream.range(0, amountOfChoices).parallel().forEach(j -> {
            returnValue[j][0] = H(j, q_j[j], bitLength);
            returnValue[j][1] = H(j, q_j[j].xor(sAsBigInteger), bitLength);
        });
        return returnValue;
    }

    static BigInteger G(BigInteger element, int bitLength) {
        return KDFUtils.KDF(element, bitLength);
    }

    static BigInteger H(int j, BigInteger q_j, int bitLength) {
        return KDFUtils.KDF(q_j.xor(BigInteger.valueOf(j)), bitLength);
    }
}
