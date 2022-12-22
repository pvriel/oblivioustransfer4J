package com.github.pvriel.oblivioustransfer4j.srot;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public interface RandomObliviousTransferSender {

    BigInteger[][] execute(int amountOfChoices, int bitLength, InputStream inputStream, OutputStream outputStream) throws IOException;
}
