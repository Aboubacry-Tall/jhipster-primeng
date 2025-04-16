package co.ats.wepapp.repository;

import co.ats.wepapp.domain.Feed;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the Feed entity.
 */
@SuppressWarnings("unused")
@Repository
public interface FeedRepository extends JpaRepository<Feed, Long> {}
