package com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13;

import com.github.pvriel.mpcutils4j.crypto.KDFUtils;
import com.github.pvriel.mpcutils4j.stream.StreamUtils;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.util.stream.IntStream;

import static com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferReceiver.random;

/**
 * Class representing an implementation of the semi-honest <a href="https://doi.org/10.1145/2508859.2516738">ALSZ13</a> {@link ObliviousTransferSender}.
 */
public class ALSZ13ObliviousTransferSender implements ObliviousTransferSender {

    private final BigInteger q;
    private final BigInteger g;
    private final BigInteger p;


    /**
     * Constructor for the {@link ALSZ13ObliviousTransferSender} class.
     * @param   p
     *          The p value, which is used for modulo operations on the h_i, u and k_i values.
     * @param   q
     *          The q value of the group (G, q, g), which should be DDH hard.
     * @param   g
     *          The g value of the group (G, q, g), which should be DDH hard.
     */
    public ALSZ13ObliviousTransferSender(BigInteger p, BigInteger q, BigInteger g) {
        this.p = p;
        this.q = q;
        this.g = g;
    }

    @Override
    public void execute(BigInteger[][] x, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException {
        // First part second round.
        BigInteger r = new BigInteger(q.bitLength(), random).mod(q);
        BigInteger u = g.modPow(r, p);
        StreamUtils.writeBigIntegerToOutputStream(u, outputStream);
        outputStream.flush();

        // First round + second part second round.
        BigInteger[] hi_0 = new BigInteger[x.length];
        BigInteger[] hi_1 = new BigInteger[x.length];
        BigInteger[] vi_0 = new BigInteger[x.length];
        BigInteger[] vi_1 = new BigInteger[x.length];
        for (int i = 0; i < x.length; i ++) {
            hi_0[i] = StreamUtils.readBigIntegerFromInputStream(inputStream);
            hi_1[i] = StreamUtils.readBigIntegerFromInputStream(inputStream);
        }
        IntStream.range(0, x.length).parallel().forEach(i -> {
            BigInteger ki_0 = hi_0[i].modPow(r, p);
            BigInteger ki_1 = hi_1[i].modPow(r, p);
            BigInteger kdf_0 = KDFUtils.KDF(ki_0, bitLength);
            BigInteger kdf_1 = KDFUtils.KDF(ki_1, bitLength);
            vi_0[i] = x[i][0].xor(kdf_0);
            vi_1[i] = x[i][1].xor(kdf_1);
        });

        for (int i = 0; i < x.length; i ++) {
            StreamUtils.writeBigIntegerToOutputStream(vi_0[i], outputStream);
            StreamUtils.writeBigIntegerToOutputStream(vi_1[i], outputStream);
        }
        outputStream.flush();
    }
}
