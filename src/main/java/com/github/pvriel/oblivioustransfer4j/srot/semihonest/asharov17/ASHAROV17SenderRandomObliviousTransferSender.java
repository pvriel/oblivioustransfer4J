package com.github.pvriel.oblivioustransfer4j.srot.semihonest.asharov17;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.srot.RandomObliviousTransferSender;
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
 * Class representing an implementation of the sender part from the SR-OT protocol of <a href=https://eprint.iacr.org/2016/602>ASHAROV17</a>.
 */
public class ASHAROV17SenderRandomObliviousTransferSender implements RandomObliviousTransferSender {

    private final int amountOfBaseOTs;
    private final ObliviousTransferReceiver baseOTsReceiver;

    /**
     * Constructor for the {@link ASHAROV17SenderRandomObliviousTransferSender} class.
     * @param   amountOfBaseOTs
     *          The amount of base OTs to use.
     *          <br>Should be strictly positive, and should match the value used by the other party.
     * @param   baseOTsReceiver
     *          The not-null {@link ObliviousTransferReceiver} instance to use for the base OTs.
     */
    public ASHAROV17SenderRandomObliviousTransferSender(int amountOfBaseOTs, ObliviousTransferReceiver baseOTsReceiver) {
        this.amountOfBaseOTs = amountOfBaseOTs;
        this.baseOTsReceiver = baseOTsReceiver;
    }

    @Override
    public BigInteger[][] execute(int amountOfChoices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        // Initial OT phase.
        boolean[] s = RandomUtils.generateRandomBooleanArrayOfLength(amountOfBaseOTs);
        BigInteger[] k_i = baseOTsReceiver.execute(s, amountOfBaseOTs, inputStream, outputStream);
        BigInteger[] t_i = new BigInteger[k_i.length];
        for (int i = 0; i < t_i.length; i ++) t_i[i] = G(k_i[i], amountOfChoices);

        // OT extension phase.
        BigInteger sAsBigInteger = ArrayUtils.convertToBigInteger(s);
        BigInteger[] q_i = new BigInteger[k_i.length];
        for (int i = 0; i < q_i.length; i ++) {
            BigInteger u_i = StreamUtils.readBigIntegerFromInputStream( inputStream);
            q_i[i] = (s[i]? u_i : BigInteger.ZERO).xor(t_i[i]);
        }
        BigIntegerMatrix Q = new BigIntegerMatrix(q_i, amountOfChoices);
        BigIntegerMatrix Q_transposed = Q.transpose();
        BigInteger[] q_j = Q_transposed.getColumns();
        BigInteger[][] returnValue = new BigInteger[amountOfChoices][2];
        for (int j = 0; j < amountOfChoices; j ++) {
            returnValue[j][0] = H(j, q_j[j], bitLength);
            returnValue[j][1] = H(j, q_j[j].xor(sAsBigInteger), bitLength);
        }
        return returnValue;
    }

    static BigInteger G(BigInteger element, int bitLength) {
        return KDFUtils.KDF(element, bitLength);
    }

    static BigInteger H(int j, BigInteger q_j, int bitLength) {
        return KDFUtils.KDF(q_j.xor(BigInteger.valueOf(j)), bitLength);
    }
}
