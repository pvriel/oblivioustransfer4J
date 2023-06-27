package com.github.pvriel.oblivioustransfer4j.rrot.semihonest.asharov17;

import com.github.pvriel.mpcutils4j.array.ArrayUtils;
import com.github.pvriel.mpcutils4j.crypto.KDFUtils;
import com.github.pvriel.mpcutils4j.math.BigIntegerMatrix;
import com.github.pvriel.mpcutils4j.random.RandomUtils;
import com.github.pvriel.mpcutils4j.stream.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.rrot.RandomObliviousTransferReceiver;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.stream.IntStream;

/**
 * Class representing an implementation of the receiver part from the RR-OT protocol of <a href=https://eprint.iacr.org/2016/602>ASHAROV17</a>.
 */
public class ASHAROV17ReceiverRandomObliviousTransferReceiver implements RandomObliviousTransferReceiver {

    private final int amountOfBaseOTs;
    private final ObliviousTransferSender baseOTsSender;

    /**
     * Constructor for the {@link ASHAROV17ReceiverRandomObliviousTransferReceiver} class.
     * @param   amountOfBaseOTs
     *          The amount of base OTs to use.
     *          <br>Should be a strictly positive value, and match the value used by the other party.
     * @param   baseOTsSender
     *          The not-null {@link ObliviousTransferSender} instance to use for the base OTs.
     */
    public ASHAROV17ReceiverRandomObliviousTransferReceiver(int amountOfBaseOTs, ObliviousTransferSender baseOTsSender) {
        this.amountOfBaseOTs = amountOfBaseOTs;
        this.baseOTsSender = baseOTsSender;
    }

    @Override
    public Pair<boolean[], BigInteger[]> execute(int amountOfChoices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        BigInteger[][] k_i = RandomUtils.generateRandomBigInteger2DArrayOfLengths(amountOfBaseOTs, 2, amountOfBaseOTs);
        baseOTsSender.execute(k_i, amountOfBaseOTs, inputStream, outputStream);

        BigInteger[] t_i = new BigInteger[amountOfBaseOTs];
        t_i[0] = G(k_i[0][0], amountOfChoices);
        BigInteger G_k_0_1 = G(k_i[0][1], amountOfChoices);
        BigInteger r = t_i[0].xor(G_k_0_1);
        boolean[] choices = ArrayUtils.convertFromBigInteger(r, amountOfChoices);
        BigInteger[] u_i = new BigInteger[amountOfBaseOTs - 1];
        IntStream.range(1, t_i.length).forEach(i -> {
            t_i[i] = G(k_i[i][0], amountOfChoices);
            BigInteger G_k_1 = G(k_i[i][1], amountOfChoices);
            u_i[i - 1] = t_i[i].xor(G_k_1).xor(r);
        });
        for (BigInteger bigInteger : u_i) {
            StreamUtils.writeBigIntegerToOutputStream(bigInteger, outputStream);
            outputStream.flush();
        }
        BigIntegerMatrix T = new BigIntegerMatrix(t_i, amountOfChoices);
        BigIntegerMatrix T_transposed = T.transpose();
        BigInteger[] t_j = T_transposed.getColumns();

        BigInteger[] returnValue = new BigInteger[amountOfChoices];
        BigInteger[] y0 = new BigInteger[returnValue.length];
        BigInteger[] y1 = new BigInteger[returnValue.length];
        for (int i = 0; i < returnValue.length; i ++) {
            y0[i] = StreamUtils.readBigIntegerFromInputStream(inputStream);
            y1[i] = StreamUtils.readBigIntegerFromInputStream(inputStream);
        }
        IntStream.range(0, returnValue.length).parallel().forEach(i -> returnValue[i] = (choices[i]? y1[i] : y0[i]).xor(H(i, t_j[i], bitLength)));
        return Pair.of(choices, returnValue);
    }

    static BigInteger G(BigInteger element, int bitLength) {
        return KDFUtils.KDF(element, bitLength);
    }

    static BigInteger H(int j, BigInteger q_j, int bitLength) {
        return KDFUtils.KDF(q_j.xor(BigInteger.valueOf(j)), bitLength);
    }
}
