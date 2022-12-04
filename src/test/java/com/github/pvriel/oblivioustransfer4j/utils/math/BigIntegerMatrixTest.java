package com.github.pvriel.oblivioustransfer4j.utils.math;

import org.junit.jupiter.api.Test;

import java.math.BigInteger;

import static org.junit.jupiter.api.Assertions.*;

class BigIntegerMatrixTest {

    @Test
    void transposeTestOne() {
        BigInteger columnOne = BigInteger.TWO;
        BigInteger columnTwo = BigInteger.ONE;
        BigIntegerMatrix matrix = new BigIntegerMatrix(new BigInteger[]{columnOne, columnTwo}, 2);
        /**
         * 1 0   ==>  The same thing!
         * 0 1
         */
        BigIntegerMatrix transposed = matrix.transpose();
        assertEquals(transposed, matrix);
    }

    @Test
    void transposeTestTwo() {
        BigInteger columOne = BigInteger.valueOf(1);
        BigInteger columnTwo = BigInteger.valueOf(2);
        BigInteger columnThree = BigInteger.valueOf(3);
        BigIntegerMatrix matrix = new BigIntegerMatrix(new BigInteger[]{columOne, columnTwo, columnThree}, 2);
        /*
        0   1   1   ==> 0   1
        1   0   1       1   0
                        1   1
         */
        BigIntegerMatrix transposed = matrix.transpose();
        BigInteger columnFour = BigInteger.valueOf(3);
        BigInteger columnFive = BigInteger.valueOf(5);
        BigIntegerMatrix transposedEquivalent = new BigIntegerMatrix(new BigInteger[]{columnFour, columnFive}, 3);
        assertEquals(transposedEquivalent, transposed);
    }

    @Test
    void BigIntegerMatrixConstructorTest() {
        BigInteger columOne = BigInteger.valueOf(1);
        BigInteger columnTwo = BigInteger.valueOf(2);
        BigInteger columnThree = BigInteger.valueOf(3);
        BigIntegerMatrix rowMatrix = new BigIntegerMatrix(3, new BigInteger[]{columOne, columnTwo, columnThree});

        BigInteger columnFour = BigInteger.ZERO;
        BigInteger columnFive = BigInteger.valueOf(3);
        BigInteger columnSix = BigInteger.valueOf(5);
        BigIntegerMatrix columnMatrix = new BigIntegerMatrix(new BigInteger[]{columnFour, columnFive, columnSix}, 3);

        assertEquals(columnMatrix, rowMatrix);
    }
}