package co.ats.wepapp.domain;

import static co.ats.wepapp.domain.ArticleTestSamples.*;
import static co.ats.wepapp.domain.FeedTestSamples.*;
import static org.assertj.core.api.Assertions.assertThat;

import co.ats.wepapp.web.rest.TestUtil;
import java.util.HashSet;
import java.util.Set;
import org.junit.jupiter.api.Test;

class FeedTest {

    @Test
    void equalsVerifier() throws Exception {
        TestUtil.equalsVerifier(Feed.class);
        Feed feed1 = getFeedSample1();
        Feed feed2 = new Feed();
        assertThat(feed1).isNotEqualTo(feed2);

        feed2.setId(feed1.getId());
        assertThat(feed1).isEqualTo(feed2);

        feed2 = getFeedSample2();
        assertThat(feed1).isNotEqualTo(feed2);
    }

    @Test
    void articleTest() {
        Feed feed = getFeedRandomSampleGenerator();
        Article articleBack = getArticleRandomSampleGenerator();

        feed.addArticle(articleBack);
        assertThat(feed.getArticles()).containsOnly(articleBack);
        assertThat(articleBack.getFeed()).isEqualTo(feed);

        feed.removeArticle(articleBack);
        assertThat(feed.getArticles()).doesNotContain(articleBack);
        assertThat(articleBack.getFeed()).isNull();

        feed.articles(new HashSet<>(Set.of(articleBack)));
        assertThat(feed.getArticles()).containsOnly(articleBack);
        assertThat(articleBack.getFeed()).isEqualTo(feed);

        feed.setArticles(new HashSet<>());
        assertThat(feed.getArticles()).doesNotContain(articleBack);
        assertThat(articleBack.getFeed()).isNull();
    }
}
