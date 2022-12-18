package com.github.pvriel.oblivioustransfer4j.ot;

import com.github.pvriel.oblivioustransfer4j.utils.ArrayUtils;
import org.apache.commons.lang3.tuple.Pair;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Random;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractObliviousTransferTest {

    public final static int AMOUNT_OF_CHOICES = 4;
    public final static int MAX_BIT_LENGTH_VALUES = 65536;
    private final static Random random = new Random();

    protected abstract Pair<ObliviousTransferSender, ObliviousTransferReceiver> generateSenderReceiverOTPairForTesting();

    @Test
    void execute() throws IOException, InterruptedException {
        System.out.println(this.getClass().toString());
        Pair<ObliviousTransferSender, ObliviousTransferReceiver> senderReceiverPair = generateSenderReceiverOTPairForTesting();
        ObliviousTransferSender sender = senderReceiverPair.getLeft();
        ObliviousTransferReceiver receiver = senderReceiverPair.getRight();

        for (int i = 4; i <= MAX_BIT_LENGTH_VALUES; i *= 2) {

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
                    System.out.println("FAIL");
                    System.out.println("x: " + ArrayUtils.toString(x));
                    System.out.println("choices: " + Arrays.toString(choices));
                    System.out.println("received values: " + Arrays.toString(receivedValues));
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