package com.xtramile.library2024.web.rest;

import static com.xtramile.library2024.domain.VisitorBookStorageAsserts.*;
import static com.xtramile.library2024.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtramile.library2024.IntegrationTest;
import com.xtramile.library2024.domain.VisitorBookStorage;
import com.xtramile.library2024.repository.VisitorBookStorageRepository;
import com.xtramile.library2024.service.dto.VisitorBookStorageDTO;
import com.xtramile.library2024.service.mapper.VisitorBookStorageMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
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
 * Integration tests for the {@link VisitorBookStorageResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VisitorBookStorageResourceIT {

    private static final LocalDate DEFAULT_BORROW_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_BORROW_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final LocalDate DEFAULT_RETURN_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_RETURN_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/visitor-book-storages";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VisitorBookStorageRepository visitorBookStorageRepository;

    @Autowired
    private VisitorBookStorageMapper visitorBookStorageMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVisitorBookStorageMockMvc;

    private VisitorBookStorage visitorBookStorage;

    private VisitorBookStorage insertedVisitorBookStorage;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VisitorBookStorage createEntity(EntityManager em) {
        VisitorBookStorage visitorBookStorage = new VisitorBookStorage().borrowDate(DEFAULT_BORROW_DATE).returnDate(DEFAULT_RETURN_DATE);
        return visitorBookStorage;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static VisitorBookStorage createUpdatedEntity(EntityManager em) {
        VisitorBookStorage visitorBookStorage = new VisitorBookStorage().borrowDate(UPDATED_BORROW_DATE).returnDate(UPDATED_RETURN_DATE);
        return visitorBookStorage;
    }

    @BeforeEach
    public void initTest() {
        visitorBookStorage = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedVisitorBookStorage != null) {
            visitorBookStorageRepository.delete(insertedVisitorBookStorage);
            insertedVisitorBookStorage = null;
        }
    }

    @Test
    @Transactional
    void createVisitorBookStorage() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the VisitorBookStorage
        VisitorBookStorageDTO visitorBookStorageDTO = visitorBookStorageMapper.toDto(visitorBookStorage);
        var returnedVisitorBookStorageDTO = om.readValue(
            restVisitorBookStorageMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitorBookStorageDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VisitorBookStorageDTO.class
        );

        // Validate the VisitorBookStorage in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVisitorBookStorage = visitorBookStorageMapper.toEntity(returnedVisitorBookStorageDTO);
        assertVisitorBookStorageUpdatableFieldsEquals(
            returnedVisitorBookStorage,
            getPersistedVisitorBookStorage(returnedVisitorBookStorage)
        );

        insertedVisitorBookStorage = returnedVisitorBookStorage;
    }

    @Test
    @Transactional
    void createVisitorBookStorageWithExistingId() throws Exception {
        // Create the VisitorBookStorage with an existing ID
        visitorBookStorage.setId(1L);
        VisitorBookStorageDTO visitorBookStorageDTO = visitorBookStorageMapper.toDto(visitorBookStorage);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVisitorBookStorageMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitorBookStorageDTO)))
            .andExpect(status().isBadRequest());

        // Validate the VisitorBookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVisitorBookStorages() throws Exception {
        // Initialize the database
        insertedVisitorBookStorage = visitorBookStorageRepository.saveAndFlush(visitorBookStorage);

        // Get all the visitorBookStorageList
        restVisitorBookStorageMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(visitorBookStorage.getId().intValue())))
            .andExpect(jsonPath("$.[*].borrowDate").value(hasItem(DEFAULT_BORROW_DATE.toString())))
            .andExpect(jsonPath("$.[*].returnDate").value(hasItem(DEFAULT_RETURN_DATE.toString())));
    }

    @Test
    @Transactional
    void getVisitorBookStorage() throws Exception {
        // Initialize the database
        insertedVisitorBookStorage = visitorBookStorageRepository.saveAndFlush(visitorBookStorage);

        // Get the visitorBookStorage
        restVisitorBookStorageMockMvc
            .perform(get(ENTITY_API_URL_ID, visitorBookStorage.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(visitorBookStorage.getId().intValue()))
            .andExpect(jsonPath("$.borrowDate").value(DEFAULT_BORROW_DATE.toString()))
            .andExpect(jsonPath("$.returnDate").value(DEFAULT_RETURN_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingVisitorBookStorage() throws Exception {
        // Get the visitorBookStorage
        restVisitorBookStorageMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVisitorBookStorage() throws Exception {
        // Initialize the database
        insertedVisitorBookStorage = visitorBookStorageRepository.saveAndFlush(visitorBookStorage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visitorBookStorage
        VisitorBookStorage updatedVisitorBookStorage = visitorBookStorageRepository.findById(visitorBookStorage.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVisitorBookStorage are not directly saved in db
        em.detach(updatedVisitorBookStorage);
        updatedVisitorBookStorage.borrowDate(UPDATED_BORROW_DATE).returnDate(UPDATED_RETURN_DATE);
        VisitorBookStorageDTO visitorBookStorageDTO = visitorBookStorageMapper.toDto(updatedVisitorBookStorage);

        restVisitorBookStorageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitorBookStorageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(visitorBookStorageDTO))
            )
            .andExpect(status().isOk());

        // Validate the VisitorBookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVisitorBookStorageToMatchAllProperties(updatedVisitorBookStorage);
    }

    @Test
    @Transactional
    void putNonExistingVisitorBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitorBookStorage.setId(longCount.incrementAndGet());

        // Create the VisitorBookStorage
        VisitorBookStorageDTO visitorBookStorageDTO = visitorBookStorageMapper.toDto(visitorBookStorage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitorBookStorageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitorBookStorageDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(visitorBookStorageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitorBookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVisitorBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitorBookStorage.setId(longCount.incrementAndGet());

        // Create the VisitorBookStorage
        VisitorBookStorageDTO visitorBookStorageDTO = visitorBookStorageMapper.toDto(visitorBookStorage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorBookStorageMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(visitorBookStorageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitorBookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVisitorBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitorBookStorage.setId(longCount.incrementAndGet());

        // Create the VisitorBookStorage
        VisitorBookStorageDTO visitorBookStorageDTO = visitorBookStorageMapper.toDto(visitorBookStorage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorBookStorageMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitorBookStorageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VisitorBookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVisitorBookStorageWithPatch() throws Exception {
        // Initialize the database
        insertedVisitorBookStorage = visitorBookStorageRepository.saveAndFlush(visitorBookStorage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visitorBookStorage using partial update
        VisitorBookStorage partialUpdatedVisitorBookStorage = new VisitorBookStorage();
        partialUpdatedVisitorBookStorage.setId(visitorBookStorage.getId());

        partialUpdatedVisitorBookStorage.returnDate(UPDATED_RETURN_DATE);

        restVisitorBookStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisitorBookStorage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVisitorBookStorage))
            )
            .andExpect(status().isOk());

        // Validate the VisitorBookStorage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVisitorBookStorageUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedVisitorBookStorage, visitorBookStorage),
            getPersistedVisitorBookStorage(visitorBookStorage)
        );
    }

    @Test
    @Transactional
    void fullUpdateVisitorBookStorageWithPatch() throws Exception {
        // Initialize the database
        insertedVisitorBookStorage = visitorBookStorageRepository.saveAndFlush(visitorBookStorage);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visitorBookStorage using partial update
        VisitorBookStorage partialUpdatedVisitorBookStorage = new VisitorBookStorage();
        partialUpdatedVisitorBookStorage.setId(visitorBookStorage.getId());

        partialUpdatedVisitorBookStorage.borrowDate(UPDATED_BORROW_DATE).returnDate(UPDATED_RETURN_DATE);

        restVisitorBookStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisitorBookStorage.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVisitorBookStorage))
            )
            .andExpect(status().isOk());

        // Validate the VisitorBookStorage in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVisitorBookStorageUpdatableFieldsEquals(
            partialUpdatedVisitorBookStorage,
            getPersistedVisitorBookStorage(partialUpdatedVisitorBookStorage)
        );
    }

    @Test
    @Transactional
    void patchNonExistingVisitorBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitorBookStorage.setId(longCount.incrementAndGet());

        // Create the VisitorBookStorage
        VisitorBookStorageDTO visitorBookStorageDTO = visitorBookStorageMapper.toDto(visitorBookStorage);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitorBookStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, visitorBookStorageDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(visitorBookStorageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitorBookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVisitorBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitorBookStorage.setId(longCount.incrementAndGet());

        // Create the VisitorBookStorage
        VisitorBookStorageDTO visitorBookStorageDTO = visitorBookStorageMapper.toDto(visitorBookStorage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorBookStorageMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(visitorBookStorageDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the VisitorBookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVisitorBookStorage() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitorBookStorage.setId(longCount.incrementAndGet());

        // Create the VisitorBookStorage
        VisitorBookStorageDTO visitorBookStorageDTO = visitorBookStorageMapper.toDto(visitorBookStorage);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorBookStorageMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(visitorBookStorageDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the VisitorBookStorage in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVisitorBookStorage() throws Exception {
        // Initialize the database
        insertedVisitorBookStorage = visitorBookStorageRepository.saveAndFlush(visitorBookStorage);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the visitorBookStorage
        restVisitorBookStorageMockMvc
            .perform(delete(ENTITY_API_URL_ID, visitorBookStorage.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return visitorBookStorageRepository.count();
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

    protected VisitorBookStorage getPersistedVisitorBookStorage(VisitorBookStorage visitorBookStorage) {
        return visitorBookStorageRepository.findById(visitorBookStorage.getId()).orElseThrow();
    }

    protected void assertPersistedVisitorBookStorageToMatchAllProperties(VisitorBookStorage expectedVisitorBookStorage) {
        assertVisitorBookStorageAllPropertiesEquals(expectedVisitorBookStorage, getPersistedVisitorBookStorage(expectedVisitorBookStorage));
    }

    protected void assertPersistedVisitorBookStorageToMatchUpdatableProperties(VisitorBookStorage expectedVisitorBookStorage) {
        assertVisitorBookStorageAllUpdatablePropertiesEquals(
            expectedVisitorBookStorage,
            getPersistedVisitorBookStorage(expectedVisitorBookStorage)
        );
    }
}
