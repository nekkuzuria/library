package com.xtramile.library2024.web.rest;

import static com.xtramile.library2024.domain.VisitAsserts.*;
import static com.xtramile.library2024.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtramile.library2024.IntegrationTest;
import com.xtramile.library2024.domain.Visit;
import com.xtramile.library2024.repository.VisitRepository;
import com.xtramile.library2024.service.dto.VisitDTO;
import com.xtramile.library2024.service.mapper.VisitMapper;
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
 * Integration tests for the {@link VisitResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class VisitResourceIT {

    private static final LocalDate DEFAULT_DATE = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE = LocalDate.now(ZoneId.systemDefault());

    private static final String ENTITY_API_URL = "/api/visits";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VisitRepository visitRepository;

    @Autowired
    private VisitMapper visitMapper;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVisitMockMvc;

    private Visit visit;

    private Visit insertedVisit;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visit createEntity(EntityManager em) {
        Visit visit = new Visit().date(DEFAULT_DATE);
        return visit;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visit createUpdatedEntity(EntityManager em) {
        Visit visit = new Visit().date(UPDATED_DATE);
        return visit;
    }

    @BeforeEach
    public void initTest() {
        visit = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedVisit != null) {
            visitRepository.delete(insertedVisit);
            insertedVisit = null;
        }
    }

    @Test
    @Transactional
    void createVisit() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);
        var returnedVisitDTO = om.readValue(
            restVisitMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VisitDTO.class
        );

        // Validate the Visit in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVisit = visitMapper.toEntity(returnedVisitDTO);
        assertVisitUpdatableFieldsEquals(returnedVisit, getPersistedVisit(returnedVisit));

        insertedVisit = returnedVisit;
    }

    @Test
    @Transactional
    void createVisitWithExistingId() throws Exception {
        // Create the Visit with an existing ID
        visit.setId(1L);
        VisitDTO visitDTO = visitMapper.toDto(visit);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVisitMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVisits() throws Exception {
        // Initialize the database
        insertedVisit = visitRepository.saveAndFlush(visit);

        // Get all the visitList
        restVisitMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(visit.getId().intValue())))
            .andExpect(jsonPath("$.[*].date").value(hasItem(DEFAULT_DATE.toString())));
    }

    @Test
    @Transactional
    void getVisit() throws Exception {
        // Initialize the database
        insertedVisit = visitRepository.saveAndFlush(visit);

        // Get the visit
        restVisitMockMvc
            .perform(get(ENTITY_API_URL_ID, visit.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(visit.getId().intValue()))
            .andExpect(jsonPath("$.date").value(DEFAULT_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingVisit() throws Exception {
        // Get the visit
        restVisitMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVisit() throws Exception {
        // Initialize the database
        insertedVisit = visitRepository.saveAndFlush(visit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visit
        Visit updatedVisit = visitRepository.findById(visit.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVisit are not directly saved in db
        em.detach(updatedVisit);
        updatedVisit.date(UPDATED_DATE);
        VisitDTO visitDTO = visitMapper.toDto(updatedVisit);

        restVisitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitDTO))
            )
            .andExpect(status().isOk());

        // Validate the Visit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVisitToMatchAllProperties(updatedVisit);
    }

    @Test
    @Transactional
    void putNonExistingVisit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visit.setId(longCount.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVisit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visit.setId(longCount.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVisit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visit.setId(longCount.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Visit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVisitWithPatch() throws Exception {
        // Initialize the database
        insertedVisit = visitRepository.saveAndFlush(visit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visit using partial update
        Visit partialUpdatedVisit = new Visit();
        partialUpdatedVisit.setId(visit.getId());

        partialUpdatedVisit.date(UPDATED_DATE);

        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVisit))
            )
            .andExpect(status().isOk());

        // Validate the Visit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVisitUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVisit, visit), getPersistedVisit(visit));
    }

    @Test
    @Transactional
    void fullUpdateVisitWithPatch() throws Exception {
        // Initialize the database
        insertedVisit = visitRepository.saveAndFlush(visit);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visit using partial update
        Visit partialUpdatedVisit = new Visit();
        partialUpdatedVisit.setId(visit.getId());

        partialUpdatedVisit.date(UPDATED_DATE);

        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisit.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVisit))
            )
            .andExpect(status().isOk());

        // Validate the Visit in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVisitUpdatableFieldsEquals(partialUpdatedVisit, getPersistedVisit(partialUpdatedVisit));
    }

    @Test
    @Transactional
    void patchNonExistingVisit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visit.setId(longCount.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, visitDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVisit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visit.setId(longCount.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(visitDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVisit() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visit.setId(longCount.incrementAndGet());

        // Create the Visit
        VisitDTO visitDTO = visitMapper.toDto(visit);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(visitDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Visit in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVisit() throws Exception {
        // Initialize the database
        insertedVisit = visitRepository.saveAndFlush(visit);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the visit
        restVisitMockMvc
            .perform(delete(ENTITY_API_URL_ID, visit.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return visitRepository.count();
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

    protected Visit getPersistedVisit(Visit visit) {
        return visitRepository.findById(visit.getId()).orElseThrow();
    }

    protected void assertPersistedVisitToMatchAllProperties(Visit expectedVisit) {
        assertVisitAllPropertiesEquals(expectedVisit, getPersistedVisit(expectedVisit));
    }

    protected void assertPersistedVisitToMatchUpdatableProperties(Visit expectedVisit) {
        assertVisitAllUpdatablePropertiesEquals(expectedVisit, getPersistedVisit(expectedVisit));
    }
}
