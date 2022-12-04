package com.github.pvriel.oblivioustransfer4j.ote;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public interface ObliviousTransferExtensionReceiver {


    BigInteger[] execute(boolean[] choices, int bitLength,
                         int amountOfBaseOTs, ObliviousTransferSender baseOTsSender,
                         InputStream inputStream, OutputStream outputStream) throws IOException;
}
