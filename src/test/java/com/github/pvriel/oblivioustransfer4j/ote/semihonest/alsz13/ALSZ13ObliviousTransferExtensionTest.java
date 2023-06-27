package com.github.pvriel.oblivioustransfer4j.ote.semihonest.alsz13;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.ote.AbstractObliviousTransferExtensionTest;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionReceiver;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionSender;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

import static com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferTest.*;

public class ALSZ13ObliviousTransferExtensionTest extends AbstractObliviousTransferExtensionTest {

    private final static int MAX_AMOUNT_OF_BASE_OTS = 1000;

    @Override
    protected List<Triple<Integer, ObliviousTransferExtensionSender, ObliviousTransferExtensionReceiver>> generateSenderReceiverOTPairsForTesting() {
        List<Triple<Integer, ObliviousTransferExtensionSender, ObliviousTransferExtensionReceiver>> returnValues = new ArrayList<>();

        for (int i = 1; i <= MAX_AMOUNT_OF_BASE_OTS; i *= 2) {
            ObliviousTransferSender baseOTsSender = new ALSZ13ObliviousTransferSender(p, q, g);
            ObliviousTransferReceiver baseOTsReceiver = new ALSZ13ObliviousTransferReceiver(p, q, g);
            ALSZ13ObliviousTransferExtensionReceiver receiver = new ALSZ13ObliviousTransferExtensionReceiver(i, baseOTsSender);
            ALSZ13ObliviousTransferExtensionSender sender = new ALSZ13ObliviousTransferExtensionSender(i, baseOTsReceiver);
            returnValues.add(Triple.of(i, sender, receiver));
        }

        return returnValues;
    }
}
