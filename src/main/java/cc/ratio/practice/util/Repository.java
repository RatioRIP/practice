package cc.ratio.practice.util;

import java.util.Optional;

public interface Repository<T, I> {

    boolean put(T t);

    boolean remove(T t);

    Optional<T> find(I identifier);

}
