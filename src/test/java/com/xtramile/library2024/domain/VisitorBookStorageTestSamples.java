package com.xtramile.library2024.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public class VisitorBookStorageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static VisitorBookStorage getVisitorBookStorageSample1() {
        return new VisitorBookStorage().id(1L);
    }

    public static VisitorBookStorage getVisitorBookStorageSample2() {
        return new VisitorBookStorage().id(2L);
    }

    public static VisitorBookStorage getVisitorBookStorageRandomSampleGenerator() {
        return new VisitorBookStorage().id(longCount.incrementAndGet());
    }
}
