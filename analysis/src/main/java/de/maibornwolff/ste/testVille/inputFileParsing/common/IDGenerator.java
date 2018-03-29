package de.maibornwolff.ste.testVille.inputFileParsing.common;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {

    final AtomicInteger keyCentral;

    public IDGenerator() {
        this.keyCentral = new AtomicInteger();
    }

    public int generateNextUniqueKey() {
        return this.keyCentral.getAndIncrement();
    }
}
