package com.github.pvriel.oblivioustransfer4j.ote;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;

/**
 * Abstract class representing {@link ObliviousTransferSender}s which execute oblivious transfer extension protocols.
 */
public abstract class ObliviousTransferExtensionSender implements ObliviousTransferSender {

    private final int amountOfBaseOTs;
    private final ObliviousTransferReceiver baseOTsReceiver;

    /**
     * Constructor for the {@link ObliviousTransferExtensionSender} class.
     * @param   amountOfBaseOTs
     *          The amount of base OTs the underlying {@link ObliviousTransferReceiver} should perform.
     *          <br>This should be a value of at least one.
     * @param   baseOTsReceiver
     *          The (not-null) underlying {@link ObliviousTransferReceiver} protocol to perform the base OTs with.
     */
    protected ObliviousTransferExtensionSender(int amountOfBaseOTs, ObliviousTransferReceiver baseOTsReceiver) {
        this.amountOfBaseOTs = amountOfBaseOTs;
        this.baseOTsReceiver = baseOTsReceiver;
    }

    /**
     * Getter for the amount of base OTs the underlying {@link ObliviousTransferReceiver} should perform.
     * @return  The amount of base OTs the underlying {@link ObliviousTransferReceiver} should perform.
     */
    protected int getAmountOfBaseOTs() {
        return amountOfBaseOTs;
    }

    /**
     * Getter for the underlying {@link ObliviousTransferReceiver} protocol to perform the base OTs with.
     * @return The underlying {@link ObliviousTransferReceiver} protocol to perform the base OTs with.
     */
    protected ObliviousTransferReceiver getBaseOTsReceiver() {
        return baseOTsReceiver;
    }
}
