package com.github.pvriel.oblivioustransfer4j.ote;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;

public abstract class ObliviousTransferExtensionSender implements ObliviousTransferSender {

    private final int amountOfBaseOTs;
    private final ObliviousTransferReceiver baseOTsReceiver;

    protected ObliviousTransferExtensionSender(int amountOfBaseOTs, ObliviousTransferReceiver baseOTsReceiver) {
        this.amountOfBaseOTs = amountOfBaseOTs;
        this.baseOTsReceiver = baseOTsReceiver;
    }

    protected int getAmountOfBaseOTs() {
        return amountOfBaseOTs;
    }

    protected ObliviousTransferReceiver getBaseOTsReceiver() {
        return baseOTsReceiver;
    }
}
