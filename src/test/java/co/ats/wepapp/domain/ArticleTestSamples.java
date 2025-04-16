package co.ats.wepapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class ArticleTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Article getArticleSample1() {
        return new Article().id(1L).title("title1").link("link1").author("author1").tags("tags1").coverImageUrl("coverImageUrl1").views(1);
    }

    public static Article getArticleSample2() {
        return new Article().id(2L).title("title2").link("link2").author("author2").tags("tags2").coverImageUrl("coverImageUrl2").views(2);
    }

    public static Article getArticleRandomSampleGenerator() {
        return new Article()
            .id(longCount.incrementAndGet())
            .title(UUID.randomUUID().toString())
            .link(UUID.randomUUID().toString())
            .author(UUID.randomUUID().toString())
            .tags(UUID.randomUUID().toString())
            .coverImageUrl(UUID.randomUUID().toString())
            .views(intCount.incrementAndGet());
    }
}
