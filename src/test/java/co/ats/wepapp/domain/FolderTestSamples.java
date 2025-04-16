package co.ats.wepapp.domain;

import java.util.Random;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;

public class FolderTestSamples {

    private static final Random random = new Random();
    private static final AtomicLong longCount = new AtomicLong(random.nextInt() + (2 * Integer.MAX_VALUE));
    private static final AtomicInteger intCount = new AtomicInteger(random.nextInt() + (2 * Short.MAX_VALUE));

    public static Folder getFolderSample1() {
        return new Folder().id(1L).name("name1").order(1);
    }

    public static Folder getFolderSample2() {
        return new Folder().id(2L).name("name2").order(2);
    }

    public static Folder getFolderRandomSampleGenerator() {
        return new Folder().id(longCount.incrementAndGet()).name(UUID.randomUUID().toString()).order(intCount.incrementAndGet());
    }
}
