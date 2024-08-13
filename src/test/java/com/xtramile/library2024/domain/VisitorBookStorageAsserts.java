package com.xtramile.library2024.domain;

import static org.assertj.core.api.Assertions.assertThat;

public class VisitorBookStorageAsserts {

    /**
     * Asserts that the entity has all properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertVisitorBookStorageAllPropertiesEquals(VisitorBookStorage expected, VisitorBookStorage actual) {
        assertVisitorBookStorageAutoGeneratedPropertiesEquals(expected, actual);
        assertVisitorBookStorageAllUpdatablePropertiesEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all updatable properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertVisitorBookStorageAllUpdatablePropertiesEquals(VisitorBookStorage expected, VisitorBookStorage actual) {
        assertVisitorBookStorageUpdatableFieldsEquals(expected, actual);
        assertVisitorBookStorageUpdatableRelationshipsEquals(expected, actual);
    }

    /**
     * Asserts that the entity has all the auto generated properties (fields/relationships) set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertVisitorBookStorageAutoGeneratedPropertiesEquals(VisitorBookStorage expected, VisitorBookStorage actual) {
        assertThat(expected)
            .as("Verify VisitorBookStorage auto generated properties")
            .satisfies(e -> assertThat(e.getId()).as("check id").isEqualTo(actual.getId()));
    }

    /**
     * Asserts that the entity has all the updatable fields set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertVisitorBookStorageUpdatableFieldsEquals(VisitorBookStorage expected, VisitorBookStorage actual) {
        assertThat(expected)
            .as("Verify VisitorBookStorage relevant properties")
            .satisfies(e -> assertThat(e.getBorrowDate()).as("check borrowDate").isEqualTo(actual.getBorrowDate()))
            .satisfies(e -> assertThat(e.getReturnDate()).as("check returnDate").isEqualTo(actual.getReturnDate()));
    }

    /**
     * Asserts that the entity has all the updatable relationships set.
     *
     * @param expected the expected entity
     * @param actual the actual entity
     */
    public static void assertVisitorBookStorageUpdatableRelationshipsEquals(VisitorBookStorage expected, VisitorBookStorage actual) {
        assertThat(expected)
            .as("Verify VisitorBookStorage relationships")
            .satisfies(e -> assertThat(e.getVisitor()).as("check visitor").isEqualTo(actual.getVisitor()))
            .satisfies(e -> assertThat(e.getBook()).as("check book").isEqualTo(actual.getBook()));
    }
}
