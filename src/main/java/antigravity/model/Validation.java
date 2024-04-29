package antigravity.model;

import java.util.List;

public class Validation {
    public static <T> boolean isNull(T data) {
        return data == null;
    }

    public static boolean isNegative(Integer data) {
        return data <= 0;
    }

    public static boolean isNegative(Long data) {
        return data <= 0L;
    }

    public static <T> boolean isEmptyList(List<T> list) {
        return list.isEmpty();
    }
}
