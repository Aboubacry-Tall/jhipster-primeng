package co.ats.wepapp.web.rest;

import static co.ats.wepapp.domain.ArticleAsserts.*;
import static co.ats.wepapp.web.rest.TestUtil.createUpdateProxyForBean;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasItem;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import co.ats.wepapp.IntegrationTest;
import co.ats.wepapp.domain.Article;
import co.ats.wepapp.repository.ArticleRepository;
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
 * Integration tests for the {@link ArticleResource} REST controller.
 */
@IntegrationTest
@AutoConfigureMockMvc
@WithMockUser
class ArticleResourceIT {

    private static final String DEFAULT_TITLE = "AAAAAAAAAA";
    private static final String UPDATED_TITLE = "BBBBBBBBBB";

    private static final String DEFAULT_LINK = "AAAAAAAAAA";
    private static final String UPDATED_LINK = "BBBBBBBBBB";

    private static final String DEFAULT_CONTENT = "AAAAAAAAAA";
    private static final String UPDATED_CONTENT = "BBBBBBBBBB";

    private static final String DEFAULT_SUMMARY = "AAAAAAAAAA";
    private static final String UPDATED_SUMMARY = "BBBBBBBBBB";

    private static final String DEFAULT_AUTHOR = "AAAAAAAAAA";
    private static final String UPDATED_AUTHOR = "BBBBBBBBBB";

    private static final Instant DEFAULT_PUBLISHED_DATE = Instant.ofEpochMilli(0L);
    private static final Instant UPDATED_PUBLISHED_DATE = Instant.now().truncatedTo(ChronoUnit.MILLIS);

    private static final Boolean DEFAULT_READ = false;
    private static final Boolean UPDATED_READ = true;

    private static final Boolean DEFAULT_FAVORITED = false;
    private static final Boolean UPDATED_FAVORITED = true;

    private static final String DEFAULT_TAGS = "AAAAAAAAAA";
    private static final String UPDATED_TAGS = "BBBBBBBBBB";

    private static final String DEFAULT_COVER_IMAGE_URL = "AAAAAAAAAA";
    private static final String UPDATED_COVER_IMAGE_URL = "BBBBBBBBBB";

    private static final Integer DEFAULT_VIEWS = 1;
    private static final Integer UPDATED_VIEWS = 2;

    private static final String ENTITY_API_URL = "/api/articles";
    private static final String ENTITY_API_URL_ID = ENTITY_API_URL + "/{id}";

    private static Random random = new Random();
    private static AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    @Autowired
    private ObjectMapper om;

    @Autowired
    private ArticleRepository articleRepository;

    @Autowired
    private EntityManager em;

    @Autowired
    private MockMvc restArticleMockMvc;

    private Article article;

    private Article insertedArticle;

    /**
     * Create an entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Article createEntity() {
        return new Article()
            .title(DEFAULT_TITLE)
            .link(DEFAULT_LINK)
            .content(DEFAULT_CONTENT)
            .summary(DEFAULT_SUMMARY)
            .author(DEFAULT_AUTHOR)
            .publishedDate(DEFAULT_PUBLISHED_DATE)
            .read(DEFAULT_READ)
            .favorited(DEFAULT_FAVORITED)
            .tags(DEFAULT_TAGS)
            .coverImageUrl(DEFAULT_COVER_IMAGE_URL)
            .views(DEFAULT_VIEWS);
    }

    /**
     * Create an updated entity for this test.
     *
     * This is a static method, as tests for other entities might also need it,
     * if they test an entity which requires the current entity.
     */
    public static Article createUpdatedEntity() {
        return new Article()
            .title(UPDATED_TITLE)
            .link(UPDATED_LINK)
            .content(UPDATED_CONTENT)
            .summary(UPDATED_SUMMARY)
            .author(UPDATED_AUTHOR)
            .publishedDate(UPDATED_PUBLISHED_DATE)
            .read(UPDATED_READ)
            .favorited(UPDATED_FAVORITED)
            .tags(UPDATED_TAGS)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .views(UPDATED_VIEWS);
    }

    @BeforeEach
    public void initTest() {
        article = createEntity();
    }

    @AfterEach
    public void cleanup() {
        if (insertedArticle != null) {
            articleRepository.delete(insertedArticle);
            insertedArticle = null;
        }
    }

    @Test
    @Transactional
    void createArticle() throws Exception {
        long databaseSizeBeforeCreate = getRepositoryCount();
        // Create the Article
        var returnedArticle = om.readValue(
            restArticleMockMvc
                .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(article)))
                .andExpect(status().isCreated())
                .andReturn()
                .getResponse()
                .getContentAsString(),
            Article.class
        );

        // Validate the Article in the database
        assertIncrementedRepositoryCount(databaseSizeBeforeCreate);
        assertArticleUpdatableFieldsEquals(returnedArticle, getPersistedArticle(returnedArticle));

        insertedArticle = returnedArticle;
    }

    @Test
    @Transactional
    void createArticleWithExistingId() throws Exception {
        // Create the Article with an existing ID
        article.setId(1L);

        long databaseSizeBeforeCreate = getRepositoryCount();

        // An entity with an existing ID cannot be created, so this API call must fail
        restArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(article)))
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeCreate);
    }

    @Test
    @Transactional
    void checkTitleIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        article.setTitle(null);

        // Create the Article, which fails.

        restArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(article)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void checkLinkIsRequired() throws Exception {
        long databaseSizeBeforeTest = getRepositoryCount();
        // set the field null
        article.setLink(null);

        // Create the Article, which fails.

        restArticleMockMvc
            .perform(post(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(article)))
            .andExpect(status().isBadRequest());

        assertSameRepositoryCount(databaseSizeBeforeTest);
    }

    @Test
    @Transactional
    void getAllArticles() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        // Get all the articleList
        restArticleMockMvc
            .perform(get(ENTITY_API_URL + "?sort=id,desc"))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.[*].id").value(hasItem(article.getId().intValue())))
            .andExpect(jsonPath("$.[*].title").value(hasItem(DEFAULT_TITLE)))
            .andExpect(jsonPath("$.[*].link").value(hasItem(DEFAULT_LINK)))
            .andExpect(jsonPath("$.[*].content").value(hasItem(DEFAULT_CONTENT)))
            .andExpect(jsonPath("$.[*].summary").value(hasItem(DEFAULT_SUMMARY)))
            .andExpect(jsonPath("$.[*].author").value(hasItem(DEFAULT_AUTHOR)))
            .andExpect(jsonPath("$.[*].publishedDate").value(hasItem(DEFAULT_PUBLISHED_DATE.toString())))
            .andExpect(jsonPath("$.[*].read").value(hasItem(DEFAULT_READ)))
            .andExpect(jsonPath("$.[*].favorited").value(hasItem(DEFAULT_FAVORITED)))
            .andExpect(jsonPath("$.[*].tags").value(hasItem(DEFAULT_TAGS)))
            .andExpect(jsonPath("$.[*].coverImageUrl").value(hasItem(DEFAULT_COVER_IMAGE_URL)))
            .andExpect(jsonPath("$.[*].views").value(hasItem(DEFAULT_VIEWS)));
    }

    @Test
    @Transactional
    void getArticle() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        // Get the article
        restArticleMockMvc
            .perform(get(ENTITY_API_URL_ID, article.getId()))
            .andExpect(status().isOk())
            .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
            .andExpect(jsonPath("$.id").value(article.getId().intValue()))
            .andExpect(jsonPath("$.title").value(DEFAULT_TITLE))
            .andExpect(jsonPath("$.link").value(DEFAULT_LINK))
            .andExpect(jsonPath("$.content").value(DEFAULT_CONTENT))
            .andExpect(jsonPath("$.summary").value(DEFAULT_SUMMARY))
            .andExpect(jsonPath("$.author").value(DEFAULT_AUTHOR))
            .andExpect(jsonPath("$.publishedDate").value(DEFAULT_PUBLISHED_DATE.toString()))
            .andExpect(jsonPath("$.read").value(DEFAULT_READ))
            .andExpect(jsonPath("$.favorited").value(DEFAULT_FAVORITED))
            .andExpect(jsonPath("$.tags").value(DEFAULT_TAGS))
            .andExpect(jsonPath("$.coverImageUrl").value(DEFAULT_COVER_IMAGE_URL))
            .andExpect(jsonPath("$.views").value(DEFAULT_VIEWS));
    }

    @Test
    @Transactional
    void getNonExistingArticle() throws Exception {
        // Get the article
        restArticleMockMvc.perform(get(ENTITY_API_URL_ID, Long.MAX_VALUE)).andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void putExistingArticle() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the article
        Article updatedArticle = articleRepository.findById(article.getId()).orElseThrow();
        // Disconnect from session so that the updates on updatedArticle are not directly saved in db
        em.detach(updatedArticle);
        updatedArticle
            .title(UPDATED_TITLE)
            .link(UPDATED_LINK)
            .content(UPDATED_CONTENT)
            .summary(UPDATED_SUMMARY)
            .author(UPDATED_AUTHOR)
            .publishedDate(UPDATED_PUBLISHED_DATE)
            .read(UPDATED_READ)
            .favorited(UPDATED_FAVORITED)
            .tags(UPDATED_TAGS)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .views(UPDATED_VIEWS);

        restArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, updatedArticle.getId())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(updatedArticle))
            )
            .andExpect(status().isOk());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertPersistedArticleToMatchAllProperties(updatedArticle);
    }

    @Test
    @Transactional
    void putNonExistingArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(put(ENTITY_API_URL_ID, article.getId()).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(article)))
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithIdMismatchArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                put(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(om.writeValueAsBytes(article))
            )
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void putWithMissingIdPathParamArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(put(ENTITY_API_URL).contentType(MediaType.APPLICATION_JSON).content(om.writeValueAsBytes(article)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void partialUpdateArticleWithPatch() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the article using partial update
        Article partialUpdatedArticle = new Article();
        partialUpdatedArticle.setId(article.getId());

        partialUpdatedArticle
            .title(UPDATED_TITLE)
            .link(UPDATED_LINK)
            .content(UPDATED_CONTENT)
            .summary(UPDATED_SUMMARY)
            .read(UPDATED_READ)
            .favorited(UPDATED_FAVORITED)
            .tags(UPDATED_TAGS)
            .views(UPDATED_VIEWS);

        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedArticle))
            )
            .andExpect(status().isOk());

        // Validate the Article in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertArticleUpdatableFieldsEquals(createUpdateProxyForBean(partialUpdatedArticle, article), getPersistedArticle(article));
    }

    @Test
    @Transactional
    void fullUpdateArticleWithPatch() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        long databaseSizeBeforeUpdate = getRepositoryCount();

        // Update the article using partial update
        Article partialUpdatedArticle = new Article();
        partialUpdatedArticle.setId(article.getId());

        partialUpdatedArticle
            .title(UPDATED_TITLE)
            .link(UPDATED_LINK)
            .content(UPDATED_CONTENT)
            .summary(UPDATED_SUMMARY)
            .author(UPDATED_AUTHOR)
            .publishedDate(UPDATED_PUBLISHED_DATE)
            .read(UPDATED_READ)
            .favorited(UPDATED_FAVORITED)
            .tags(UPDATED_TAGS)
            .coverImageUrl(UPDATED_COVER_IMAGE_URL)
            .views(UPDATED_VIEWS);

        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, partialUpdatedArticle.getId())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(partialUpdatedArticle))
            )
            .andExpect(status().isOk());

        // Validate the Article in the database

        assertSameRepositoryCount(databaseSizeBeforeUpdate);
        assertArticleUpdatableFieldsEquals(partialUpdatedArticle, getPersistedArticle(partialUpdatedArticle));
    }

    @Test
    @Transactional
    void patchNonExistingArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // If the entity doesn't have an ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, article.getId()).contentType("application/merge-patch+json").content(om.writeValueAsBytes(article))
            )
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithIdMismatchArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(
                patch(ENTITY_API_URL_ID, longCount.incrementAndGet())
                    .contentType("application/merge-patch+json")
                    .content(om.writeValueAsBytes(article))
            )
            .andExpect(status().isBadRequest());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void patchWithMissingIdPathParamArticle() throws Exception {
        long databaseSizeBeforeUpdate = getRepositoryCount();
        article.setId(longCount.incrementAndGet());

        // If url ID doesn't match entity ID, it will throw BadRequestAlertException
        restArticleMockMvc
            .perform(patch(ENTITY_API_URL).contentType("application/merge-patch+json").content(om.writeValueAsBytes(article)))
            .andExpect(status().isMethodNotAllowed());

        // Validate the Article in the database
        assertSameRepositoryCount(databaseSizeBeforeUpdate);
    }

    @Test
    @Transactional
    void deleteArticle() throws Exception {
        // Initialize the database
        insertedArticle = articleRepository.saveAndFlush(article);

        long databaseSizeBeforeDelete = getRepositoryCount();

        // Delete the article
        restArticleMockMvc
            .perform(delete(ENTITY_API_URL_ID, article.getId()).accept(MediaType.APPLICATION_JSON))
            .andExpect(status().isNoContent());

        // Validate the database contains one less item
        assertDecrementedRepositoryCount(databaseSizeBeforeDelete);
    }

    protected long getRepositoryCount() {
        return articleRepository.count();
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

    protected Article getPersistedArticle(Article article) {
        return articleRepository.findById(article.getId()).orElseThrow();
    }

    protected void assertPersistedArticleToMatchAllProperties(Article expectedArticle) {
        assertArticleAllPropertiesEquals(expectedArticle, getPersistedArticle(expectedArticle));
    }

    protected void assertPersistedArticleToMatchUpdatableProperties(Article expectedArticle) {
        assertArticleAllUpdatablePropertiesEquals(expectedArticle, getPersistedArticle(expectedArticle));
    }
}
