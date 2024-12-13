package me.qwqdev.egm.utils;

import lombok.experimental.UtilityClass;

import java.util.OptionalInt;
import java.util.Set;
import java.util.stream.IntStream;

/**
 * The type Math utils.
 *
 * @author qwq-dev
 * @since 2024-12-08 11:11
 */
@UtilityClass
public class MathUtils {
    /**
     * Find closest available optional int.
     *
     * @param min the min
     * @param max the max
     * @param set the set
     * @return the optional int
     */
    public static OptionalInt findClosestAvailable(int min, int max, Set<Integer> set) {
        return IntStream.rangeClosed(min, max)
                .filter(num -> !set.contains(num))
                .findFirst();
    }
}
