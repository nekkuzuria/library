package com.xtramile.library2024.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class BookTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Book getBookSample1() {
        return new Book().id(1L).title("title1").year(1).totalPage(1).author("author1").cover("cover1").synopsis("synopsis1");
    }

    public static Book getBookSample2() {
        return new Book().id(2L).title("title2").year(2).totalPage(2).author("author2").cover("cover2").synopsis("synopsis2");
    }

    public static Book getBookRandomSampleGenerator() {
        return new Book()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .year(intCount.incrementAndGet())
            .totalPage(intCount.incrementAndGet())
            .author(UUID.randomUUID().toString())
            .cover(UUID.randomUUID().toString())
            .synopsis(UUID.randomUUID().toString());
    }
}
