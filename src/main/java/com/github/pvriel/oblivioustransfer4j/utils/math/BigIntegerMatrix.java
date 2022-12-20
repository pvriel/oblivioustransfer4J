package com.github.pvriel.oblivioustransfer4j.utils.math;

import com.github.pvriel.oblivioustransfer4j.utils.ArrayUtils;

import java.math.BigInteger;
import java.util.Arrays;

/**
 * Class representing bit matrices, of which the columns are represented using {@link BigInteger} instances.
 */
public class BigIntegerMatrix {

    private final BigInteger[] columns;
    private final int amountOfRows;

    /**
     * Constructor for the {@link BigIntegerMatrix} class.
     * @param   columns
     *          The columns of the matrix.
     * @param   amountOfRows
     *          The amount of rows of the matrix.
     */
    public BigIntegerMatrix(BigInteger[] columns, int amountOfRows) {
        this.columns = columns;
        this.amountOfRows = amountOfRows;
    }

    /**
     * Constructor for the {@link BigIntegerMatrix} class.
     * <br>This constructor just calls this(new BigIntegerMatrix(rows, amountOfColumns).transpose().columns, rows.length).
     * @param   amountOfColumns
     *          The amount of columns of the matrix.
     * @param   rows
     *          The rows of the matrix.
     */
    public BigIntegerMatrix(int amountOfColumns, BigInteger[] rows) {
        this(new BigIntegerMatrix(rows, amountOfColumns).transpose().columns, rows.length);
    }

    /**
     * Method to transpose the matrix.
     * @return  The transposed matrix.
     */
    public BigIntegerMatrix transpose() {
        char[][] currentColumnsAsChars = convertToCharArrays();
        char[][] newColumnsAsChars = ArrayUtils.transpose(currentColumnsAsChars);

        BigInteger[] newColumns = new BigInteger[amountOfRows];

        for (int i = 0; i < amountOfRows; i ++) {
            newColumns[i] = new BigInteger(new String(newColumnsAsChars[i]), 2);
        }
        return new BigIntegerMatrix(newColumns, columns.length);
    }

    /**
     * Method to represent the content of the matrix as a 2D char array.
     * @return  A 2D char array of lengths columns x rows, where each subarray represents a bit representation of the columns of this matrix.
     *          <br>In case the bit representation of a columns is shorter than the amount of rows provided to the constructor,
     *          the bit representation will be padded at the beginning of the array with '0' chars.
     */
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

//    @Override
//    public String toString() {
//        StringBuilder stringBuilder = new StringBuilder();
//        char[][] charArray = convertToCharArrays();
//        for (int i = 0; i < charArray[0].length; i ++) {
//            for (int j = 0; j < charArray.length; j ++) {
//                stringBuilder.append(charArray[j][i]).append("\t");
//            }
//            stringBuilder.append(System.lineSeparator());
//        }
//        return stringBuilder.toString();
//    }

    /**
     * Getter for the columns of the matrix.
     * @return  The columns.
     */
    public BigInteger[] getColumns() {
        return columns;
    }
}
