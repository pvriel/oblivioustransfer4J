package com.github.pvriel.oblivioustransfer4j.rot.semihonest.asharov17;

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


public class ASHAROV17RandomObliviousTransferSender implements RandomObliviousTransferSender {

    private final int amountOfBaseOTs;
    private final ObliviousTransferReceiver baseOTsReceiver;

    public ASHAROV17RandomObliviousTransferSender(int amountOfBaseOTs, ObliviousTransferReceiver baseOTsReceiver) {
        this.amountOfBaseOTs = amountOfBaseOTs;
        this.baseOTsReceiver = baseOTsReceiver;
    }

    @Override
    public BigInteger[][] execute(int amountOfChoices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        boolean[] s = RandomUtils.generateRandomBooleanArrayOfLength(amountOfBaseOTs);
        BigInteger[] k_i_s = baseOTsReceiver.execute(s, amountOfBaseOTs, inputStream, outputStream);
        BigInteger[] t_i = new BigInteger[k_i_s.length];
        for (int i = 0; i < t_i.length; i ++) t_i[i] = G(k_i_s[i], amountOfChoices);
        BigInteger sAsBigInteger = ArrayUtils.convertToBigInteger(s);

        BigInteger[] q_i = new BigInteger[amountOfBaseOTs];
        q_i[0] = G(k_i_s[0], amountOfChoices);
        for (int i = 1; i < q_i.length; i ++) {
            BigInteger G_k_i = G(k_i_s[i], amountOfChoices);
            BigInteger u_i = StreamUtils.readBigIntegerFromInputStream(inputStream);
            q_i[i] = s[i]? u_i.xor(G_k_i) : G_k_i;
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
