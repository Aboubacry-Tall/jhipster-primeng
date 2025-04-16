package co.ats.wepapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicLong;

public class FeedTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));

    public static Feed getFeedSample1() {
        return new Feed()
            .id(1L)
            .name("name1")
            .url("url1")
            .siteName("siteName1")
            .description("description1")
            .category("category1")
            .faviconUrl("faviconUrl1");
    }

    public static Feed getFeedSample2() {
        return new Feed()
            .id(2L)
            .name("name2")
            .url("url2")
            .siteName("siteName2")
            .description("description2")
            .category("category2")
            .faviconUrl("faviconUrl2");
    }

    public static Feed getFeedRandomSampleGenerator() {
        return new Feed()
            .id(longCount.incrementAndGet())
            .name(UUID.randomUUID().toString())
            .url(UUID.randomUUID().toString())
            .siteName(UUID.randomUUID().toString())
            .description(UUID.randomUUID().toString())
            .category(UUID.randomUUID().toString())
            .faviconUrl(UUID.randomUUID().toString());
    }
}
