package com.github.pvriel.oblivioustransfer4j.srot.semihonest.asharov17;

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

import static com.github.pvriel.oblivioustransfer4j.srot.semihonest.asharov17.ASHAROV17SenderRandomObliviousTransferSender.G;
import static com.github.pvriel.oblivioustransfer4j.srot.semihonest.asharov17.ASHAROV17SenderRandomObliviousTransferSender.H;

/**
 * Class representing an implementation of the receiver part from the SR-OT protocol of <a href=https://eprint.iacr.org/2016/602>ASHAROV17</a>.
 */
public class ASHAROV17SenderRandomObliviousTransferReceiver extends ObliviousTransferExtensionReceiver {

    /**
     * Constructor for the {@link ASHAROV17SenderRandomObliviousTransferReceiver} class.
     * @param   amountOfBaseOTs
     *          The amount of base OTs to use.
     *          <br>Should be strictly positive, and should match the value used by the other party.
     * @param   baseOTsSender
     *          The not-null {@link ObliviousTransferSender} instance to use for the base OTs.
     */
    public ASHAROV17SenderRandomObliviousTransferReceiver(int amountOfBaseOTs, ObliviousTransferSender baseOTsSender) {
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
        BigInteger[] G_k_i_1 = new BigInteger[k_i.length];
        BigInteger[] u_i = new BigInteger[k_i.length];
        for (int i = 0; i < t_i.length; i ++) {
            t_i[i] = G(k_i[i][0], choices.length);
            G_k_i_1[i] = G(k_i[i][1], choices.length);
            u_i[i] = t_i[i].xor(G_k_i_1[i]).xor(r);
            StreamUtils.writeBigIntegerToOutputStream(u_i[i], outputStream);
            outputStream.flush();
        }
        BigIntegerMatrix T = new BigIntegerMatrix(t_i, choices.length);
        BigIntegerMatrix T_transposed = T.transpose();
        BigInteger[] t_j = T_transposed.getColumns();

        BigInteger[] x = new BigInteger[choices.length];
        for (int j = 0; j < x.length; j ++) x[j] = H(j, t_j[j], bitLength);
        return x;
    }
}
