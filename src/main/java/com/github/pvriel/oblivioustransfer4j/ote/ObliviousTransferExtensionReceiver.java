package com.github.pvriel.oblivioustransfer4j.ote;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;


/**
 * Abstract class representing {@link ObliviousTransferReceiver}s which execute oblivious transfer extension protocols.
 */
public abstract class ObliviousTransferExtensionReceiver implements ObliviousTransferReceiver {

    private final int amountOfBaseOTs;
    private final ObliviousTransferSender baseOTsSender;


    /**
     * Constructor for the {@link ObliviousTransferExtensionReceiver} class.
     * @param   amountOfBaseOTs
     *          The amount of base OTs the underlying {@link ObliviousTransferSender} should perform.
     *          <br>This should be a value of at least one.
     * @param   baseOTsSender
     *          The (not-null) underlying {@link ObliviousTransferSender} protocol to perform the base OTs with.
     */
    protected ObliviousTransferExtensionReceiver(int amountOfBaseOTs, ObliviousTransferSender baseOTsSender) {
        this.amountOfBaseOTs = amountOfBaseOTs;
        this.baseOTsSender = baseOTsSender;
    }

    /**
     * Getter for the amount of base OTs the underlying {@link ObliviousTransferSender} should perform.
     * @return The amount of base OTs the underlying {@link ObliviousTransferSender} should perform.
     */
    protected int getAmountOfBaseOTs() {
        return amountOfBaseOTs;
    }

    /**
     * Getter for the underlying {@link ObliviousTransferSender} protocol to perform the base OTs with.
     * @return The underlying {@link ObliviousTransferSender} protocol to perform the base OTs with.
     */
    protected ObliviousTransferSender getBaseOTsSender() {
        return baseOTsSender;
    }
}
