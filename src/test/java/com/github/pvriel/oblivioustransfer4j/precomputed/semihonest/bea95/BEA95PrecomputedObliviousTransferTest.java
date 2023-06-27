package com.github.pvriel.oblivioustransfer4j.precomputed.semihonest.bea95;

import com.github.pvriel.oblivioustransfer4j.ot.AbstractObliviousTransferTest;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.precomputed.semihonest.bea95.BEA95PrecomputedObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.precomputed.semihonest.bea95.BEA95PrecomputedObliviousTransferSender;
import org.apache.commons.lang3.tuple.Pair;

import static com.github.pvriel.oblivioustransfer4j.ot.semihonest.alsz13.ALSZ13ObliviousTransferTest.*;

class BEA95PrecomputedObliviousTransferTest extends AbstractObliviousTransferTest {

    @Override
    protected Pair<ObliviousTransferSender, ObliviousTransferReceiver> generateSenderReceiverOTPairForTesting() {
        ALSZ13ObliviousTransferSender underlyingSender = new ALSZ13ObliviousTransferSender(p, q, g);
        ALSZ13ObliviousTransferReceiver underlyingReceiver = new ALSZ13ObliviousTransferReceiver(p, q, g);

        var sender = new BEA95PrecomputedObliviousTransferSender(underlyingSender);
        var receiver = new BEA95PrecomputedObliviousTransferReceiver(underlyingReceiver);

        return Pair.of(sender, receiver);
    }
}