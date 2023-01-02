package com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.utils.KDFUtils;
import com.github.pvriel.oblivioustransfer4j.utils.StreamUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

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
        for (BigInteger[] bigIntegers : x) {
            BigInteger hi_0 = StreamUtils.readBigIntegerFromInputStream( inputStream);
            BigInteger hi_1 = StreamUtils.readBigIntegerFromInputStream( inputStream);
            BigInteger ki_0 = hi_0.modPow(r, p);
            BigInteger ki_1 = hi_1.modPow(r, p);
            BigInteger kdf_0 = KDFUtils.KDF(ki_0, bitLength);
            BigInteger kdf_1 = KDFUtils.KDF(ki_1, bitLength);
            BigInteger vi_0 = bigIntegers[0].xor(kdf_0);
            BigInteger vi_1 = bigIntegers[1].xor(kdf_1);

            StreamUtils.writeBigIntegerToOutputStream(vi_0, outputStream);
            StreamUtils.writeBigIntegerToOutputStream(vi_1, outputStream);
            outputStream.flush();
        }
    }
}
