package com.github.pvriel.oblivioustransfer4j.rot.semihonest.asharov17;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.rot.AbstractROTTest;
import com.github.pvriel.oblivioustransfer4j.rrot.RandomObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.rrot.semihonest.asharov17.ASHAROV17ReceiverRandomObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.rrot.semihonest.asharov17.ASHAROV17ReceiverRandomObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.srot.RandomObliviousTransferSender;
import org.apache.commons.lang3.tuple.Triple;

import java.util.ArrayList;
import java.util.List;

import static com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferTest.*;
import static com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferTest.g;

class ASHAROV17RandomObliviousTransferTest extends AbstractROTTest {

    private final static int MAX_AMOUNT_OF_BASE_OTS = 1024;

    @Override
    protected List<Triple<Integer, RandomObliviousTransferSender, RandomObliviousTransferReceiver>> generateSenderReceiverOTPairsForTesting() {
        List<Triple<Integer, RandomObliviousTransferSender, RandomObliviousTransferReceiver>> returnValue = new ArrayList<>();
        for (int i = 1; i < MAX_AMOUNT_OF_BASE_OTS; i *= 2) {
            ObliviousTransferSender baseOTsSender = new ALSZ13ObliviousTransferSender(p, q, g);
            ObliviousTransferReceiver baseOTsReceiver = new ALSZ13ObliviousTransferReceiver(p, q, g);
            RandomObliviousTransferSender sender = new ASHAROV17RandomObliviousTransferSender(i, baseOTsReceiver);
            RandomObliviousTransferReceiver receiver = new ASHAROV17RandomObliviousTransferReceiver(i, baseOTsSender);

            returnValue.add(Triple.of(i, sender, receiver));
        }

        return returnValue;
    }
}