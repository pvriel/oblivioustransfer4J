package com.github.pvriel.oblivioustransfer4j.ote.alsz13;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionReceiver;
import com.github.pvriel.oblivioustransfer4j.utils.ArrayUtils;
import com.github.pvriel.oblivioustransfer4j.utils.RandomUtils;
import com.github.pvriel.oblivioustransfer4j.utils.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.utils.math.BigIntegerMatrix;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import static com.github.pvriel.oblivioustransfer4j.ote.alsz13.ALSZ13ObliviousTransferExtensionSender.G;
import static com.github.pvriel.oblivioustransfer4j.ote.alsz13.ALSZ13ObliviousTransferExtensionSender.H;

public class ALSZ13ObliviousTransferExtensionReceiver implements ObliviousTransferExtensionReceiver {

    @Override
    public BigInteger[] execute(boolean[] choices, int bitLength, int amountOfBaseOTs, ObliviousTransferSender baseOTsSender, InputStream inputStream, OutputStream outputStream) throws IOException {
        // Initial OT phase.
        BigInteger[][] k_i = RandomUtils.generateRandomBigInteger2DArrayOfLengths(amountOfBaseOTs, 2, amountOfBaseOTs);
        baseOTsSender.execute(k_i, amountOfBaseOTs, inputStream, outputStream);

        // OT extension phase.
        BigInteger r = ArrayUtils.convertToBigInteger(choices);
        BigInteger[] t_i = new BigInteger[k_i.length];
        BigInteger[] G_k_i_1 = new BigInteger[k_i.length];
        BigInteger[] u_i = new BigInteger[k_i.length];
        for (int i = 0; i < t_i.length; i ++) {
            t_i[i] = G(k_i[i][0], choices.length);
            G_k_i_1[i] = G(k_i[i][1], choices.length);
            u_i[i] = t_i[i].xor(G_k_i_1[i]).xor(r);
            StreamUtils.writeToOutputStream(u_i[i], outputStream);
            outputStream.flush();
        }
        BigIntegerMatrix T = new BigIntegerMatrix(t_i, choices.length);
        BigIntegerMatrix T_transposed = T.transpose();
        BigInteger[] t_j = T_transposed.getColumns();

        BigInteger[] x = new BigInteger[choices.length];
        for (int j = 0; j < x.length; j ++) {
            BigInteger y_j_0 = StreamUtils.readFromInputStream(BigInteger.class, inputStream);
            BigInteger y_j_1 = StreamUtils.readFromInputStream(BigInteger.class, inputStream);
            x[j] = (choices[j]? y_j_1 : y_j_0).xor(H(j, t_j[j], bitLength));
        }

        return x;
    }
}
