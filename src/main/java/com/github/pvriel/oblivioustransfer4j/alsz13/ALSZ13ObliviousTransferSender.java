package com.github.pvriel.oblivioustransfer4j.alsz13;

import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.github.pvriel.oblivioustransfer4j.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.utils.KDFUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

import static com.github.pvriel.oblivioustransfer4j.alsz13.ALSZ13ObliviousTransferReceiver.kryo;
import static com.github.pvriel.oblivioustransfer4j.alsz13.ALSZ13ObliviousTransferReceiver.random;

/**
 * Class representing an implementation of the <a href="https://doi.org/10.1145/2508859.2516738">ALSZ13</a> {@link ObliviousTransferSender}.
 */
public class ALSZ13ObliviousTransferSender implements ObliviousTransferSender {

    private final BigInteger q;
    private final BigInteger g;
    private final BigInteger p;
    private final int bitLength;


    /**
     * Constructor for the {@link ALSZ13ObliviousTransferSender} class.
     * @param   p
     *          The p value, which is used for modulo operations on the h_i, u and k_i values.
     * @param   q
     *          The q value of the group (G, q, g), which should be DDH hard.
     * @param   g
     *          The g value of the group (G, q, g), which should be DDH hard.
     * @param   bitLength
     *          The bit length of the values that the receiver will receiver after executing the OT protocol.
     */
    public ALSZ13ObliviousTransferSender(BigInteger p, BigInteger q, BigInteger g, int bitLength) {
        this.p = p;
        this.q = q;
        this.g = g;
        this.bitLength = bitLength;
    }

    @Override
    public void execute(BigInteger[][] x, InputStream inputStream, OutputStream outputStream) {
        Input input = new Input(inputStream);
        Output output = new Output(outputStream);

        // First part second round.
        BigInteger r = new BigInteger(q.bitLength(), random).mod(q);
        BigInteger u = g.modPow(r, p);
        kryo.writeObject(output, u);
        output.flush();

        // First round + second part second round.
        for (int i = 0; i < x.length; i ++) {
            BigInteger hi_0 = kryo.readObject(input, BigInteger.class);
            BigInteger hi_1 = kryo.readObject(input, BigInteger.class);
            BigInteger ki_0 = hi_0.modPow(r, p);
            BigInteger ki_1 = hi_1.modPow(r, p);
            BigInteger kdf_0 = KDFUtils.KDF(ki_0, bitLength);
            BigInteger kdf_1 = KDFUtils.KDF(ki_1, bitLength);
            BigInteger vi_0 = x[i][0].xor(kdf_0);
            BigInteger vi_1 = x[i][1].xor(kdf_1);

            kryo.writeObject(output, vi_0);
            kryo.writeObject(output, vi_1);
            output.flush();
        }
    }
}
