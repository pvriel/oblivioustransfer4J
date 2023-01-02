package com.github.pvriel.oblivioustransfer4j.rrot;

import org.apache.commons.lang3.tuple.Pair;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public interface RandomObliviousTransferReceiver {

    Pair<boolean[], BigInteger[]> execute(int amountOfChoices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;
}
