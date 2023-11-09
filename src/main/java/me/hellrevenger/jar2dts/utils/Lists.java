package me.hellrevenger.jar2dts.utils;

import java.util.ArrayList;
import java.util.List;

public class Lists {
    @SafeVarargs
    public static <T> ArrayList<T> from(T... elements) {
        return new ArrayList<>(List.of(elements));
    }
}
