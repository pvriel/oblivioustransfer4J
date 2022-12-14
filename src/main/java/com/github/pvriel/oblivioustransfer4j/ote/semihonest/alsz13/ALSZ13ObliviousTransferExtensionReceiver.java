package com.github.pvriel.oblivioustransfer4j.ote.semihonest.alsz13;

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

import static com.github.pvriel.oblivioustransfer4j.ote.semihonest.alsz13.ALSZ13ObliviousTransferExtensionSender.G;
import static com.github.pvriel.oblivioustransfer4j.ote.semihonest.alsz13.ALSZ13ObliviousTransferExtensionSender.H;

/**
 * Class representing an implementation of the semi-honest <a href="https://doi.org/10.1145/2508859.2516738">ALSZ13</a> {@link ObliviousTransferExtensionReceiver}.
 */
public class ALSZ13ObliviousTransferExtensionReceiver extends ObliviousTransferExtensionReceiver {

    /**
     * Constructor for the {@link ALSZ13ObliviousTransferExtensionReceiver} class.
     * @param   amountOfBaseOTs
     *          The amount of base OTs to use.
     *          <br>This should be a value of at least one.
     * @param   baseOTsSender
     *          The (not-null) {@link ObliviousTransferSender} to use for the base OTs.
     */
    public ALSZ13ObliviousTransferExtensionReceiver(int amountOfBaseOTs, ObliviousTransferSender baseOTsSender) {
        super(amountOfBaseOTs, baseOTsSender);
    }

    @Override
    public BigInteger[] execute(boolean[] choices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        // Initial OT phase.
        BigInteger[][] k_i = RandomUtils.generateRandomBigInteger2DArrayOfLengths(getAmountOfBaseOTs(), 2, getAmountOfBaseOTs());
        getBaseOTsSender().execute(k_i, getAmountOfBaseOTs(), inputStream, outputStream);

        // OT extension phase.
        BigInteger r = ArrayUtils.convertToBigInteger(choices);
        BigInteger[] t_i = new BigInteger[k_i.length];
        for (int i = 0; i < t_i.length; i ++) {
            t_i[i] = G(k_i[i][0], choices.length);
            BigInteger G_k_i_1 = G(k_i[i][1], choices.length);
            BigInteger u_i = t_i[i].xor(G_k_i_1).xor(r);
            StreamUtils.writeBigIntegerToOutputStream(u_i, outputStream);
            outputStream.flush();
        }
        BigIntegerMatrix T = new BigIntegerMatrix(t_i, choices.length);
        BigIntegerMatrix T_transposed = T.transpose();
        BigInteger[] t_j = T_transposed.getColumns();

        BigInteger[] x = new BigInteger[choices.length];
        for (int j = 0; j < x.length; j ++) {
            BigInteger y_j_0 = StreamUtils.readBigIntegerFromInputStream( inputStream);
            BigInteger y_j_1 = StreamUtils.readBigIntegerFromInputStream( inputStream);
            x[j] = (choices[j]? y_j_1 : y_j_0).xor(H(j, t_j[j], bitLength));
        }

        return x;
    }
}
