package de.maibornwolff.ste.testVille.domainModell.jiraXray;

import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.ItemTyp;

public class TestExecution extends Item {

    public TestExecution(int localKey) {super(localKey);}

    @Override
    public ItemTyp getItemTyp() {
        return ItemTyp.TESTEXECUTION;
    }

}
