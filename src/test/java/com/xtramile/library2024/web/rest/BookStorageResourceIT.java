package com.xtramile.library2024.web.rest;

import static com.xtramile.library2024.domain.BookStorageAsserts.*;
import static com.xtramile.library2024.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtramile.library2024.IntegrationTest;
import com.xtramile.library2024.domain.BookStorage;
import com.xtramile.library2024.repository.BookStorageRepository;
import com.xtramile.library2024.service.dto.BookStorageDTO;
import com.xtramile.library2024.service.mapper.BookStorageMapper;
import jakarta.persistence.EntityManager;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link BookStorageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class BookStorageResourceIT {

    private static final Integer DEFAULT_QUANTITY = 1;
    private static final Integer UPDATED_QUANTITY = 2;

    private static final String ENTITY_API_URL = "/api/book-storages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private BookStorageRepository bookStorageRepository;

    @Autowired
    private BookStorageMapper bookStorageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restBookStorageMockMvc;

    private BookStorage bookStorage;

    private BookStorage insertedBookStorage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookStorage createEntity(EntityManager em) {
        BookStorage bookStorage = new BookStorage().quantity(DEFAULT_QUANTITY);
        return bookStorage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static BookStorage createUpdatedEntity(EntityManager em) {
        BookStorage bookStorage = new BookStorage().quantity(UPDATED_QUANTITY);
        return bookStorage;
    }

    @BeforeEach
    public void initTest() {
        bookStorage = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedBookStorage != null) {
            bookStorageRepository.delete(insertedBookStorage);
            insertedBookStorage = null;
        }
    }

    @Test
    @Transactional
    void createBookStorage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the BookStorage
        BookStorageDTO bookStorageDTO = bookStorageMapper.toDto(bookStorage);
        var returnedBookStorageDTO = om.readValue(
            restBookStorageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookStorageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            BookStorageDTO.class
        );

        // Validate the BookStorage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedBookStorage = bookStorageMapper.toEntity(returnedBookStorageDTO);
        assertBookStorageUpdatableFieldsEquals(returnedBookStorage, getPersistedBookStorage(returnedBookStorage));

        insertedBookStorage = returnedBookStorage;
    }

    @Test
    @Transactional
    void createBookStorageWithExistingId() throws Exception {
        // Create the BookStorage with an existing ID
        bookStorage.setId(1L);
        BookStorageDTO bookStorageDTO = bookStorageMapper.toDto(bookStorage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restBookStorageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookStorageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the BookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllBookStorages() throws Exception {
        // Initialize the database
        insertedBookStorage = bookStorageRepository.saveAndFlush(bookStorage);

        // Get all the bookStorageList
        restBookStorageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(bookStorage.getId().intValue())))
            .andExpect(jsonPath("$.[*].quantity").value(hasItem(DEFAULT_QUANTITY)));
    }

    @Test
    @Transactional
    void getBookStorage() throws Exception {
        // Initialize the database
        insertedBookStorage = bookStorageRepository.saveAndFlush(bookStorage);

        // Get the bookStorage
        restBookStorageMockMvc
            .perform(get(ENTITY_API_URL_ID, bookStorage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(bookStorage.getId().intValue()))
            .andExpect(jsonPath("$.quantity").value(DEFAULT_QUANTITY));
    }

    @Test
    @Transactional
    void getNonExistingBookStorage() throws Exception {
        // Get the bookStorage
        restBookStorageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingBookStorage() throws Exception {
        // Initialize the database
        insertedBookStorage = bookStorageRepository.saveAndFlush(bookStorage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookStorage
        BookStorage updatedBookStorage = bookStorageRepository.findById(bookStorage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedBookStorage are not directly saved in db
        em.detach(updatedBookStorage);
        updatedBookStorage.quantity(UPDATED_QUANTITY);
        BookStorageDTO bookStorageDTO = bookStorageMapper.toDto(updatedBookStorage);

        restBookStorageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookStorageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookStorageDTO))
            )
            .andExpect(status().isOk());

        // Validate the BookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedBookStorageToMatchAllProperties(updatedBookStorage);
    }

    @Test
    @Transactional
    void putNonExistingBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookStorage.setId(longCount.incrementAndGet());

        // Create the BookStorage
        BookStorageDTO bookStorageDTO = bookStorageMapper.toDto(bookStorage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookStorageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, bookStorageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookStorageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookStorage.setId(longCount.incrementAndGet());

        // Create the BookStorage
        BookStorageDTO bookStorageDTO = bookStorageMapper.toDto(bookStorage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookStorageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(bookStorageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookStorage.setId(longCount.incrementAndGet());

        // Create the BookStorage
        BookStorageDTO bookStorageDTO = bookStorageMapper.toDto(bookStorage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookStorageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(bookStorageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateBookStorageWithPatch() throws Exception {
        // Initialize the database
        insertedBookStorage = bookStorageRepository.saveAndFlush(bookStorage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookStorage using partial update
        BookStorage partialUpdatedBookStorage = new BookStorage();
        partialUpdatedBookStorage.setId(bookStorage.getId());

        restBookStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookStorage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookStorage))
            )
            .andExpect(status().isOk());

        // Validate the BookStorage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookStorageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedBookStorage, bookStorage),
            getPersistedBookStorage(bookStorage)
        );
    }

    @Test
    @Transactional
    void fullUpdateBookStorageWithPatch() throws Exception {
        // Initialize the database
        insertedBookStorage = bookStorageRepository.saveAndFlush(bookStorage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the bookStorage using partial update
        BookStorage partialUpdatedBookStorage = new BookStorage();
        partialUpdatedBookStorage.setId(bookStorage.getId());

        partialUpdatedBookStorage.quantity(UPDATED_QUANTITY);

        restBookStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedBookStorage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedBookStorage))
            )
            .andExpect(status().isOk());

        // Validate the BookStorage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertBookStorageUpdatableFieldsEquals(partialUpdatedBookStorage, getPersistedBookStorage(partialUpdatedBookStorage));
    }

    @Test
    @Transactional
    void patchNonExistingBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookStorage.setId(longCount.incrementAndGet());

        // Create the BookStorage
        BookStorageDTO bookStorageDTO = bookStorageMapper.toDto(bookStorage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restBookStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, bookStorageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookStorageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookStorage.setId(longCount.incrementAndGet());

        // Create the BookStorage
        BookStorageDTO bookStorageDTO = bookStorageMapper.toDto(bookStorage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(bookStorageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the BookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        bookStorage.setId(longCount.incrementAndGet());

        // Create the BookStorage
        BookStorageDTO bookStorageDTO = bookStorageMapper.toDto(bookStorage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restBookStorageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(bookStorageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the BookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteBookStorage() throws Exception {
        // Initialize the database
        insertedBookStorage = bookStorageRepository.saveAndFlush(bookStorage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the bookStorage
        restBookStorageMockMvc
            .perform(delete(ENTITY_API_URL_ID, bookStorage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return bookStorageRepository.count();
    }

    protected void assertIncrementedRepositoryCount(long countBefore) {
        assertThat(countBefore + 1).isEqualTo(getRepositoryCount());
    }

    protected void assertDecrementedRepositoryCount(long countBefore) {
        assertThat(countBefore - 1).isEqualTo(getRepositoryCount());
    }

    protected void assertSameRepositoryCount(long countBefore) {
        assertThat(countBefore).isEqualTo(getRepositoryCount());
    }

    protected BookStorage getPersistedBookStorage(BookStorage bookStorage) {
        return bookStorageRepository.findById(bookStorage.getId()).orElseThrow();
    }

    protected void assertPersistedBookStorageToMatchAllProperties(BookStorage expectedBookStorage) {
        assertBookStorageAllPropertiesEquals(expectedBookStorage, getPersistedBookStorage(expectedBookStorage));
    }

    protected void assertPersistedBookStorageToMatchUpdatableProperties(BookStorage expectedBookStorage) {
        assertBookStorageAllUpdatablePropertiesEquals(expectedBookStorage, getPersistedBookStorage(expectedBookStorage));
    }
}
