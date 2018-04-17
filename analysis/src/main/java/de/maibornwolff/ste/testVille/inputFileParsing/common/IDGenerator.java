package de.maibornwolff.ste.testVille.inputFileParsing.common;

import java.util.concurrent.atomic.AtomicInteger;

public class IDGenerator {

    private AtomicInteger keyCentral;

    public IDGenerator() {
        this.keyCentral = new AtomicInteger(1);
    }

    public int generateNextUniqueKey() {
        return this.keyCentral.getAndIncrement();
    }
}