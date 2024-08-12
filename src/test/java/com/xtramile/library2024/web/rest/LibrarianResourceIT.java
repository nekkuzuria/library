package com.xtramile.library2024.web.rest;

import static com.xtramile.library2024.domain.LibrarianAsserts.*;
import static com.xtramile.library2024.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtramile.library2024.IntegrationTest;
import com.xtramile.library2024.domain.Librarian;
import com.xtramile.library2024.repository.LibrarianRepository;
import com.xtramile.library2024.service.dto.LibrarianDTO;
import com.xtramile.library2024.service.mapper.LibrarianMapper;
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
 * Integration tests for the {@link LibrarianResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class LibrarianResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/librarians";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private LibrarianRepository librarianRepository;

    @Autowired
    private LibrarianMapper librarianMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restLibrarianMockMvc;

    private Librarian librarian;

    private Librarian insertedLibrarian;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Librarian createEntity(EntityManager em) {
        Librarian librarian = new Librarian()
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .dateOfBirth(DEFAULT_DATE_OF_BIRTH);
        return librarian;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Librarian createUpdatedEntity(EntityManager em) {
        Librarian librarian = new Librarian()
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH);
        return librarian;
    }

    @BeforeEach
    public void initTest() {
        librarian = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedLibrarian != null) {
            librarianRepository.delete(insertedLibrarian);
            insertedLibrarian = null;
        }
    }

    @Test
    @Transactional
    void createLibrarian() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Librarian
        LibrarianDTO librarianDTO = librarianMapper.toDto(librarian);
        var returnedLibrarianDTO = om.readValue(
            restLibrarianMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(librarianDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            LibrarianDTO.class
        );

        // Validate the Librarian in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedLibrarian = librarianMapper.toEntity(returnedLibrarianDTO);
        assertLibrarianUpdatableFieldsEquals(returnedLibrarian, getPersistedLibrarian(returnedLibrarian));

        insertedLibrarian = returnedLibrarian;
    }

    @Test
    @Transactional
    void createLibrarianWithExistingId() throws Exception {
        // Create the Librarian with an existing ID
        librarian.setId(1L);
        LibrarianDTO librarianDTO = librarianMapper.toDto(librarian);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restLibrarianMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(librarianDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Librarian in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllLibrarians() throws Exception {
        // Initialize the database
        insertedLibrarian = librarianRepository.saveAndFlush(librarian);

        // Get all the librarianList
        restLibrarianMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(librarian.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(DEFAULT_DATE_OF_BIRTH.toString())));
    }

    @Test
    @Transactional
    void getLibrarian() throws Exception {
        // Initialize the database
        insertedLibrarian = librarianRepository.saveAndFlush(librarian);

        // Get the librarian
        restLibrarianMockMvc
            .perform(get(ENTITY_API_URL_ID, librarian.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(librarian.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.dateOfBirth").value(DEFAULT_DATE_OF_BIRTH.toString()));
    }

    @Test
    @Transactional
    void getNonExistingLibrarian() throws Exception {
        // Get the librarian
        restLibrarianMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingLibrarian() throws Exception {
        // Initialize the database
        insertedLibrarian = librarianRepository.saveAndFlush(librarian);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the librarian
        Librarian updatedLibrarian = librarianRepository.findById(librarian.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedLibrarian are not directly saved in db
        em.detach(updatedLibrarian);
        updatedLibrarian.name(UPDATED_NAME).email(UPDATED_EMAIL).phoneNumber(UPDATED_PHONE_NUMBER).dateOfBirth(UPDATED_DATE_OF_BIRTH);
        LibrarianDTO librarianDTO = librarianMapper.toDto(updatedLibrarian);

        restLibrarianMockMvc
            .perform(
                put(ENTITY_API_URL_ID, librarianDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(librarianDTO))
            )
            .andExpect(status().isOk());

        // Validate the Librarian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedLibrarianToMatchAllProperties(updatedLibrarian);
    }

    @Test
    @Transactional
    void putNonExistingLibrarian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        librarian.setId(longCount.incrementAndGet());

        // Create the Librarian
        LibrarianDTO librarianDTO = librarianMapper.toDto(librarian);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibrarianMockMvc
            .perform(
                put(ENTITY_API_URL_ID, librarianDTO.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(librarianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Librarian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchLibrarian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        librarian.setId(longCount.incrementAndGet());

        // Create the Librarian
        LibrarianDTO librarianDTO = librarianMapper.toDto(librarian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibrarianMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(librarianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Librarian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamLibrarian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        librarian.setId(longCount.incrementAndGet());

        // Create the Librarian
        LibrarianDTO librarianDTO = librarianMapper.toDto(librarian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibrarianMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(librarianDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Librarian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateLibrarianWithPatch() throws Exception {
        // Initialize the database
        insertedLibrarian = librarianRepository.saveAndFlush(librarian);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the librarian using partial update
        Librarian partialUpdatedLibrarian = new Librarian();
        partialUpdatedLibrarian.setId(librarian.getId());

        partialUpdatedLibrarian.name(UPDATED_NAME).email(UPDATED_EMAIL).phoneNumber(UPDATED_PHONE_NUMBER);

        restLibrarianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibrarian.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLibrarian))
            )
            .andExpect(status().isOk());

        // Validate the Librarian in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLibrarianUpdatableFieldsEquals(
            createUpdateProxyForBean(partialUpdatedLibrarian, librarian),
            getPersistedLibrarian(librarian)
        );
    }

    @Test
    @Transactional
    void fullUpdateLibrarianWithPatch() throws Exception {
        // Initialize the database
        insertedLibrarian = librarianRepository.saveAndFlush(librarian);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the librarian using partial update
        Librarian partialUpdatedLibrarian = new Librarian();
        partialUpdatedLibrarian.setId(librarian.getId());

        partialUpdatedLibrarian
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH);

        restLibrarianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedLibrarian.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedLibrarian))
            )
            .andExpect(status().isOk());

        // Validate the Librarian in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertLibrarianUpdatableFieldsEquals(partialUpdatedLibrarian, getPersistedLibrarian(partialUpdatedLibrarian));
    }

    @Test
    @Transactional
    void patchNonExistingLibrarian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        librarian.setId(longCount.incrementAndGet());

        // Create the Librarian
        LibrarianDTO librarianDTO = librarianMapper.toDto(librarian);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restLibrarianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, librarianDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(librarianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Librarian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchLibrarian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        librarian.setId(longCount.incrementAndGet());

        // Create the Librarian
        LibrarianDTO librarianDTO = librarianMapper.toDto(librarian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibrarianMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(librarianDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Librarian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamLibrarian() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        librarian.setId(longCount.incrementAndGet());

        // Create the Librarian
        LibrarianDTO librarianDTO = librarianMapper.toDto(librarian);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restLibrarianMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(librarianDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Librarian in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteLibrarian() throws Exception {
        // Initialize the database
        insertedLibrarian = librarianRepository.saveAndFlush(librarian);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the librarian
        restLibrarianMockMvc
            .perform(delete(ENTITY_API_URL_ID, librarian.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return librarianRepository.count();
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

    protected Librarian getPersistedLibrarian(Librarian librarian) {
        return librarianRepository.findById(librarian.getId()).orElseThrow();
    }

    protected void assertPersistedLibrarianToMatchAllProperties(Librarian expectedLibrarian) {
        assertLibrarianAllPropertiesEquals(expectedLibrarian, getPersistedLibrarian(expectedLibrarian));
    }

    protected void assertPersistedLibrarianToMatchUpdatableProperties(Librarian expectedLibrarian) {
        assertLibrarianAllUpdatablePropertiesEquals(expectedLibrarian, getPersistedLibrarian(expectedLibrarian));
    }
}
