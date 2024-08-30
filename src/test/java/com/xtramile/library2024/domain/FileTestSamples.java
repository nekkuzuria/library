package com.xtramile.library2024.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FileTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static File getFileSample1() {
        return new File().id(1L).image("image1");
    }

    public static File getFileSample2() {
        return new File().id(2L).image("image2");
    }

    public static File getFileRandomSampleGenerator() {
        return new File().id(longCount.incrementAndGet()).image(UUID.randomUUID().toString());
    }
}
