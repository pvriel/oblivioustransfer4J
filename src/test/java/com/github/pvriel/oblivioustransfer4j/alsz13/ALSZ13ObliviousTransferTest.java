package com.github.pvriel.oblivioustransfer4j.alsz13;

import com.github.pvriel.oblivioustransfer4j.AbstractObliviousTransferTest;
import com.github.pvriel.oblivioustransfer4j.ObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.ObliviousTransferSender;
import org.apache.commons.lang3.tuple.Pair;

import java.math.BigInteger;

public class ALSZ13ObliviousTransferTest extends AbstractObliviousTransferTest {

    @Override
    protected Pair<ObliviousTransferSender, ObliviousTransferReceiver> generateSenderReceiverOTPairForTesting(int bitLength) {
        BigInteger p = new BigInteger("32317006071311007300714876688669951960444102669715484032130345427524655138867890893197201411522913463688717960921898019494119559150490921095088152386448283120630877367300996091750197750389652106796057638384067568276792218642619756161838094338476170470581645852036305042887575891541065808607552399123930385521914333389668342420684974786564569494856176035326322058077805659331026192708460314150258592864177116725943603718461857357598351152301645904403697613233287231227125684710820209725157101726931323469678542580656697935045997268352998638215525193403303896028543209689578721838988682461578457274025662014413066681559");
        BigInteger q = new BigInteger("26959946667150639794667015087019630673637144422540572481103610249951");
        BigInteger g = new MultiplicativeGroup(p).getElementOfOrder(q);

        ALSZ13ObliviousTransferSender sender = new ALSZ13ObliviousTransferSender(p, q, g, bitLength);
        ALSZ13ObliviousTransferReceiver receiver = new ALSZ13ObliviousTransferReceiver(p, q, g, bitLength);
        return Pair.of(sender, receiver);
    }
}
