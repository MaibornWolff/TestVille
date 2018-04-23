package de.maibornwolff.ste.testVille.common;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class CollectionVisitor {

    public static  <A, B> Collection<B> filterAndMap(Collection<A> elements, Predicate<A> predicate, Function<A, B> with) {
        if(elements == null) return new ArrayList<>();
        return elements
                .stream()
                .filter(predicate)
                .map(with)
                .collect(Collectors.toList());
    }

    public static <A, B> Collection<B> mapAndMerge(Collection<A> elements, Function<A, Collection<B>> with) {
        return elements
                .stream()
                .map(with)
                .reduce(new ArrayList<>(), (x, y) -> {x.addAll(y); return x;});
    }
}
