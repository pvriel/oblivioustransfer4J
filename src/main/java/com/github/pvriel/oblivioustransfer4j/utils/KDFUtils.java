package com.github.pvriel.oblivioustransfer4j.utils;

import org.apache.commons.codec.digest.DigestUtils;

import java.math.BigInteger;

/**
 * Abstract class, defining some generic, KDF-related methods which are used by (different) OT protocols.
 */
public abstract class KDFUtils {


    /**
     * Method to apply a KDF on the provided element and return the result.
     * <br>The underlying implementation keeps applying SHA3-512 to the provided element, until enough bytes are generated (as temporary results)
     * to fill a byte array with (int) Math.ceil(((double) minBitLength) / 8.0) bytes in total.
     * <br>To make sure the return value has a minimum bit length of the 'minBitLength' argument, the bit at index 'minBitLength - 1' is always set to 1.
     * @param   element
     *          The element to apply the KDF with.
     * @param   minBitLength
     *          The minimum amount of bits the result BigInteger should consist out of.
     *          <br>In practise, the amount of bits of the result BigInteger is within the range of [minBitLength, (int) Math.ceil(((double) minBitLength) / 8.0)].
     * @return  The result of applying the KDF to the 'element' argument.
     */
    public static BigInteger KDF(BigInteger element, int minBitLength) {
        byte[] kdf = new byte[(int) Math.ceil(((double) minBitLength) / 8.0)];
        byte[] digest = DigestUtils.sha3_512(element.toByteArray());
        int index = 0;
        do {
            int amountToCopy = Math.min(digest.length, kdf.length - index);
            System.arraycopy(digest, 0, kdf, index, amountToCopy);
            index += amountToCopy;
        } while (index < kdf.length - 1 && (digest = DigestUtils.sha3_512(digest)) != null);

        BigInteger result = new BigInteger(kdf);
        result = result.setBit(minBitLength - 1);
        return result;
    }
}
