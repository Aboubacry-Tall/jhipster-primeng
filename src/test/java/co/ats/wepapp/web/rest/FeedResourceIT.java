package co.ats.wepapp.web.rest;

import static co.ats.wepapp.domain.FeedAsserts.*;
import static co.ats.wepapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.ats.wepapp.IntegrationTest;
import co.ats.wepapp.domain.Feed;
import co.ats.wepapp.repository.FeedRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
 * Integration tests for the {@link FeedResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class FeedResourceIT {

    private static final String DEFAULT_NAME = "AAAAAAAAAA";
    private static final String UPDATED_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_URL = "AAAAAAAAAA";
    private static final String UPDATED_URL = "BBBBBBBBBB";

    private static final String DEFAULT_SITE_NAME = "AAAAAAAAAA";
    private static final String UPDATED_SITE_NAME = "BBBBBBBBBB";

    private static final String DEFAULT_DESCRIPTION = "AAAAAAAAAA";
    private static final String UPDATED_DESCRIPTION = "BBBBBBBBBB";

    private static final String DEFAULT_CATEGORY = "AAAAAAAAAA";
    private static final String UPDATED_CATEGORY = "BBBBBBBBBB";

    private static final Boolean DEFAULT_AUTO_DISCOVERED = false;
    private static final Boolean UPDATED_AUTO_DISCOVERED = true;

    private static final String DEFAULT_FAVICON_URL = "AAAAAAAAAA";
    private static final String UPDATED_FAVICON_URL = "BBBBBBBBBB";

    private static final Boolean DEFAULT_ACTIVE = false;
    private static final Boolean UPDATED_ACTIVE = true;

    private static final Instant DEFAULT_CREATED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_CREATED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Instant DEFAULT_LAST_FETCHED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_LAST_FETCHED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final String ENTITY_API_URL = "/api/feeds";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private FeedRepository feedRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restFeedMockMvc;

    private Feed feed;

    private Feed insertedFeed;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Feed createEntity() {
        return new Feed()
            .name(DEFAULT_NAME)
            .url(DEFAULT_URL)
            .siteName(DEFAULT_SITE_NAME)
            .description(DEFAULT_DESCRIPTION)
            .category(DEFAULT_CATEGORY)
            .autoDiscovered(DEFAULT_AUTO_DISCOVERED)
            .faviconUrl(DEFAULT_FAVICON_URL)
            .active(DEFAULT_ACTIVE)
            .createdDate(DEFAULT_CREATED_DATE)
            .lastFetchedDate(DEFAULT_LAST_FETCHED_DATE);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Feed createUpdatedEntity() {
        return new Feed()
            .name(UPDATED_NAME)
            .url(UPDATED_URL)
            .siteName(UPDATED_SITE_NAME)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .autoDiscovered(UPDATED_AUTO_DISCOVERED)
            .faviconUrl(UPDATED_FAVICON_URL)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastFetchedDate(UPDATED_LAST_FETCHED_DATE);
    }

    @BeforeEach
    public void initTest() {
        feed = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedFeed != null) {
            feedRepository.delete(insertedFeed);
            insertedFeed = null;
        }
    }

    @Test
    @Transactional
    void createFeed() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Feed
        var returnedFeed = om.readValue(
            restFeedMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feed)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Feed.class
        );

        // Validate the Feed in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertFeedUpdatableFieldsEquals(returnedFeed, getPersistedFeed(returnedFeed));

        insertedFeed = returnedFeed;
    }

    @Test
    @Transactional
    void createFeedWithExistingId() throws Exception {
        // Create the Feed with an existing ID
        feed.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restFeedMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feed)))
            .andExpect(status().isBadRequest());

        // Validate the Feed in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkNameIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        feed.setName(null);

        // Create the Feed, which fails.

        restFeedMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feed)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkUrlIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        feed.setUrl(null);

        // Create the Feed, which fails.

        restFeedMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feed)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllFeeds() throws Exception {
        // Initialize the database
        insertedFeed = feedRepository.saveAndFlush(feed);

        // Get all the feedList
        restFeedMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(feed.getId().intValue())))
            .andExpect(jsonPath("$.[*].name").value(hasItem(DEFAULT_NAME)))
            .andExpect(jsonPath("$.[*].url").value(hasItem(DEFAULT_URL)))
            .andExpect(jsonPath("$.[*].siteName").value(hasItem(DEFAULT_SITE_NAME)))
            .andExpect(jsonPath("$.[*].description").value(hasItem(DEFAULT_DESCRIPTION)))
            .andExpect(jsonPath("$.[*].category").value(hasItem(DEFAULT_CATEGORY)))
            .andExpect(jsonPath("$.[*].autoDiscovered").value(hasItem(DEFAULT_AUTO_DISCOVERED)))
            .andExpect(jsonPath("$.[*].faviconUrl").value(hasItem(DEFAULT_FAVICON_URL)))
            .andExpect(jsonPath("$.[*].active").value(hasItem(DEFAULT_ACTIVE)))
            .andExpect(jsonPath("$.[*].createdDate").value(hasItem(DEFAULT_CREATED_DATE.toString())))
            .andExpect(jsonPath("$.[*].lastFetchedDate").value(hasItem(DEFAULT_LAST_FETCHED_DATE.toString())));
    }

    @Test
    @Transactional
    void getFeed() throws Exception {
        // Initialize the database
        insertedFeed = feedRepository.saveAndFlush(feed);

        // Get the feed
        restFeedMockMvc
            .perform(get(ENTITY_API_URL_ID, feed.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(feed.getId().intValue()))
            .andExpect(jsonPath("$.name").value(DEFAULT_NAME))
            .andExpect(jsonPath("$.url").value(DEFAULT_URL))
            .andExpect(jsonPath("$.siteName").value(DEFAULT_SITE_NAME))
            .andExpect(jsonPath("$.description").value(DEFAULT_DESCRIPTION))
            .andExpect(jsonPath("$.category").value(DEFAULT_CATEGORY))
            .andExpect(jsonPath("$.autoDiscovered").value(DEFAULT_AUTO_DISCOVERED))
            .andExpect(jsonPath("$.faviconUrl").value(DEFAULT_FAVICON_URL))
            .andExpect(jsonPath("$.active").value(DEFAULT_ACTIVE))
            .andExpect(jsonPath("$.createdDate").value(DEFAULT_CREATED_DATE.toString()))
            .andExpect(jsonPath("$.lastFetchedDate").value(DEFAULT_LAST_FETCHED_DATE.toString()));
    }

    @Test
    @Transactional
    void getNonExistingFeed() throws Exception {
        // Get the feed
        restFeedMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingFeed() throws Exception {
        // Initialize the database
        insertedFeed = feedRepository.saveAndFlush(feed);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feed
        Feed updatedFeed = feedRepository.findById(feed.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedFeed are not directly saved in db
        em.detach(updatedFeed);
        updatedFeed
            .name(UPDATED_NAME)
            .url(UPDATED_URL)
            .siteName(UPDATED_SITE_NAME)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .autoDiscovered(UPDATED_AUTO_DISCOVERED)
            .faviconUrl(UPDATED_FAVICON_URL)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastFetchedDate(UPDATED_LAST_FETCHED_DATE);

        restFeedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedFeed.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedFeed))
            )
            .andExpect(status().isOk());

        // Validate the Feed in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedFeedToMatchAllProperties(updatedFeed);
    }

    @Test
    @Transactional
    void putNonExistingFeed() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feed.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeedMockMvc
            .perform(put(ENTITY_API_URL_ID, feed.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feed)))
            .andExpect(status().isBadRequest());

        // Validate the Feed in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchFeed() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feed.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(feed))
            )
            .andExpect(status().isBadRequest());

        // Validate the Feed in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamFeed() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feed.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(feed)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Feed in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateFeedWithPatch() throws Exception {
        // Initialize the database
        insertedFeed = feedRepository.saveAndFlush(feed);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feed using partial update
        Feed partialUpdatedFeed = new Feed();
        partialUpdatedFeed.setId(feed.getId());

        partialUpdatedFeed
            .name(UPDATED_NAME)
            .siteName(UPDATED_SITE_NAME)
            .description(UPDATED_DESCRIPTION)
            .autoDiscovered(UPDATED_AUTO_DISCOVERED)
            .faviconUrl(UPDATED_FAVICON_URL)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastFetchedDate(UPDATED_LAST_FETCHED_DATE);

        restFeedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeed.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFeed))
            )
            .andExpect(status().isOk());

        // Validate the Feed in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFeedUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedFeed, feed), getPersistedFeed(feed));
    }

    @Test
    @Transactional
    void fullUpdateFeedWithPatch() throws Exception {
        // Initialize the database
        insertedFeed = feedRepository.saveAndFlush(feed);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the feed using partial update
        Feed partialUpdatedFeed = new Feed();
        partialUpdatedFeed.setId(feed.getId());

        partialUpdatedFeed
            .name(UPDATED_NAME)
            .url(UPDATED_URL)
            .siteName(UPDATED_SITE_NAME)
            .description(UPDATED_DESCRIPTION)
            .category(UPDATED_CATEGORY)
            .autoDiscovered(UPDATED_AUTO_DISCOVERED)
            .faviconUrl(UPDATED_FAVICON_URL)
            .active(UPDATED_ACTIVE)
            .createdDate(UPDATED_CREATED_DATE)
            .lastFetchedDate(UPDATED_LAST_FETCHED_DATE);

        restFeedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedFeed.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedFeed))
            )
            .andExpect(status().isOk());

        // Validate the Feed in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertFeedUpdatableFieldsEquals(partialUpdatedFeed, getPersistedFeed(partialUpdatedFeed));
    }

    @Test
    @Transactional
    void patchNonExistingFeed() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feed.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restFeedMockMvc
            .perform(patch(ENTITY_API_URL_ID, feed.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(feed)))
            .andExpect(status().isBadRequest());

        // Validate the Feed in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchFeed() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feed.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(feed))
            )
            .andExpect(status().isBadRequest());

        // Validate the Feed in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamFeed() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        feed.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restFeedMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(feed)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Feed in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteFeed() throws Exception {
        // Initialize the database
        insertedFeed = feedRepository.saveAndFlush(feed);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the feed
        restFeedMockMvc
            .perform(delete(ENTITY_API_URL_ID, feed.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return feedRepository.count();
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

    protected Feed getPersistedFeed(Feed feed) {
        return feedRepository.findById(feed.getId()).orElseThrow();
    }

    protected void assertPersistedFeedToMatchAllProperties(Feed expectedFeed) {
        assertFeedAllPropertiesEquals(expectedFeed, getPersistedFeed(expectedFeed));
    }

    protected void assertPersistedFeedToMatchUpdatableProperties(Feed expectedFeed) {
        assertFeedAllUpdatablePropertiesEquals(expectedFeed, getPersistedFeed(expectedFeed));
    }
}
