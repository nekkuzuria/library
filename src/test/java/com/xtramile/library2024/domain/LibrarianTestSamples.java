package com.xtramile.library2024.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class LibrarianTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Librarian getLibrarianSample1() {
        return new Librarian().id(1L).name("name1").email("email1").phoneNumber("phoneNumber1");
    }

    public static Librarian getLibrarianSample2() {
        return new Librarian().id(2L).name("name2").email("email2").phoneNumber("phoneNumber2");
    }

    public static Librarian getLibrarianRandomSampleGenerator() {
        return new Librarian()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .email(UUID.randomUUID().toString())
            .phoneNumber(UUID.randomUUID().toString());
    }
}
