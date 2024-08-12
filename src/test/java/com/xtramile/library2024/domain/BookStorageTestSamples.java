package com.xtramile.library2024.domain;

import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BookStorageTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static BookStorage getBookStorageSample1() {
        return new BookStorage().id(1L).quantity(1);
    }

    public static BookStorage getBookStorageSample2() {
        return new BookStorage().id(2L).quantity(2);
    }

    public static BookStorage getBookStorageRandomSampleGenerator() {
        return new BookStorage().id(longCount.incrementAndGet()).quantity(intCount.incrementAndGet());
    }
}
