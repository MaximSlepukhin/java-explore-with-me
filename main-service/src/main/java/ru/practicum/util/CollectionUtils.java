package ru.practicum.util;

import java.util.List;

public class CollectionUtils {
    public static <T> List<T> getSublistByOffset(List<T> list, Integer offset, Integer size) {

        if (list.size() > offset) {
            return list.subList(offset, Math.min(offset + size, list.size()));
        } else {
            return List.of();
        }
    }
}
