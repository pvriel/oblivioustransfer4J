package com.github.pvriel.oblivioustransfer4j.alsz13;

import com.esotericsoftware.kryo.kryo5.Kryo;
import com.esotericsoftware.kryo.kryo5.io.Input;
import com.esotericsoftware.kryo.kryo5.io.Output;
import com.github.pvriel.oblivioustransfer4j.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.utils.KDFUtils;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Class representing an implementation of the <a href="https://doi.org/10.1145/2508859.2516738">ALSZ13</a> {@link ObliviousTransferReceiver}.
 */
public class ALSZ13ObliviousTransferReceiver implements ObliviousTransferReceiver {

    protected static Kryo kryo = new Kryo();
    protected static Random random = new SecureRandom();

    private final BigInteger g;
    private final BigInteger q;
    private final BigInteger p;
    private final int bitLength;

    static {
        kryo.register(BigInteger.class);
    }

    /**
     * Constructor for the {@link ALSZ13ObliviousTransferReceiver} class.
     * @param   p
     *          The p value, which is used for modulo operations on the h_i, u and k_i values.
     * @param   q
     *          The q value of the group (G, q, g), which should be DDH hard.
     * @param   g
     *          The g value of the group (G, q, g), which should be DDH hard.
     * @param   bitLength
     *          The bit length of the values that the receiver will receiver after executing the OT protocol.
     */
    public ALSZ13ObliviousTransferReceiver(BigInteger p, BigInteger q, BigInteger g, int bitLength) {
        this.p = p;
        this.g = g;
        this.q = q;
        this.bitLength = bitLength;
    }

    @Override
    public BigInteger[] execute(boolean[] choices, InputStream inputStream, OutputStream outputStream) {
        Input input = new Input(inputStream);
        Output output = new Output(outputStream);

        // First round.
        BigInteger[] alpha_i = new BigInteger[choices.length];
        for (int i = 0; i < choices.length; i ++) {
            alpha_i[i] = new BigInteger(q.bitLength(), random).mod(q);
            BigInteger g_pow_alpha_i = g.modPow(alpha_i[i], p);
            BigInteger h_i = new BigInteger(p.bitLength(), random).mod(p);

            if (!choices[i]) kryo.writeObject(output, g_pow_alpha_i);
            kryo.writeObject(output, h_i);
            if (choices[i]) kryo.writeObject(output, g_pow_alpha_i);
            output.flush();
        }

        // Second and third round.
        BigInteger u = kryo.readObject(input, BigInteger.class);
        BigInteger[] x = new BigInteger[choices.length];
        for (int i = 0; i < choices.length; i ++) {
            BigInteger k_i_delta_i = u.modPow(alpha_i[i], p);
            BigInteger kdf = KDFUtils.KDF(k_i_delta_i, bitLength);

            BigInteger chosen_v_i;
            if (choices[i]) kryo.readObject(input, BigInteger.class);
            chosen_v_i = kryo.readObject(input, BigInteger.class);
            if (!choices[i]) kryo.readObject(input, BigInteger.class);

            x[i] = chosen_v_i.xor(kdf);
        }

        return x;
    }
}
