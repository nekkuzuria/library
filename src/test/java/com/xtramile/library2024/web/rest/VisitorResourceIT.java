package com.xtramile.library2024.web.rest;

import static com.xtramile.library2024.domain.VisitorAsserts.*;
import static com.xtramile.library2024.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.xtramile.library2024.IntegrationTest;
import com.xtramile.library2024.domain.Visitor;
import com.xtramile.library2024.repository.UserRepository;
import com.xtramile.library2024.repository.VisitorRepository;
import com.xtramile.library2024.service.VisitorService;
import com.xtramile.library2024.service.dto.VisitorDTO;
import com.xtramile.library2024.service.mapper.VisitorMapper;
import jakarta.persistence.EntityManager;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration tests for the {@link VisitorResource} REST controller.
 */
@IntegrationTest
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@WithMockUser
class VisitorResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_EMAIL = "AAAAAAAAAA";
    private static final String UPDATED_EMAIL = "BBBBBBBBBB";

    private static final String DEFAULT_PHONE_NUMBER = "AAAAAAAAAA";
    private static final String UPDATED_PHONE_NUMBER = "BBBBBBBBBB";

    private static final LocalDate DEFAULT_DATE_OF_BIRTH = LocalDate.ofEpochDay(0L);
    private static final LocalDate UPDATED_DATE_OF_BIRTH = LocalDate.now(ZoneId.systemDefault());

    private static final Boolean DEFAULT_MEMBERSHIP_STATUS = false;
    private static final Boolean UPDATED_MEMBERSHIP_STATUS = true;

    private static final String ENTITY_API_URL = "/api/visitors";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private VisitorRepository visitorRepository;

    @Autowired
    private UserRepository userRepository;

    @Mock
    private VisitorRepository visitorRepositoryMock;

    @Autowired
    private VisitorMapper visitorMapper;

    @Mock
    private VisitorService visitorServiceMock;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restVisitorMockMvc;

    private Visitor visitor;

    private Visitor insertedVisitor;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visitor createEntity(EntityManager em) {
        Visitor visitor = new Visitor()
            .name(DEFAULT_NAME)
            .email(DEFAULT_EMAIL)
            .phoneNumber(DEFAULT_PHONE_NUMBER)
            .dateOfBirth(DEFAULT_DATE_OF_BIRTH)
            .membershipStatus(DEFAULT_MEMBERSHIP_STATUS);
        return visitor;
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Visitor createUpdatedEntity(EntityManager em) {
        Visitor visitor = new Visitor()
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .membershipStatus(UPDATED_MEMBERSHIP_STATUS);
        return visitor;
    }

    @BeforeEach
    public void initTest() {
        visitor = createEntity(em);
    }

    @AfterEach
    public void cleanup() {
        if (insertedVisitor != null) {
            visitorRepository.delete(insertedVisitor);
            insertedVisitor = null;
        }
    }

    @Test
    @Transactional
    void createVisitor() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Visitor
        VisitorDTO visitorDTO = visitorMapper.toDto(visitor);
        var returnedVisitorDTO = om.readValue(
            restVisitorMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitorDTO)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            VisitorDTO.class
        );

        // Validate the Visitor in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        var returnedVisitor = visitorMapper.toEntity(returnedVisitorDTO);
        assertVisitorUpdatableFieldsEquals(returnedVisitor, getPersistedVisitor(returnedVisitor));

        insertedVisitor = returnedVisitor;
    }

    @Test
    @Transactional
    void createVisitorWithExistingId() throws Exception {
        // Create the Visitor with an existing ID
        visitor.setId(1L);
        VisitorDTO visitorDTO = visitorMapper.toDto(visitor);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restVisitorMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitorDTO)))
            .andExpect(status().isBadRequest());

        // Validate the Visitor in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void getAllVisitors() throws Exception {
        // Initialize the database
        insertedVisitor = visitorRepository.saveAndFlush(visitor);

        // Get all the visitorList
        restVisitorMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(visitor.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].email").value(hasItem(DEFAULT_EMAIL)))
            .andExpect(jsonPath("$.[*].phoneNumber").value(hasItem(DEFAULT_PHONE_NUMBER)))
            .andExpect(jsonPath("$.[*].dateOfBirth").value(hasItem(DEFAULT_DATE_OF_BIRTH.toString())))
            .andExpect(jsonPath("$.[*].membershipStatus").value(hasItem(DEFAULT_MEMBERSHIP_STATUS.booleanValue())));
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVisitorsWithEagerRelationshipsIsEnabled() throws Exception {
        when(visitorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVisitorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=true")).andExpect(status().isOk());

        verify(visitorServiceMock, times(1)).findAllWithEagerRelationships(any());
    }

    @SuppressWarnings({ "unchecked" })
    void getAllVisitorsWithEagerRelationshipsIsNotEnabled() throws Exception {
        when(visitorServiceMock.findAllWithEagerRelationships(any())).thenReturn(new PageImpl(new ArrayList<>()));

        restVisitorMockMvc.perform(get(ENTITY_API_URL + "?eagerload=false")).andExpect(status().isOk());
        verify(visitorRepositoryMock, times(1)).findAll(any(Pageable.class));
    }

    @Test
    @Transactional
    void getVisitor() throws Exception {
        // Initialize the database
        insertedVisitor = visitorRepository.saveAndFlush(visitor);

        // Get the visitor
        restVisitorMockMvc
            .perform(get(ENTITY_API_URL_ID, visitor.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(visitor.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.email").value(DEFAULT_EMAIL))
            .andExpect(jsonPath("$.phoneNumber").value(DEFAULT_PHONE_NUMBER))
            .andExpect(jsonPath("$.dateOfBirth").value(DEFAULT_DATE_OF_BIRTH.toString()))
            .andExpect(jsonPath("$.membershipStatus").value(DEFAULT_MEMBERSHIP_STATUS.booleanValue()));
    }

    @Test
    @Transactional
    void getNonExistingVisitor() throws Exception {
        // Get the visitor
        restVisitorMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingVisitor() throws Exception {
        // Initialize the database
        insertedVisitor = visitorRepository.saveAndFlush(visitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visitor
        Visitor updatedVisitor = visitorRepository.findById(visitor.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedVisitor are not directly saved in db
        em.detach(updatedVisitor);
        updatedVisitor
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .membershipStatus(UPDATED_MEMBERSHIP_STATUS);
        VisitorDTO visitorDTO = visitorMapper.toDto(updatedVisitor);

        restVisitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitorDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitorDTO))
            )
            .andExpect(status().isOk());

        // Validate the Visitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedVisitorToMatchAllProperties(updatedVisitor);
    }

    @Test
    @Transactional
    void putNonExistingVisitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitor.setId(longCount.incrementAndGet());

        // Create the Visitor
        VisitorDTO visitorDTO = visitorMapper.toDto(visitor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, visitorDTO.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchVisitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitor.setId(longCount.incrementAndGet());

        // Create the Visitor
        VisitorDTO visitorDTO = visitorMapper.toDto(visitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(visitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamVisitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitor.setId(longCount.incrementAndGet());

        // Create the Visitor
        VisitorDTO visitorDTO = visitorMapper.toDto(visitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(visitorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Visitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateVisitorWithPatch() throws Exception {
        // Initialize the database
        insertedVisitor = visitorRepository.saveAndFlush(visitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visitor using partial update
        Visitor partialUpdatedVisitor = new Visitor();
        partialUpdatedVisitor.setId(visitor.getId());

        partialUpdatedVisitor
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .membershipStatus(UPDATED_MEMBERSHIP_STATUS);

        restVisitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisitor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVisitor))
            )
            .andExpect(status().isOk());

        // Validate the Visitor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVisitorUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedVisitor, visitor), getPersistedVisitor(visitor));
    }

    @Test
    @Transactional
    void fullUpdateVisitorWithPatch() throws Exception {
        // Initialize the database
        insertedVisitor = visitorRepository.saveAndFlush(visitor);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the visitor using partial update
        Visitor partialUpdatedVisitor = new Visitor();
        partialUpdatedVisitor.setId(visitor.getId());

        partialUpdatedVisitor
            .name(UPDATED_NAME)
            .email(UPDATED_EMAIL)
            .phoneNumber(UPDATED_PHONE_NUMBER)
            .dateOfBirth(UPDATED_DATE_OF_BIRTH)
            .membershipStatus(UPDATED_MEMBERSHIP_STATUS);

        restVisitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedVisitor.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedVisitor))
            )
            .andExpect(status().isOk());

        // Validate the Visitor in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertVisitorUpdatableFieldsEquals(partialUpdatedVisitor, getPersistedVisitor(partialUpdatedVisitor));
    }

    @Test
    @Transactional
    void patchNonExistingVisitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitor.setId(longCount.incrementAndGet());

        // Create the Visitor
        VisitorDTO visitorDTO = visitorMapper.toDto(visitor);

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, visitorDTO.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(visitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchVisitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitor.setId(longCount.incrementAndGet());

        // Create the Visitor
        VisitorDTO visitorDTO = visitorMapper.toDto(visitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(visitorDTO))
            )
            .andExpect(status().isBadRequest());

        // Validate the Visitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamVisitor() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        visitor.setId(longCount.incrementAndGet());

        // Create the Visitor
        VisitorDTO visitorDTO = visitorMapper.toDto(visitor);

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restVisitorMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(visitorDTO)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Visitor in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteVisitor() throws Exception {
        // Initialize the database
        insertedVisitor = visitorRepository.saveAndFlush(visitor);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the visitor
        restVisitorMockMvc
            .perform(delete(ENTITY_API_URL_ID, visitor.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return visitorRepository.count();
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

    protected Visitor getPersistedVisitor(Visitor visitor) {
        return visitorRepository.findById(visitor.getId()).orElseThrow();
    }

    protected void assertPersistedVisitorToMatchAllProperties(Visitor expectedVisitor) {
        assertVisitorAllPropertiesEquals(expectedVisitor, getPersistedVisitor(expectedVisitor));
    }

    protected void assertPersistedVisitorToMatchUpdatableProperties(Visitor expectedVisitor) {
        assertVisitorAllUpdatablePropertiesEquals(expectedVisitor, getPersistedVisitor(expectedVisitor));
    }
}
