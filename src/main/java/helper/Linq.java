package helper;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class Linq {
    public static <T> Collection<List<T>> Chunk(List<T> enumerable, int chunkSize) {
        AtomicInteger counter = new AtomicInteger();
        return enumerable.stream().collect(Collectors.groupingBy(group -> counter.getAndIncrement() / chunkSize)).values();
    }
}