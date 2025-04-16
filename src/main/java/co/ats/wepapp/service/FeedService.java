package co.ats.wepapp.service;

import co.ats.wepapp.domain.Feed;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

/**
 * Service Interface for managing {@link co.ats.wepapp.domain.Feed}.
 */
public interface FeedService {
    /**
     * Save a feed.
     *
     * @param feed the entity to save.
     * @return the persisted entity.
     */
    Feed save(Feed feed);

    /**
     * Updates a feed.
     *
     * @param feed the entity to update.
     * @return the persisted entity.
     */
    Feed update(Feed feed);

    /**
     * Partially updates a feed.
     *
     * @param feed the entity to update partially.
     * @return the persisted entity.
     */
    Optional<Feed> partialUpdate(Feed feed);

    /**
     * Get all the feeds.
     *
     * @param pageable the pagination information.
     * @return the list of entities.
     */
    Page<Feed> findAll(Pageable pageable);

    /**
     * Get the "id" feed.
     *
     * @param id the id of the entity.
     * @return the entity.
     */
    Optional<Feed> findOne(Long id);

    /**
     * Delete the "id" feed.
     *
     * @param id the id of the entity.
     */
    void delete(Long id);
}
