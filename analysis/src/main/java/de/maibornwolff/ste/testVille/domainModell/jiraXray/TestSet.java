package de.maibornwolff.ste.testVille.domainModell.jiraXray;

import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.ItemTyp;

public class TestSet extends Item {

    public TestSet(int localKey) {super(localKey);}

    @Override
    public ItemTyp getItemTyp() {
        return ItemTyp.TESTSET;
    }

}
