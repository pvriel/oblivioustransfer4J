package com.github.pvriel.oblivioustransfer4j;

import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractObliviousTransferTest {

    private final static int AMOUNT_OF_CHOICES = 1000;
    private final static int MAX_BIT_LENGTH_VALUES = 65536;
    private final static Random random = new Random();

    protected abstract Pair<ObliviousTransferSender, ObliviousTransferReceiver> generateSenderReceiverOTPairForTesting(int bitLength);

    @Test
    void execute() throws IOException, InterruptedException {
        System.out.println(this.getClass().toString());
        for (int i = 1; i <= MAX_BIT_LENGTH_VALUES; i *= 2) {
            Pair<ObliviousTransferSender, ObliviousTransferReceiver> senderReceiverPair = generateSenderReceiverOTPairForTesting(i);
            ObliviousTransferSender sender = senderReceiverPair.getLeft();
            ObliviousTransferReceiver receiver = senderReceiverPair.getRight();

            System.out.println("Current bit length : %s".formatted(i));
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

            Thread senderThread = new Thread(() -> sender.execute(x, inOne, outTwo));
            senderThread.start();
            BigInteger[] receivedValues = receiver.execute(choices, inTwo, outOne);
            senderThread.join();

            assertEquals(AMOUNT_OF_CHOICES, receivedValues.length);
            for (int j = 0; j < AMOUNT_OF_CHOICES; j ++) {
                for (int k = 0; k < 2; k ++) assertNotNull(receivedValues[k]);
                if (!x[j][choices[j]? 1 : 0].equals(receivedValues[j])) {
                    fail("Expected value %s, got %s.".formatted(x[j][choices[j]? 1 : 0], receivedValues[j]));
                }
            }
        }
    }
}