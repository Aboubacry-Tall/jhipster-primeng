package co.ats.wepapp.service.impl;

import co.ats.wepapp.domain.Article;
import co.ats.wepapp.repository.ArticleRepository;
import co.ats.wepapp.service.ArticleService;
import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service Implementation for managing {@link co.ats.wepapp.domain.Article}.
 */
@Service
@Transactional
public class ArticleServiceImpl implements ArticleService {

    private static final Logger LOG = LoggerFactory.getLogger(ArticleServiceImpl.class);

    private final ArticleRepository articleRepository;

    public ArticleServiceImpl(ArticleRepository articleRepository) {
        this.articleRepository = articleRepository;
    }

    @Override
    public Article save(Article article) {
        LOG.debug("Request to save Article : {}", article);
        return articleRepository.save(article);
    }

    @Override
    public Article update(Article article) {
        LOG.debug("Request to update Article : {}", article);
        return articleRepository.save(article);
    }

    @Override
    public Optional<Article> partialUpdate(Article article) {
        LOG.debug("Request to partially update Article : {}", article);

        return articleRepository
            .findById(article.getId())
            .map(existingArticle -> {
                if (article.getTitle() != null) {
                    existingArticle.setTitle(article.getTitle());
                }
                if (article.getLink() != null) {
                    existingArticle.setLink(article.getLink());
                }
                if (article.getContent() != null) {
                    existingArticle.setContent(article.getContent());
                }
                if (article.getSummary() != null) {
                    existingArticle.setSummary(article.getSummary());
                }
                if (article.getAuthor() != null) {
                    existingArticle.setAuthor(article.getAuthor());
                }
                if (article.getPublishedDate() != null) {
                    existingArticle.setPublishedDate(article.getPublishedDate());
                }
                if (article.getRead() != null) {
                    existingArticle.setRead(article.getRead());
                }
                if (article.getFavorited() != null) {
                    existingArticle.setFavorited(article.getFavorited());
                }
                if (article.getTags() != null) {
                    existingArticle.setTags(article.getTags());
                }
                if (article.getCoverImageUrl() != null) {
                    existingArticle.setCoverImageUrl(article.getCoverImageUrl());
                }
                if (article.getViews() != null) {
                    existingArticle.setViews(article.getViews());
                }

                return existingArticle;
            })
            .map(articleRepository::save);
    }

    @Override
    @Transactional(readOnly = true)
    public Page<Article> findAll(Pageable pageable) {
        LOG.debug("Request to get all Articles");
        return articleRepository.findAll(pageable);
    }

    @Override
    @Transactional(readOnly = true)
    public Optional<Article> findOne(Long id) {
        LOG.debug("Request to get Article : {}", id);
        return articleRepository.findById(id);
    }

    @Override
    public void delete(Long id) {
        LOG.debug("Request to delete Article : {}", id);
        articleRepository.deleteById(id);
    }
}
