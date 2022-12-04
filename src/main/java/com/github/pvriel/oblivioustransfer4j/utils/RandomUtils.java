package com.github.pvriel.oblivioustransfer4j.utils;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Random;

public abstract class RandomUtils {

    private final static Random random = new SecureRandom();

    public static boolean[] generateRandomBooleanArrayOfLength(int length) {
        boolean[] returnValue = new boolean[length];
        for (int i = 0; i < length; i++) returnValue[i] = random.nextBoolean();
        return returnValue;
    }

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
