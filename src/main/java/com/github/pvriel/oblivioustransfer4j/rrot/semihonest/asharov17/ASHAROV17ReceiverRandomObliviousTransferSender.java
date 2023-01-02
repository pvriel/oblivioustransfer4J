package com.github.pvriel.oblivioustransfer4j.rrot.semihonest.asharov17;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionSender;
import com.github.pvriel.oblivioustransfer4j.utils.ArrayUtils;
import com.github.pvriel.oblivioustransfer4j.utils.RandomUtils;
import com.github.pvriel.oblivioustransfer4j.utils.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.utils.math.BigIntegerMatrix;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import static com.github.pvriel.oblivioustransfer4j.rrot.semihonest.asharov17.ASHAROV17ReceiverRandomObliviousTransferReceiver.G;
import static com.github.pvriel.oblivioustransfer4j.rrot.semihonest.asharov17.ASHAROV17ReceiverRandomObliviousTransferReceiver.H;

public class ASHAROV17ReceiverRandomObliviousTransferSender extends ObliviousTransferExtensionSender {

    public ASHAROV17ReceiverRandomObliviousTransferSender(int amountOfBaseOTs, ObliviousTransferReceiver baseOTsReceiver) {
        super(amountOfBaseOTs, baseOTsReceiver);
    }

    @Override
    public void execute(BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        boolean[] s = RandomUtils.generateRandomBooleanArrayOfLength(getAmountOfBaseOTs());
        BigInteger[] k_i_s = getBaseOTsReceiver().execute(s, getAmountOfBaseOTs(), inputStream, outputStream);
        BigInteger sAsBigInteger = ArrayUtils.convertToBigInteger(s);

        BigInteger[] q_i = new BigInteger[getAmountOfBaseOTs()];
        q_i[0] = G(k_i_s[0], x.length);
        for (int i = 1; i < q_i.length; i ++) {
            BigInteger G_k_i = G(k_i_s[i], x.length);
            BigInteger u_i = StreamUtils.readBigIntegerFromInputStream(inputStream);
            q_i[i] = s[i]? u_i.xor(G_k_i) : G_k_i;
        }
        BigIntegerMatrix Q = new BigIntegerMatrix(q_i, x.length);
        BigIntegerMatrix Q_transposed = Q.transpose();
        BigInteger[] q_j = Q_transposed.getColumns();

        for (int i = 0; i < x.length; i ++) {
            BigInteger y_0 = x[i][0].xor(H(i, q_j[i], bitLength));
            BigInteger y_1 = x[i][1].xor(H(i, q_j[i].xor(sAsBigInteger), bitLength));
            StreamUtils.writeBigIntegerToOutputStream(y_0, outputStream);
            StreamUtils.writeBigIntegerToOutputStream(y_1, outputStream);
            outputStream.flush();
        }
    }
}
