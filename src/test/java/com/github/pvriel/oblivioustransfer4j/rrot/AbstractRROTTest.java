package com.github.pvriel.oblivioustransfer4j.rrot;

import com.github.pvriel.oblivioustransfer4j.ot.ObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.srot.RandomObliviousTransferSender;
import com.github.pvriel.oblivioustransfer4j.utils.ArrayUtils;
import com.github.pvriel.oblivioustransfer4j.utils.RandomUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractRROTTest {

    public final static int AMOUNT_OF_CHOICES = 1000;
    public final static int MAX_BIT_LENGTH_VALUES = 65536;

    protected abstract List<Triple<Integer, ObliviousTransferSender, RandomObliviousTransferReceiver>> generateSenderReceiverOTPairsForTesting();

    @Test
    public void test() throws Exception {
        System.out.println(this.getClass().toString());
        for (var triple : generateSenderReceiverOTPairsForTesting()) {
            Integer amountOfBaseOTs = triple.getLeft();
            ObliviousTransferSender sender = triple.getMiddle();
            RandomObliviousTransferReceiver receiver = triple.getRight();
            System.out.println("Testing with " + amountOfBaseOTs + " base OTs.");

            for (int i = 1; i < MAX_BIT_LENGTH_VALUES; i *= 2) {
                BigInteger[][] x = RandomUtils.generateRandomBigInteger2DArrayOfLengths(AMOUNT_OF_CHOICES, 2, i);

                PipedInputStream inOne = new PipedInputStream(100000000);
                PipedOutputStream outOne = new PipedOutputStream(inOne);
                PipedInputStream inTwo = new PipedInputStream(100000000);
                PipedOutputStream outTwo = new PipedOutputStream(inTwo);

                int finalI = i;
                Thread senderThread = new Thread(() -> {
                    try {
                        sender.execute(x, finalI, inOne, outTwo);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                senderThread.start();
                Pair<boolean[], BigInteger[]> receivedValues = receiver.execute(x.length, i, inTwo, outOne);
                senderThread.join();
                boolean[] choices = receivedValues.getLeft();
                BigInteger[] chosenXs = receivedValues.getRight();

                assertEquals(AMOUNT_OF_CHOICES, choices.length);
                assertEquals(AMOUNT_OF_CHOICES, chosenXs.length);
                for (int j = 0; j < AMOUNT_OF_CHOICES; j ++) {
                    assertNotNull(chosenXs[j]);
                    if (!x[j][choices[j]? 1 : 0].equals(chosenXs[j])) {
                        System.out.println(ArrayUtils.toString(x));
                        System.out.println(Arrays.toString(choices));
                        System.out.println(Arrays.toString(chosenXs));
                        fail("Expected value %s, but got %s.".formatted(x[j][choices[j]? 1 : 0], chosenXs[j]));
                    }
                }
            }
        }
    }
}
