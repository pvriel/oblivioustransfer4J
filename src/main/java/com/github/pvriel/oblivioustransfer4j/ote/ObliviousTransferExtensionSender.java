package com.github.pvriel.oblivioustransfer4j.ote;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;

public interface ObliviousTransferExtensionSender {

    void execute(BigInteger[][] x, int bitLength,
                 int amountOfBaseOTs, ObliviousTransferReceiver baseOTsReceiver,
                 InputStream inputStream, OutputStream outputStream) throws IOException;
}
