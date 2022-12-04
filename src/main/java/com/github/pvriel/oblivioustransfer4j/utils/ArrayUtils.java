package com.github.pvriel.oblivioustransfer4j.utils;

import java.math.BigInteger;

public abstract class ArrayUtils {

    public static char[][] transpose(char[][] original) {
        char[][] returnValue = new char[original[0].length][original.length];
        for (int i = 0; i < returnValue.length; i ++) {
            for (int j = 0; j < returnValue[0].length; j ++) {
                returnValue[i][j] = original[j][i];
            }
        }
        return returnValue;
    }

    public static BigInteger convertToBigInteger(boolean[] choices) {
        BigInteger returnValue = BigInteger.ZERO;
        for (int i = 0; i < choices.length; i ++) if (choices[choices.length - 1 - i]) returnValue = returnValue.setBit(i);
        return returnValue;
    }
}
