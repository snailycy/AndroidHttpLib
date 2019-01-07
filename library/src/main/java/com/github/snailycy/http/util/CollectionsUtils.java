package com.github.snailycy.http.util;

import java.util.Collection;
import java.util.Map;

public class CollectionsUtils {

    public static <T extends Collection> boolean isEmpty(T t) {
        if (t != null && t.size() > 0) {
            return false;
        }
        return true;
    }

    public static <T extends Map> boolean isEmpty(T t) {
        if (t == null || t.isEmpty()) {
            return true;
        }
        return false;
    }
}
