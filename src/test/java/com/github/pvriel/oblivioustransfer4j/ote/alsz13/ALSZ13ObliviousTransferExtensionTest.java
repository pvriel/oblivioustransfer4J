package com.github.pvriel.oblivioustransfer4j.ote.alsz13;

import com.github.pvriel.oblivioustransfer4j.ote.AbstractObliviousTransferExtensionTest;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionReceiver;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionSender;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

public class ALSZ13ObliviousTransferExtensionTest extends AbstractObliviousTransferExtensionTest {
    @Override
    protected List<Triple<Integer, ObliviousTransferExtensionSender, ObliviousTransferExtensionReceiver>> generateSenderReceiverOTPairsForTesting() {
        List<Triple<Integer, ObliviousTransferExtensionSender, ObliviousTransferExtensionReceiver>> returnValues = new ArrayList<>();

        ALSZ13ObliviousTransferExtensionReceiver receiver = new ALSZ13ObliviousTransferExtensionReceiver();
        ALSZ13ObliviousTransferExtensionSender sender = new ALSZ13ObliviousTransferExtensionSender();
        for (int i = 1; i <= AMOUNT_OF_CHOICES; i *= 2) {
            returnValues.add(Triple.of(i, sender, receiver));
        }

        return returnValues;
    }
}
