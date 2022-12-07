package com.github.pvriel.oblivioustransfer4j.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;

/**
 * Abstract class, defining some generic, KDF-related methods which are used by (different) OT protocols.
 */
public abstract class KDFUtils {


    /**
     * Method to apply a KDF on the provided element and return the result.
     * @param   element
     *          The element to apply the KDF with.
     * @param   bitLength
     *          The exact amount of bits the result BigInteger should consist out of.
     * @return  The result of applying the KDF to the 'element' argument.
     */
    public static BigInteger KDF(BigInteger element, int bitLength) {
        byte[] kdf = new byte[(int) Math.ceil(((double) bitLength) / 8.0)];
        byte[] digest = DigestUtils.sha3_512(element.toByteArray());
        int index = 0;
        do {
            int amountToCopy = Math.min(digest.length, kdf.length - index);
            System.arraycopy(digest, 0, kdf, index, amountToCopy);
            index += amountToCopy;
        } while (index < kdf.length - 1 && (digest = DigestUtils.sha3_512(digest)) != null);

        BigInteger result = new BigInteger(1, kdf);
        for (int i = bitLength; i < result.bitLength(); i ++) {
            result = result.clearBit(i);
        }
        if (result.bitLength() != bitLength) result = result.setBit(bitLength - 1);
        return result;
    }
}
