package com.github.pvriel.oblivioustransfer4j.ote;

import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractObliviousTransferExtensionTest {

    public final static int AMOUNT_OF_CHOICES = 1000;
    public final static int MAX_BIT_LENGTH_VALUES = 2048;
    private final static Random random = new Random();

    protected abstract List<Triple<Integer, ObliviousTransferExtensionSender, ObliviousTransferExtensionReceiver>> generateSenderReceiverOTPairsForTesting();

    @Test
    @DisplayName("Test if the OT extension works as expected.")
    void execute() throws IOException, InterruptedException {
        System.out.println(this.getClass().toString());
        List<Triple<Integer,ObliviousTransferExtensionSender, ObliviousTransferExtensionReceiver>> listWithPairs = generateSenderReceiverOTPairsForTesting();
        for (var pair : listWithPairs) {
            System.out.println("Testing with " + pair.getLeft() + " base OTs.");
            ObliviousTransferExtensionSender sender = pair.getMiddle();
            ObliviousTransferExtensionReceiver receiver = pair.getRight();

            for (int i = 1; i <= MAX_BIT_LENGTH_VALUES; i *= 2) {
                BigInteger[][] x = new BigInteger[AMOUNT_OF_CHOICES][2];
                boolean[] choices = new boolean[AMOUNT_OF_CHOICES];
                for (int j = 0; j < AMOUNT_OF_CHOICES; j ++) {
                    for (int k = 0; k < 2; k ++) x[j][k] = new BigInteger(i, random);
                    choices[j] = random.nextBoolean();
                }

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
                BigInteger[] receivedValues = receiver.execute(choices, i, inTwo, outOne);
                senderThread.join();

                assertEquals(AMOUNT_OF_CHOICES, receivedValues.length);
                for (int j = 0; j < AMOUNT_OF_CHOICES; j ++) {
                    assertNotNull(receivedValues[j]);
                    if (!x[j][choices[j]? 1 : 0].equals(receivedValues[j])) {
                        System.out.println(Arrays.toString(x[j]));
                        System.out.println(choices[j]);
                        fail("Expected value %s, got %s.".formatted(x[j][choices[j]? 1 : 0], receivedValues[j]));
                    }
                }

                inOne.close();
                inTwo.close();
                outOne.close();
                outTwo.close();
                System.gc();
            }
        }
    }
}
