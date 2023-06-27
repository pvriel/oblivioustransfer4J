package com.github.pvriel.oblivioustransfer4j.rot.semihonest.asharov17;

import com.github.pvriel.mpcutils4j.array.ArrayUtils;
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

import static com.github.pvriel.oblivioustransfer4j.rot.semihonest.asharov17.ASHAROV17RandomObliviousTransferSender.G;
import static com.github.pvriel.oblivioustransfer4j.rot.semihonest.asharov17.ASHAROV17RandomObliviousTransferSender.H;

/**
 * Class representing an implementation of the receiver part from the R-OT protocol of <a href=https://eprint.iacr.org/2016/602>ASHAROV17</a>.
 */
public class ASHAROV17RandomObliviousTransferReceiver implements RandomObliviousTransferReceiver {

    private final int amountOfBaseOTs;
    private final ObliviousTransferSender baseOTsSender;

    /**
     * Constructor for the {@link ASHAROV17RandomObliviousTransferReceiver} class.
     * @param   amountOfBaseOTs
     *          The amount of base OTs to use. Should be a value of at least one.
     * @param   baseOTsSender
     *          The (not-null) {@link ObliviousTransferSender} instance to use for the base OTs.
     */
    public ASHAROV17RandomObliviousTransferReceiver(int amountOfBaseOTs, ObliviousTransferSender baseOTsSender) {
        this.amountOfBaseOTs = amountOfBaseOTs;
        this.baseOTsSender = baseOTsSender;
    }

    @Override
    public Pair<boolean[], BigInteger[]> execute(int amountOfChoices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        BigInteger[][] k_i = RandomUtils.generateRandomBigInteger2DArrayOfLengths(amountOfBaseOTs, 2, amountOfBaseOTs);
        baseOTsSender.execute(k_i, amountOfBaseOTs, inputStream, outputStream);

        BigInteger[] t_i = new BigInteger[amountOfBaseOTs];
        t_i[0] = G(k_i[0][0], amountOfChoices);
        BigInteger G_k_1 = G(k_i[0][1], amountOfChoices);
        BigInteger r = t_i[0].xor(G_k_1);;
        boolean[] choices = ArrayUtils.convertFromBigInteger(r, amountOfChoices);
        BigInteger[] u_i = new BigInteger[amountOfBaseOTs - 1];
        IntStream.range(1, amountOfBaseOTs).parallel().forEach(i -> {
            t_i[i] = G(k_i[i][0], amountOfChoices);
            BigInteger G_k_1_temp = G(k_i[i][1], amountOfChoices);
            u_i[i - 1] = t_i[i].xor(G_k_1_temp).xor(r);
        });
        for (int i = 0; i < amountOfBaseOTs - 1; i ++) {
            StreamUtils.writeBigIntegerToOutputStream(u_i[i], outputStream);
            outputStream.flush();
        }

        BigIntegerMatrix T = new BigIntegerMatrix(t_i, amountOfChoices);
        BigIntegerMatrix T_transposed = T.transpose();
        BigInteger[] t_j = T_transposed.getColumns();

        BigInteger[] returnValue = new BigInteger[amountOfChoices];
        IntStream.range(0, returnValue.length).parallel().forEach(i -> returnValue[i] = H(i, t_j[i], bitLength));
        return Pair.of(choices, returnValue);
    }
}
