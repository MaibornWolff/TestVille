package de.maibornwolff.ste.testVille.domainModell.hpALM;

import de.maibornwolff.ste.testVille.domainModell.ComposedItem;
import de.maibornwolff.ste.testVille.domainModell.ItemTyp;

public class Requirement extends ComposedItem {

    public Requirement(int localKey) {
        super(localKey);
    }

    @Override
    public ItemTyp getType() {
        return ItemTyp.REQUIREMENT;
    }
}