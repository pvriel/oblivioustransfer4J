package com.github.pvriel.oblivioustransfer4j.rrot.semihonest.asharov17;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.rrot.AbstractRROTTest;
import com.github.pvriel.oblivioustransfer4j.rrot.RandomObliviousTransferReceiver;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

import static com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferTest.*;
import static com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferTest.g;

public class ASHAROV17RROTTest extends AbstractRROTTest {

    private final static int MAX_AMOUNT_OF_BASE_OTS = 1000;

    @Override
    protected List<Triple<Integer, ObliviousTransferSender, RandomObliviousTransferReceiver>> generateSenderReceiverOTPairsForTesting() {
        List<Triple<Integer, ObliviousTransferSender, RandomObliviousTransferReceiver>> returnValue = new ArrayList<>();
        for (int i = 1; i < MAX_AMOUNT_OF_BASE_OTS; i *= 2) {
            ObliviousTransferSender baseOTsSender = new ALSZ13ObliviousTransferSender(p, q, g);
            ObliviousTransferReceiver baseOTsReceiver = new ALSZ13ObliviousTransferReceiver(p, q, g);
            ObliviousTransferSender sender = new ASHAROV17ReceiverRandomObliviousTransferSender(i, baseOTsReceiver);
            RandomObliviousTransferReceiver receiver = new ASHAROV17ReceiverRandomObliviousTransferReceiver(i, baseOTsSender);

            returnValue.add(Triple.of(i, sender, receiver));
        }

        return returnValue;
    }
}
