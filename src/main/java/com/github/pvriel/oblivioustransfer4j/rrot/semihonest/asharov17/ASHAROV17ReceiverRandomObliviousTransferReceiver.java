package com.github.pvriel.oblivioustransfer4j.rrot.semihonest.asharov17;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.rrot.RandomObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.utils.ArrayUtils;
import com.github.pvriel.oblivioustransfer4j.utils.KDFUtils;
import com.github.pvriel.oblivioustransfer4j.utils.RandomUtils;
import com.github.pvriel.oblivioustransfer4j.utils.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.utils.math.BigIntegerMatrix;
import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public class ASHAROV17ReceiverRandomObliviousTransferReceiver implements RandomObliviousTransferReceiver {

    private final int amountOfBaseOTs;
    private final ObliviousTransferSender baseOTsSender;

    public ASHAROV17ReceiverRandomObliviousTransferReceiver(int amountOfBaseOTs, ObliviousTransferSender baseOTsSender) {
        this.amountOfBaseOTs = amountOfBaseOTs;
        this.baseOTsSender = baseOTsSender;
    }

    @Override
    public Pair<boolean[], BigInteger[]> execute(int amountOfChoices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        BigInteger[][] k_i = RandomUtils.generateRandomBigInteger2DArrayOfLengths(amountOfBaseOTs, 2, amountOfBaseOTs);
        baseOTsSender.execute(k_i, amountOfBaseOTs, inputStream, outputStream);

        BigInteger r = null;
        boolean[] choices = new boolean[amountOfChoices];
        BigInteger[] t_i = new BigInteger[amountOfBaseOTs];
        for (int i = 0; i < amountOfBaseOTs; i ++) {
            t_i[i] = G(k_i[i][0], amountOfChoices);
            BigInteger G_k_1 = G(k_i[i][1], amountOfChoices);
            if (i == 0) {
                r = t_i[i].xor(G_k_1);
                choices = ArrayUtils.convertFromBigInteger(r, amountOfChoices);
                continue;
            }

            BigInteger u_i = t_i[i].xor(G_k_1).xor(r);
            StreamUtils.writeBigIntegerToOutputStream(u_i, outputStream);
            outputStream.flush();
        }
        BigIntegerMatrix T = new BigIntegerMatrix(t_i, amountOfChoices);
        BigIntegerMatrix T_transposed = T.transpose();
        BigInteger[] t_j = T_transposed.getColumns();

        BigInteger[] returnValue = new BigInteger[amountOfChoices];
        for (int i = 0; i < returnValue.length; i ++) {
            BigInteger y_0 = StreamUtils.readBigIntegerFromInputStream(inputStream);
            BigInteger y_1 = StreamUtils.readBigIntegerFromInputStream(inputStream);
            returnValue[i] = (choices[i]? y_1 : y_0).xor(H(i, t_j[i], bitLength));
        }
        return Pair.of(choices, returnValue);
    }

    static BigInteger G(BigInteger element, int bitLength) {
        return KDFUtils.KDF(element, bitLength);
    }

    static BigInteger H(int j, BigInteger q_j, int bitLength) {
        return KDFUtils.KDF(q_j.xor(BigInteger.valueOf(j)), bitLength);
    }
}
