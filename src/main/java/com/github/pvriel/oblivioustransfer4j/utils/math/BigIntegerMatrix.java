package com.github.pvriel.oblivioustransfer4j.utils.math;

import com.github.pvriel.oblivioustransfer4j.utils.ArrayUtils;

import java.math.BigInteger;
import java.util.Arrays;

public class BigIntegerMatrix {

    private final BigInteger[] columns;
    private int amountOfRows;

    public BigIntegerMatrix(BigInteger[] columns, int amountOfRows) {
        this.columns = columns;
        this.amountOfRows = amountOfRows;
    }

    public BigIntegerMatrix(int amountOfColumns, BigInteger[] rows) {
        // TODO: test this.
        this(new BigIntegerMatrix(rows, amountOfColumns).transpose().columns, rows.length);
    }

    public BigIntegerMatrix transpose() {
        char[][] currentColumnsAsChars = convertToCharArrays();
        char[][] newColumnsAsChars = ArrayUtils.transpose(currentColumnsAsChars);

        BigInteger[] newColumns = new BigInteger[amountOfRows];

        for (int i = 0; i < amountOfRows; i ++) {
            newColumns[i] = new BigInteger(new String(newColumnsAsChars[i]), 2);
        }
        return new BigIntegerMatrix(newColumns, columns.length);
    }

    public char[][] convertToCharArrays() {
        char[][] currentColumnsAsChars = new char[columns.length][amountOfRows];
        for (int i = 0; i < columns.length; i ++) {
            char[] tempBigIntegerAsCharArray = columns[i].toString(2).toCharArray();
            int startIndex = Math.max(0, amountOfRows - tempBigIntegerAsCharArray.length);
            int length = Math.min(amountOfRows, tempBigIntegerAsCharArray.length);
            System.arraycopy(tempBigIntegerAsCharArray, 0, currentColumnsAsChars[i], startIndex, length);
            for (int j = 0; j < startIndex; j ++) currentColumnsAsChars[i][j] = '0';
        }
        return currentColumnsAsChars;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof BigIntegerMatrix)) return false;
        return amountOfRows == ((BigIntegerMatrix) obj).amountOfRows &&
                Arrays.equals(columns, ((BigIntegerMatrix) obj).columns);
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        char[][] charArray = convertToCharArrays();
        for (int i = 0; i < charArray[0].length; i ++) {
            for (int j = 0; j < charArray.length; j ++) {
                stringBuilder.append(charArray[j][i]).append("\t");
            }
            stringBuilder.append(System.lineSeparator());
        }
        return stringBuilder.toString();
    }

    public BigInteger[] getColumns() {
        return columns;
    }
}
