package com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.utils.KDFUtils;
import com.github.pvriel.oblivioustransfer4j.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Class representing an implementation of the semi-honest <a href="https://doi.org/10.1145/2508859.2516738">ALSZ13</a> {@link ObliviousTransferReceiver}.
 */
public class ALSZ13ObliviousTransferReceiver implements ObliviousTransferReceiver {

    static Random random = new SecureRandom();

    private final BigInteger g;
    private final BigInteger q;
    private final BigInteger p;

    /**
     * Constructor for the {@link ALSZ13ObliviousTransferReceiver} class.
     * @param   p
     *          The p value, which is used for modulo operations on the h_i, u and k_i values.
     * @param   q
     *          The q value of the group (G, q, g), which should be DDH hard.
     * @param   g
     *          The g value of the group (G, q, g), which should be DDH hard.
     */
    public ALSZ13ObliviousTransferReceiver(BigInteger p, BigInteger q, BigInteger g) {
        this.p = p;
        this.g = g;
        this.q = q;
    }

    @Override
    public BigInteger[] execute(boolean[] choices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        // First round.
        BigInteger[] alpha_i = new BigInteger[choices.length];
        for (int i = 0; i < choices.length; i ++) {
            alpha_i[i] = new BigInteger(q.bitLength(), random).mod(q);
            BigInteger g_pow_alpha_i = g.modPow(alpha_i[i], p);
            BigInteger h_i = new BigInteger(p.bitLength(), random).mod(p);

            if (!choices[i]) StreamUtils.writeToOutputStream(g_pow_alpha_i, outputStream);
            StreamUtils.writeToOutputStream(h_i, outputStream);
            if (choices[i]) StreamUtils.writeToOutputStream(g_pow_alpha_i, outputStream);
            outputStream.flush();
        }

        // Second and third round.
        BigInteger u = StreamUtils.readFromInputStream(BigInteger.class, inputStream);
        BigInteger[] x = new BigInteger[choices.length];
        for (int i = 0; i < choices.length; i ++) {
            BigInteger k_i_delta_i = u.modPow(alpha_i[i], p);
            BigInteger kdf = KDFUtils.KDF(k_i_delta_i, bitLength);

            BigInteger chosen_v_i;
            if (choices[i]) StreamUtils.readFromInputStream(BigInteger.class, inputStream);
            chosen_v_i = StreamUtils.readFromInputStream(BigInteger.class, inputStream);
            if (!choices[i]) StreamUtils.readFromInputStream(BigInteger.class, inputStream);

            x[i] = chosen_v_i.xor(kdf);
        }

        return x;
    }
}
