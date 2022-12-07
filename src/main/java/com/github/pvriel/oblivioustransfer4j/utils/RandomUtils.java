package com.github.pvriel.oblivioustransfer4j.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

/**
 * Abstract class, containing some generic methods to work with {@link SecureRandom}.
 */
public abstract class RandomUtils {

    private final static Random random = new SecureRandom();

    /**
     * Method to generate a random boolean array with a specific length.
     * @param   length
     *          The length of the return value.
     * @return  A random boolean array of length 'length'.
     */
    public static boolean[] generateRandomBooleanArrayOfLength(int length) {
        boolean[] returnValue = new boolean[length];
        for (int i = 0; i < length; i++) returnValue[i] = random.nextBoolean();
        return returnValue;
    }

    /**
     * Method to generate a random 2D {@link BigInteger} array, with predefined lengths.
     * @param   length0
     *          The length of the array.
     * @param   length1
     *          The lengths of the sub arrays.
     * @param   bitLength
     *          The bit length of the resulting {@link BigInteger} instances.
     * @return  The random 2D {@link BigInteger} array.
     */
    public static BigInteger[][] generateRandomBigInteger2DArrayOfLengths(int length0, int length1, int bitLength) {
        BigInteger[][] returnValue = new BigInteger[length0][length1];
        for (int i = 0; i < length0; i++) {
            for (int j = 0; j < length1; j++) {
                returnValue[i][j] = new BigInteger(bitLength, random);
            }
        }
        return returnValue;
    }
}
