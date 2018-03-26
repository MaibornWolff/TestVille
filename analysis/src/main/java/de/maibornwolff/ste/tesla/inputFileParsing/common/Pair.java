package de.maibornwolff.ste.tesla.inputFileParsing.common;

import java.util.Objects;
import java.util.function.Function;

public class Pair<A, B> {
    private A first;
    private B second;

    public Pair(A fst, B scd) {
        this.first = fst;
        this.second = scd;
    }

    public void setFirst(A first) {
        this.first = first;
    }

    public void applyFunctionToFirst(Function<A, A> fun) {
        this.first = fun.apply(this.getFirst());
    }

    public void applyFunctionToSecond(Function<B, B> fun) {
        this.second = fun.apply(this.getSecond());
    }


    public A getFirst() {
        return first;
    }

    public B getSecond() {
        return second;
    }

    @Override
    public String toString() {
        return "["+this.first.toString()+", "+this.second.toString()+"]";
    }

    @Override
    public boolean equals(Object obj){
        if(! (obj instanceof Pair)) return false;
        Pair pair = (Pair)obj;
        return (this.second.equals(pair.second)) && this.first.equals(pair.first);
    }

    @Override
    public int hashCode() {
        return Objects.hash(first, second);
    }
}