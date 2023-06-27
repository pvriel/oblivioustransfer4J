package com.github.pvriel.oblivioustransfer4j.rot;

import com.github.pvriel.oblivioustransfer4j.rrot.RandomObliviousTransferReceiver;
import com.github.pvriel.oblivioustransfer4j.srot.RandomObliviousTransferSender;
import org.apache.commons.lang3.tuple.Pair;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractROTTest {

    public final static int AMOUNT_OF_CHOICES = 1000;
    public final static int MAX_BIT_LENGTH_VALUES = 2048;

    protected abstract List<Triple<Integer, RandomObliviousTransferSender, RandomObliviousTransferReceiver>> generateSenderReceiverOTPairsForTesting();

    @Test
    @DisplayName("Test the correctness of the Random Oblivious Transfer protocol.")
    public void test() throws Exception {
        System.out.println(this.getClass().toString());
        for (var triple : generateSenderReceiverOTPairsForTesting()) {
            Integer amountOfBaseOTs = triple.getLeft();
            RandomObliviousTransferSender sender = triple.getMiddle();
            RandomObliviousTransferReceiver receiver = triple.getRight();
            System.out.println("Testing with " + amountOfBaseOTs + " base OTs.");

            for (int i = 1; i < MAX_BIT_LENGTH_VALUES; i *= 2) {
                PipedInputStream inOne = new PipedInputStream(100000000);
                PipedOutputStream outOne = new PipedOutputStream(inOne);
                PipedInputStream inTwo = new PipedInputStream(100000000);
                PipedOutputStream outTwo = new PipedOutputStream(inTwo);

                int finalI = i;
                AtomicReference<BigInteger[][]> valuesSender = new AtomicReference<>();
                Thread senderThread = new Thread(() -> {
                    try {
                        valuesSender.set(sender.execute(AMOUNT_OF_CHOICES, finalI, inOne, outTwo));
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                senderThread.start();
                Pair<boolean[], BigInteger[]> receivedValues = receiver.execute(AMOUNT_OF_CHOICES, finalI, inTwo, outOne);
                senderThread.join();

                BigInteger[][] x = valuesSender.get();
                boolean[] choices = receivedValues.getLeft();
                BigInteger[] chosenXs = receivedValues.getRight();

                assertEquals(AMOUNT_OF_CHOICES, x.length);
                assertEquals(AMOUNT_OF_CHOICES, choices.length);
                assertEquals(AMOUNT_OF_CHOICES, chosenXs.length);
                for (int j = 0; j < AMOUNT_OF_CHOICES; j ++) {
                    for (int k = 0; k < 2; k ++) assertNotNull(x[j][k]);
                    assertNotNull(chosenXs[j]);
                    if (!x[j][choices[j]? 1 : 0].equals(chosenXs[j])) {
                        fail("Expected value %s, but got %s.".formatted(x[j][choices[j]? 1 : 0], chosenXs[j]));
                    }
                }
            }
        }
    }
}
