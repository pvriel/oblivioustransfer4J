package com.github.pvriel.oblivioustransfer4j.rrot.semihonest.asharov17;

import com.github.pvriel.mpcutils4j.array.ArrayUtils;
import com.github.pvriel.mpcutils4j.math.BigIntegerMatrix;
import com.github.pvriel.mpcutils4j.random.RandomUtils;
import com.github.pvriel.mpcutils4j.stream.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.stream.IntStream;

import static com.github.pvriel.oblivioustransfer4j.rrot.semihonest.asharov17.ASHAROV17ReceiverRandomObliviousTransferReceiver.G;
import static com.github.pvriel.oblivioustransfer4j.rrot.semihonest.asharov17.ASHAROV17ReceiverRandomObliviousTransferReceiver.H;

/**
 * Class representing an implementation of the sender part from the RR-OT protocol of <a href=https://eprint.iacr.org/2016/602>ASHAROV17</a>.
 */
public class ASHAROV17ReceiverRandomObliviousTransferSender extends ObliviousTransferExtensionSender {

    /**
     * Constructor for the {@link ASHAROV17ReceiverRandomObliviousTransferSender} class.
     * @param   amountOfBaseOTs
     *          The amount of base OTs to use.
     *          <br>Should be strictly positive, and should match the value used by the other party.
     * @param   baseOTsReceiver
     *          The not-null {@link ObliviousTransferReceiver} instance to use for the base OTs.
     */
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
        BigInteger[] u_i = new BigInteger[q_i.length - 1];
        for (int i = 0; i < u_i.length; i ++) {
            u_i[i] = StreamUtils.readBigIntegerFromInputStream(inputStream);
        }
        IntStream.range(1, q_i.length).parallel().forEach(i -> {
            BigInteger G_k_i = G(k_i_s[i], x.length);
            q_i[i] = s[i]? u_i[i - 1].xor(G_k_i) : G_k_i;
        });
        BigIntegerMatrix Q = new BigIntegerMatrix(q_i, x.length);
        BigIntegerMatrix Q_transposed = Q.transpose();
        BigInteger[] q_j = Q_transposed.getColumns();

        BigInteger[] y_0 = new BigInteger[x.length];
        BigInteger[] y_1 = new BigInteger[x.length];
        IntStream.range(0, x.length).parallel().forEach(i -> {
            y_0[i] = x[i][0].xor(H(i, q_j[i], bitLength));
            y_1[i] = x[i][1].xor(H(i, q_j[i].xor(sAsBigInteger), bitLength));
        });
        for (int i = 0; i < x.length; i ++) {
            StreamUtils.writeBigIntegerToOutputStream(y_0[i], outputStream);
            StreamUtils.writeBigIntegerToOutputStream(y_1[i], outputStream);
        }
        outputStream.flush();
    }
}
