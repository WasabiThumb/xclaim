package io.github.wasabithumb.xclaim.util;

import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.EnumMap;
import java.util.function.Function;

@ApiStatus.Internal
public class MapUtil {

    public static <A extends Enum<A>, B> @NotNull EnumMap<A, B> enums(
            Class<A> aClass,
            Function<A, B> mapper
    ) {
        EnumMap<A, B> map = new EnumMap<>(aClass);
        for (A a : aClass.getEnumConstants()) {
            map.put(a, mapper.apply(a));
        }
        return map;
    }

    public static <A extends Enum<A>, B extends Enum<B>> @NotNull EnumMap<A, B> enums(
            Class<A> aClass,
            Class<B> bClass
    ) {
        return enums(aClass, (A a) -> Enum.valueOf(bClass, a.name()));
    }

}
