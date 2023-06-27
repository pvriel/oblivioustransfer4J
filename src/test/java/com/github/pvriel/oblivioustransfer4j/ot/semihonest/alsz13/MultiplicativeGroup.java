package com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13;

import java.math.BigInteger;
import java.security.SecureRandom;

/**
 * Class to represent some basic operations on 𝐙∗𝑝 groups.
 */
public class MultiplicativeGroup {

    
    private final static SecureRandom random = new SecureRandom();

    
    private final BigInteger p;

    public MultiplicativeGroup( BigInteger p) {
        this.p = p;
    }

    
    public BigInteger getElementOfOrder( BigInteger q) {
        return getElementOfOrder(q, Integer.MAX_VALUE);
    }

    
    public BigInteger getElementOfOrder( BigInteger q, int maxTries) throws IllegalArgumentException {
        // https://crypto.stackexchange.com/questions/33471/how-to-find-element-g-order-q-given-2-large-primes-p-and-q-where-qp-1
        // https://www.di-mgt.com.au/multiplicative-group-mod-p.html
        BigInteger pMinusOne = p.subtract(BigInteger.ONE);
        BigInteger pMinusOneDividedByQ = pMinusOne.divide(q);
        BigInteger range = pMinusOne.subtract(BigInteger.ONE);
        int len = pMinusOne.bitLength();

        BigInteger a, g;
        for (int i = 0; i < maxTries; i ++) {
            a = new BigInteger(len, random);
            if (a.compareTo(BigInteger.ONE) < 0) a = a.add(BigInteger.ONE);
            if (a.compareTo(range) >= 0) a = a.mod(range).add(BigInteger.ONE);

            g = a.modPow(pMinusOneDividedByQ, p);

            if (!g.mod(p).equals(BigInteger.ONE)) return g;
        }

        throw new IllegalArgumentException(String.format("Could not find an element of order (%s) after %d tries for" +
                " multiplicative group (%s).", q, maxTries, this));
    }
}
