package com.github.pvriel.oblivioustransfer4j.srot.semihonest.asharov17;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionReceiver;
import com.github.pvriel.oblivioustransfer4j.srot.AbstractSROTTest;
import com.github.pvriel.oblivioustransfer4j.srot.RandomObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.srot.semihonest.asharov17.ASHAROV17SenderRandomObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.srot.semihonest.asharov17.ASHAROV17SenderRandomObliviousTransferSender;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

import static com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferTest.*;
import static com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferTest.g;

public class ASHAROV17SROTTest extends AbstractSROTTest {

    private final static int MAX_AMOUNT_OF_BASE_OTS = 1000;

    @Override
    protected List<Triple<Integer, RandomObliviousTransferSender, ObliviousTransferExtensionReceiver>> generateSenderReceiverOTPairsForTesting() {
        List<Triple<Integer, RandomObliviousTransferSender, ObliviousTransferExtensionReceiver>> returnValue = new ArrayList<>();
        for (int i = 1; i < MAX_AMOUNT_OF_BASE_OTS; i *= 2) {
            ObliviousTransferSender baseOTsSender = new ALSZ13ObliviousTransferSender(p, q, g);
            ObliviousTransferReceiver baseOTsReceiver = new ALSZ13ObliviousTransferReceiver(p, q, g);
            RandomObliviousTransferSender sender = new ASHAROV17SenderRandomObliviousTransferSender(i, baseOTsReceiver);
            ObliviousTransferExtensionReceiver receiver = new ASHAROV17SenderRandomObliviousTransferReceiver(i, baseOTsSender);
            returnValue.add(Triple.of(i, sender, receiver));
        }
        return returnValue;
    }
}
