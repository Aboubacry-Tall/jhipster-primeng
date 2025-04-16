package co.ats.wepapp.web.rest;

import static co.ats.wepapp.domain.FolderAsserts.*;
import static co.ats.wepapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.ats.wepapp.IntegrationTest;
import co.ats.wepapp.domain.Folder;
import co.ats.wepapp.repository.FolderRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
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
 * Integration tests for the {@link FolderResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FolderResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final Integer DEFAULT_ORDER = 1;
    private static final Integer UPDATED_ORDER = 2;

    private static final String ENTITY_API_URL = "/api/folders";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FolderRepository folderRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFolderMockMvc;

    private Folder folder;

    private Folder insertedFolder;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Folder createEntity() {
        return new Folder().name(DEFAULT_NAME).order(DEFAULT_ORDER);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Folder createUpdatedEntity() {
        return new Folder().name(UPDATED_NAME).order(UPDATED_ORDER);
    }

    @BeforeEach
    public void initTest() {
        folder = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedFolder != null) {
            folderRepository.delete(insertedFolder);
            insertedFolder = null;
        }
    }

    @Test
    @Transactional
    void createFolder() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Folder
        var returnedFolder = om.readValue(
            restFolderMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(folder)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Folder.class
        );

        // Validate the Folder in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertFolderUpdatableFieldsEquals(returnedFolder, getPersistedFolder(returnedFolder));

        insertedFolder = returnedFolder;
    }

    @Test
    @Transactional
    void createFolderWithExistingId() throws Exception {
        // Create the Folder with an existing ID
        folder.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFolderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(folder)))
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        folder.setName(null);

        // Create the Folder, which fails.

        restFolderMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(folder)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFolders() throws Exception {
        // Initialize the database
        insertedFolder = folderRepository.saveAndFlush(folder);

        // Get all the folderList
        restFolderMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(folder.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].order").value(hasItem(DEFAULT_ORDER)));
    }

    @Test
    @Transactional
    void getFolder() throws Exception {
        // Initialize the database
        insertedFolder = folderRepository.saveAndFlush(folder);

        // Get the folder
        restFolderMockMvc
            .perform(get(ENTITY_API_URL_ID, folder.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(folder.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.order").value(DEFAULT_ORDER));
    }

    @Test
    @Transactional
    void getNonExistingFolder() throws Exception {
        // Get the folder
        restFolderMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFolder() throws Exception {
        // Initialize the database
        insertedFolder = folderRepository.saveAndFlush(folder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the folder
        Folder updatedFolder = folderRepository.findById(folder.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFolder are not directly saved in db
        em.detach(updatedFolder);
        updatedFolder.name(UPDATED_NAME).order(UPDATED_ORDER);

        restFolderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFolder.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedFolder))
            )
            .andExpect(status().isOk());

        // Validate the Folder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFolderToMatchAllProperties(updatedFolder);
    }

    @Test
    @Transactional
    void putNonExistingFolder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        folder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(put(ENTITY_API_URL_ID, folder.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(folder)))
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFolder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        folder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(folder))
            )
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFolder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        folder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(folder)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Folder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFolderWithPatch() throws Exception {
        // Initialize the database
        insertedFolder = folderRepository.saveAndFlush(folder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the folder using partial update
        Folder partialUpdatedFolder = new Folder();
        partialUpdatedFolder.setId(folder.getId());

        partialUpdatedFolder.name(UPDATED_NAME);

        restFolderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFolder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFolder))
            )
            .andExpect(status().isOk());

        // Validate the Folder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFolderUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFolder, folder), getPersistedFolder(folder));
    }

    @Test
    @Transactional
    void fullUpdateFolderWithPatch() throws Exception {
        // Initialize the database
        insertedFolder = folderRepository.saveAndFlush(folder);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the folder using partial update
        Folder partialUpdatedFolder = new Folder();
        partialUpdatedFolder.setId(folder.getId());

        partialUpdatedFolder.name(UPDATED_NAME).order(UPDATED_ORDER);

        restFolderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFolder.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFolder))
            )
            .andExpect(status().isOk());

        // Validate the Folder in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFolderUpdatableFieldsEquals(partialUpdatedFolder, getPersistedFolder(partialUpdatedFolder));
    }

    @Test
    @Transactional
    void patchNonExistingFolder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        folder.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, folder.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(folder))
            )
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFolder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        folder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(folder))
            )
            .andExpect(status().isBadRequest());

        // Validate the Folder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFolder() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        folder.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFolderMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(folder)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Folder in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFolder() throws Exception {
        // Initialize the database
        insertedFolder = folderRepository.saveAndFlush(folder);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the folder
        restFolderMockMvc
            .perform(delete(ENTITY_API_URL_ID, folder.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return folderRepository.count();
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

    protected Folder getPersistedFolder(Folder folder) {
        return folderRepository.findById(folder.getId()).orElseThrow();
    }

    protected void assertPersistedFolderToMatchAllProperties(Folder expectedFolder) {
        assertFolderAllPropertiesEquals(expectedFolder, getPersistedFolder(expectedFolder));
    }

    protected void assertPersistedFolderToMatchUpdatableProperties(Folder expectedFolder) {
        assertFolderAllUpdatablePropertiesEquals(expectedFolder, getPersistedFolder(expectedFolder));
    }
}
