package com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13;

import com.github.pvriel.mpcutils4j.crypto.KDFUtils;
import com.github.pvriel.mpcutils4j.stream.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;
import java.util.stream.IntStream;

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
        BigInteger[] g_pow_alpha_i = new BigInteger[choices.length];
        BigInteger[] h_i = new BigInteger[choices.length];
        IntStream.range(0, choices.length).parallel().forEach(i -> {
            alpha_i[i] = new BigInteger(q.bitLength(), random).mod(q);
            g_pow_alpha_i[i] = g.modPow(alpha_i[i], p);
            h_i[i] = new BigInteger(p.bitLength(), random).mod(p);
        });
        for (int i = 0; i < choices.length; i ++) {
            if (!choices[i]) StreamUtils.writeBigIntegerToOutputStream(g_pow_alpha_i[i], outputStream);
            StreamUtils.writeBigIntegerToOutputStream(h_i[i], outputStream);
            if (choices[i]) StreamUtils.writeBigIntegerToOutputStream(g_pow_alpha_i[i], outputStream);
        }
        outputStream.flush();

        // Second and third round.
        BigInteger u = StreamUtils.readBigIntegerFromInputStream(inputStream);
        BigInteger[] x = new BigInteger[choices.length];
        BigInteger[] k_i_delta_i = new BigInteger[choices.length];
        BigInteger[] kdf = new BigInteger[choices.length];
        IntStream.range(0, choices.length).parallel().forEach(i -> {
            k_i_delta_i[i] = u.modPow(alpha_i[i], p);
            kdf[i] = KDFUtils.KDF(k_i_delta_i[i], bitLength);
        });

        for (int i = 0; i < choices.length; i ++) {
            if (choices[i]) StreamUtils.readBigIntegerFromInputStream( inputStream);
            BigInteger chosen_v_i = StreamUtils.readBigIntegerFromInputStream( inputStream);
            if (!choices[i]) StreamUtils.readBigIntegerFromInputStream( inputStream);
            x[i] = chosen_v_i.xor(kdf[i]);
        }

        return x;
    }
}
