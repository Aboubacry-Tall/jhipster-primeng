package co.ats.wepapp.web.rest;

import co.ats.wepapp.domain.Feed;
import co.ats.wepapp.repository.FeedRepository;
import co.ats.wepapp.service.FeedService;
import co.ats.wepapp.web.rest.errors.BadRequestAlertException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;
import tech.jhipster.web.util.HeaderUtil;
import tech.jhipster.web.util.PaginationUtil;
import tech.jhipster.web.util.ResponseUtil;

/**
 * REST controller for managing {@link co.ats.wepapp.domain.Feed}.
 */
@RestController
@RequestMapping("/api/feeds")
public class FeedResource {

    private static final Logger LOG = LoggerFactory.getLogger(FeedResource.class);

    private static final String ENTITY_NAME = "feed";

    @Value("${jhipster.clientApp.name}")
    private String applicationName;

    private final FeedService feedService;

    private final FeedRepository feedRepository;

    public FeedResource(FeedService feedService, FeedRepository feedRepository) {
        this.feedService = feedService;
        this.feedRepository = feedRepository;
    }

    /**
     * {@code POST  /feeds} : Create a new feed.
     *
     * @param feed the feed to create.
     * @return the {@link ResponseEntity} with status {@code 201 (Created)} and with body the new feed, or with status {@code 400 (Bad Request)} if the feed has already an ID.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PostMapping("")
    public ResponseEntity<Feed> createFeed(@Valid @RequestBody Feed feed) throws URISyntaxException {
        LOG.debug("REST request to save Feed : {}", feed);
        if (feed.getId() != null) {
            throw new BadRequestAlertException("A new feed cannot already have an ID", ENTITY_NAME, "idexists");
        }
        feed = feedService.save(feed);
        return ResponseEntity.created(new URI("/api/feeds/" + feed.getId()))
            .headers(HeaderUtil.createEntityCreationAlert(applicationName, true, ENTITY_NAME, feed.getId().toString()))
            .body(feed);
    }

    /**
     * {@code PUT  /feeds/:id} : Updates an existing feed.
     *
     * @param id the id of the feed to save.
     * @param feed the feed to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feed,
     * or with status {@code 400 (Bad Request)} if the feed is not valid,
     * or with status {@code 500 (Internal Server Error)} if the feed couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PutMapping("/{id}")
    public ResponseEntity<Feed> updateFeed(@PathVariable(value = "id", required = false) final Long id, @Valid @RequestBody Feed feed)
        throws URISyntaxException {
        LOG.debug("REST request to update Feed : {}, {}", id, feed);
        if (feed.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feed.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        feed = feedService.update(feed);
        return ResponseEntity.ok()
            .headers(HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feed.getId().toString()))
            .body(feed);
    }

    /**
     * {@code PATCH  /feeds/:id} : Partial updates given fields of an existing feed, field will ignore if it is null
     *
     * @param id the id of the feed to save.
     * @param feed the feed to update.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the updated feed,
     * or with status {@code 400 (Bad Request)} if the feed is not valid,
     * or with status {@code 404 (Not Found)} if the feed is not found,
     * or with status {@code 500 (Internal Server Error)} if the feed couldn't be updated.
     * @throws URISyntaxException if the Location URI syntax is incorrect.
     */
    @PatchMapping(value = "/{id}", consumes = { "application/json", "application/merge-patch+json" })
    public ResponseEntity<Feed> partialUpdateFeed(
        @PathVariable(value = "id", required = false) final Long id,
        @NotNull @RequestBody Feed feed
    ) throws URISyntaxException {
        LOG.debug("REST request to partial update Feed partially : {}, {}", id, feed);
        if (feed.getId() == null) {
            throw new BadRequestAlertException("Invalid id", ENTITY_NAME, "idnull");
        }
        if (!Objects.equals(id, feed.getId())) {
            throw new BadRequestAlertException("Invalid ID", ENTITY_NAME, "idinvalid");
        }

        if (!feedRepository.existsById(id)) {
            throw new BadRequestAlertException("Entity not found", ENTITY_NAME, "idnotfound");
        }

        Optional<Feed> result = feedService.partialUpdate(feed);

        return ResponseUtil.wrapOrNotFound(
            result,
            HeaderUtil.createEntityUpdateAlert(applicationName, true, ENTITY_NAME, feed.getId().toString())
        );
    }

    /**
     * {@code GET  /feeds} : get all the feeds.
     *
     * @param pageable the pagination information.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and the list of feeds in body.
     */
    @GetMapping("")
    public ResponseEntity<List<Feed>> getAllFeeds(@org.springdoc.core.annotations.ParameterObject Pageable pageable) {
        LOG.debug("REST request to get a page of Feeds");
        Page<Feed> page = feedService.findAll(pageable);
        HttpHeaders headers = PaginationUtil.generatePaginationHttpHeaders(ServletUriComponentsBuilder.fromCurrentRequest(), page);
        return ResponseEntity.ok().headers(headers).body(page.getContent());
    }

    /**
     * {@code GET  /feeds/:id} : get the "id" feed.
     *
     * @param id the id of the feed to retrieve.
     * @return the {@link ResponseEntity} with status {@code 200 (OK)} and with body the feed, or with status {@code 404 (Not Found)}.
     */
    @GetMapping("/{id}")
    public ResponseEntity<Feed> getFeed(@PathVariable("id") Long id) {
        LOG.debug("REST request to get Feed : {}", id);
        Optional<Feed> feed = feedService.findOne(id);
        return ResponseUtil.wrapOrNotFound(feed);
    }

    /**
     * {@code DELETE  /feeds/:id} : delete the "id" feed.
     *
     * @param id the id of the feed to delete.
     * @return the {@link ResponseEntity} with status {@code 204 (NO_CONTENT)}.
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFeed(@PathVariable("id") Long id) {
        LOG.debug("REST request to delete Feed : {}", id);
        feedService.delete(id);
        return ResponseEntity.noContent()
            .headers(HeaderUtil.createEntityDeletionAlert(applicationName, true, ENTITY_NAME, id.toString()))
            .build();
    }
}
