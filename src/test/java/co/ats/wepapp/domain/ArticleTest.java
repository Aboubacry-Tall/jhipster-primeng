package co.ats.wepapp.domain;

import static co.ats.wepapp.domain.ArticleTestSamples.*;
import static co.ats.wepapp.domain.FeedTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.ats.wepapp.web.rest.TestUtil;
import org.junit.jupiter.api.Test;

class ArticleTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Article.class);
        Article article1 = getArticleSample1();
        Article article2 = new Article();
        assertThat(article1).isNotEqualTo(article2);

        article2.setId(article1.getId());
        assertThat(article1).isEqualTo(article2);

        article2 = getArticleSample2();
        assertThat(article1).isNotEqualTo(article2);
    }

    @Test
    void feedTest() {
        Article article = getArticleRandomSampleGenerator();
        Feed feedBack = getFeedRandomSampleGenerator();

        article.setFeed(feedBack);
        assertThat(article.getFeed()).isEqualTo(feedBack);

        article.feed(null);
        assertThat(article.getFeed()).isNull();
    }
}
