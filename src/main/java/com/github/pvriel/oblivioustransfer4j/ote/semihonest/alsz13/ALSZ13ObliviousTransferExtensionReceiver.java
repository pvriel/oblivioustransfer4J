package com.github.pvriel.oblivioustransfer4j.ote.semihonest.alsz13;

import com.github.pvriel.mpcutils4j.array.ArrayUtils;
import com.github.pvriel.mpcutils4j.math.BigIntegerMatrix;
import com.github.pvriel.mpcutils4j.random.RandomUtils;
import com.github.pvriel.mpcutils4j.stream.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.concurrent.ForkJoinPool;
import java.util.concurrent.ForkJoinTask;
import java.util.stream.IntStream;

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
        BigInteger[] u_i = new BigInteger[k_i.length];
        IntStream.range(0, t_i.length).parallel().forEach(i -> {
            t_i[i] = G(k_i[i][0], choices.length);
            BigInteger G_k_i_1 = G(k_i[i][1], choices.length);
            u_i[i] = t_i[i].xor(G_k_i_1).xor(r);
        });

        // For the next step.
        ForkJoinTask<BigInteger[]> t_jTask = ForkJoinPool.commonPool().submit(() -> {
            BigIntegerMatrix T = new BigIntegerMatrix(t_i, choices.length);
            BigIntegerMatrix T_transposed = T.transpose();
            return T_transposed.getColumns();
        });

        for (int i = 0; i < t_i.length; i ++) StreamUtils.writeBigIntegerToOutputStream(u_i[i], outputStream);
        outputStream.flush();


        BigInteger[][] y_j = new BigInteger[choices.length][2];
        for (int j = 0; j < choices.length; j ++) {
            y_j[j][0] = StreamUtils.readBigIntegerFromInputStream( inputStream);
            y_j[j][1] = StreamUtils.readBigIntegerFromInputStream( inputStream);
        }
        BigInteger[] t_j = t_jTask.join();
        BigInteger[] x = new BigInteger[choices.length];
        IntStream.range(0, x.length).parallel().forEach(j -> {
            x[j] = (choices[j]? y_j[j][1] : y_j[j][0]).xor(H(j, t_j[j], bitLength));
        });

        return x;
    }
}
