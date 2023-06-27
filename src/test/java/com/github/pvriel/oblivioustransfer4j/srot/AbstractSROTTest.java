package com.github.pvriel.oblivioustransfer4j.srot;

import com.github.pvriel.mpcutils4j.random.RandomUtils;
import com.github.pvriel.oblivioustransfer4j.ote.ObliviousTransferExtensionReceiver;
import org.apache.commons.lang3.tuple.Triple;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.jupiter.api.Assertions.*;

public abstract class AbstractSROTTest {

    public final static int AMOUNT_OF_CHOICES = 1000;
    public final static int MAX_BIT_LENGTH_VALUES = 2048;

    protected abstract List<Triple<Integer, RandomObliviousTransferSender, ObliviousTransferExtensionReceiver>> generateSenderReceiverOTPairsForTesting();

    @Test
    @DisplayName("Test the SROT protocol with different amounts of base OTs and different bit lengths.")
    public void execute() throws IOException, InterruptedException {
        System.out.println(this.getClass().toString());
        for (var triple : generateSenderReceiverOTPairsForTesting()) {
            Integer amountOfBaseOTs = triple.getLeft();
            RandomObliviousTransferSender sender = triple.getMiddle();
            ObliviousTransferExtensionReceiver receiver = triple.getRight();
            System.out.println("Testing with " + amountOfBaseOTs + " base OTs.");

            for (int i = 1; i < MAX_BIT_LENGTH_VALUES; i *= 2) {
                boolean[] choices = RandomUtils.generateRandomBooleanArrayOfLength(AMOUNT_OF_CHOICES);

                PipedInputStream inOne = new PipedInputStream(100000000);
                PipedOutputStream outOne = new PipedOutputStream(inOne);
                PipedInputStream inTwo = new PipedInputStream(100000000);
                PipedOutputStream outTwo = new PipedOutputStream(inTwo);

                int finalI = i;
                AtomicReference<BigInteger[][]> atomicX = new AtomicReference<>();
                Thread senderThread = new Thread(() -> {
                    try {
                        var result = sender.execute(AMOUNT_OF_CHOICES, finalI, inOne, outTwo);
                        atomicX.set(result);
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                });
                senderThread.start();
                BigInteger[] receivedValues = receiver.execute(choices, i, inTwo, outOne);
                senderThread.join();
                BigInteger[][] x = atomicX.get();

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
