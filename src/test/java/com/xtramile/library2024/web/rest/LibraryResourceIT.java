package com.xtramile.library2024.web.rest;

import static com.xtramile.library2024.domain.LibraryAsserts.*;
import static com.xtramile.library2024.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtramile.library2024.IntegrationTest;
import com.xtramile.library2024.domain.Library;
import com.xtramile.library2024.repository.LibraryRepository;
import com.xtramile.library2024.service.dto.LibraryDTO;
import com.xtramile.library2024.service.mapper.LibraryMapper;
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
 * Integration tests for the {@link LibraryResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LibraryResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_ESTABLISHED_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_ESTABLISHED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/libraries";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LibraryRepository libraryRepository;

    @Autowired
    private LibraryMapper libraryMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLibraryMockMvc;

    private Library library;

    private Library insertedLibrary;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Library createEntity(EntityManager em) {
        Library library = new Library().name(DEFAULT_NAME).establishedDate(DEFAULT_ESTABLISHED_DATE);
        return library;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Library createUpdatedEntity(EntityManager em) {
        Library library = new Library().name(UPDATED_NAME).establishedDate(UPDATED_ESTABLISHED_DATE);
        return library;
    }

    @BeforeEach
    public void initTest() {
        library = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedLibrary != null) {
            libraryRepository.delete(insertedLibrary);
            insertedLibrary = null;
        }
    }

    @Test
    @Transactional
    void createLibrary() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);
        var returnedLibraryDTO = om.readValue(
            restLibraryMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LibraryDTO.class
        );

        // Validate the Library in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLibrary = libraryMapper.toEntity(returnedLibraryDTO);
        assertLibraryUpdatableFieldsEquals(returnedLibrary, getPersistedLibrary(returnedLibrary));

        insertedLibrary = returnedLibrary;
    }

    @Test
    @Transactional
    void createLibraryWithExistingId() throws Exception {
        // Create the Library with an existing ID
        library.setId(1L);
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLibraryMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLibraries() throws Exception {
        // Initialize the database
        insertedLibrary = libraryRepository.saveAndFlush(library);

        // Get all the libraryList
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(library.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].establishedDate").value(hasItem(DEFAULT_ESTABLISHED_DATE.toString())));
    }

    @Test
    @Transactional
    void getLibrary() throws Exception {
        // Initialize the database
        insertedLibrary = libraryRepository.saveAndFlush(library);

        // Get the library
        restLibraryMockMvc
            .perform(get(ENTITY_API_URL_ID, library.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(library.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.establishedDate").value(DEFAULT_ESTABLISHED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLibrary() throws Exception {
        // Get the library
        restLibraryMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLibrary() throws Exception {
        // Initialize the database
        insertedLibrary = libraryRepository.saveAndFlush(library);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the library
        Library updatedLibrary = libraryRepository.findById(library.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLibrary are not directly saved in db
        em.detach(updatedLibrary);
        updatedLibrary.name(UPDATED_NAME).establishedDate(UPDATED_ESTABLISHED_DATE);
        LibraryDTO libraryDTO = libraryMapper.toDto(updatedLibrary);

        restLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, libraryDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryDTO))
            )
            .andExpect(status().isOk());

        // Validate the Library in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLibraryToMatchAllProperties(updatedLibrary);
    }

    @Test
    @Transactional
    void putNonExistingLibrary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        library.setId(longCount.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, libraryDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLibrary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        library.setId(longCount.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(libraryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLibrary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        library.setId(longCount.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(libraryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Library in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLibraryWithPatch() throws Exception {
        // Initialize the database
        insertedLibrary = libraryRepository.saveAndFlush(library);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the library using partial update
        Library partialUpdatedLibrary = new Library();
        partialUpdatedLibrary.setId(library.getId());

        partialUpdatedLibrary.establishedDate(UPDATED_ESTABLISHED_DATE);

        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibrary.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLibrary))
            )
            .andExpect(status().isOk());

        // Validate the Library in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLibraryUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedLibrary, library), getPersistedLibrary(library));
    }

    @Test
    @Transactional
    void fullUpdateLibraryWithPatch() throws Exception {
        // Initialize the database
        insertedLibrary = libraryRepository.saveAndFlush(library);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the library using partial update
        Library partialUpdatedLibrary = new Library();
        partialUpdatedLibrary.setId(library.getId());

        partialUpdatedLibrary.name(UPDATED_NAME).establishedDate(UPDATED_ESTABLISHED_DATE);

        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibrary.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLibrary))
            )
            .andExpect(status().isOk());

        // Validate the Library in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLibraryUpdatableFieldsEquals(partialUpdatedLibrary, getPersistedLibrary(partialUpdatedLibrary));
    }

    @Test
    @Transactional
    void patchNonExistingLibrary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        library.setId(longCount.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, libraryDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(libraryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLibrary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        library.setId(longCount.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(libraryDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Library in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLibrary() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        library.setId(longCount.incrementAndGet());

        // Create the Library
        LibraryDTO libraryDTO = libraryMapper.toDto(library);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibraryMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(libraryDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Library in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLibrary() throws Exception {
        // Initialize the database
        insertedLibrary = libraryRepository.saveAndFlush(library);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the library
        restLibraryMockMvc
            .perform(delete(ENTITY_API_URL_ID, library.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return libraryRepository.count();
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

    protected Library getPersistedLibrary(Library library) {
        return libraryRepository.findById(library.getId()).orElseThrow();
    }

    protected void assertPersistedLibraryToMatchAllProperties(Library expectedLibrary) {
        assertLibraryAllPropertiesEquals(expectedLibrary, getPersistedLibrary(expectedLibrary));
    }

    protected void assertPersistedLibraryToMatchUpdatableProperties(Library expectedLibrary) {
        assertLibraryAllUpdatablePropertiesEquals(expectedLibrary, getPersistedLibrary(expectedLibrary));
    }
}
