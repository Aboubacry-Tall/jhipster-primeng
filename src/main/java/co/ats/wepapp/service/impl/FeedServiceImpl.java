package co.ats.wepapp.service.impl;

import co.ats.wepapp.domain.Feed;
import co.ats.wepapp.repository.FeedRepository;
import co.ats.wepapp.service.FeedService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.ats.wepapp.domain.Feed}.
 */
@Service
@Transactional
public class FeedServiceImpl implements FeedService {

    private static final Logger LOG = LoggerFactory.getLogger(FeedServiceImpl.class);

    private final FeedRepository feedRepository;

    public FeedServiceImpl(FeedRepository feedRepository) {
        this.feedRepository = feedRepository;
    }

    @Override
    public Feed save(Feed feed) {
        LOG.debug("Request to save Feed : {}", feed);
        return feedRepository.save(feed);
    }

    @Override
    public Feed update(Feed feed) {
        LOG.debug("Request to update Feed : {}", feed);
        return feedRepository.save(feed);
    }

    @Override
    public Optional<Feed> partialUpdate(Feed feed) {
        LOG.debug("Request to partially update Feed : {}", feed);

        return feedRepository
            .findById(feed.getId())
            .map(existingFeed -> {
                if (feed.getName() != null) {
                    existingFeed.setName(feed.getName());
                }
                if (feed.getUrl() != null) {
                    existingFeed.setUrl(feed.getUrl());
                }
                if (feed.getSiteName() != null) {
                    existingFeed.setSiteName(feed.getSiteName());
                }
                if (feed.getDescription() != null) {
                    existingFeed.setDescription(feed.getDescription());
                }
                if (feed.getCategory() != null) {
                    existingFeed.setCategory(feed.getCategory());
                }
                if (feed.getAutoDiscovered() != null) {
                    existingFeed.setAutoDiscovered(feed.getAutoDiscovered());
                }
                if (feed.getFaviconUrl() != null) {
                    existingFeed.setFaviconUrl(feed.getFaviconUrl());
                }
                if (feed.getActive() != null) {
                    existingFeed.setActive(feed.getActive());
                }
                if (feed.getCreatedDate() != null) {
                    existingFeed.setCreatedDate(feed.getCreatedDate());
                }
                if (feed.getLastFetchedDate() != null) {
                    existingFeed.setLastFetchedDate(feed.getLastFetchedDate());
                }

                return existingFeed;
            })
            .map(feedRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Feed> findAll(Pageable pageable) {
        LOG.debug("Request to get all Feeds");
        return feedRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Feed> findOne(Long id) {
        LOG.debug("Request to get Feed : {}", id);
        return feedRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Feed : {}", id);
        feedRepository.deleteById(id);
    }
}
