package com.github.pvriel.oblivioustransfer4j.ote;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;


public abstract class ObliviousTransferExtensionReceiver implements ObliviousTransferReceiver {

    private final int amountOfBaseOTs;
    private final ObliviousTransferSender baseOTsSender;


    protected ObliviousTransferExtensionReceiver(int amountOfBaseOTs, ObliviousTransferSender baseOTsSender) {
        this.amountOfBaseOTs = amountOfBaseOTs;
        this.baseOTsSender = baseOTsSender;
    }

    protected int getAmountOfBaseOTs() {
        return amountOfBaseOTs;
    }

    protected ObliviousTransferSender getBaseOTsSender() {
        return baseOTsSender;
    }
}
