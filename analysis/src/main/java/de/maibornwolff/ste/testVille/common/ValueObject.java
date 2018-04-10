package de.maibornwolff.ste.testVille.common;

import java.util.Comparator;
import java.util.Objects;

public abstract class ValueObject<A> implements Comparable{
    public A content;

    public ValueObject(A content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ValueObject<?> that = (ValueObject<?>) o;
        return Objects.equals(content, that.content);
    }

    @Override
    public int hashCode() {
        return Objects.hash(content);
    }

    public boolean containsNullContent() {
        return this.content == null;
    }
}
