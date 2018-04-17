package de.maibornwolff.ste.testVille.domainModell.jiraXray;

import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.ItemTyp;

import java.util.Collection;
import java.util.stream.Collectors;

public class TestExecution extends Item {

    public TestExecution(int localKey) {super(localKey);}

    @Override
    public ItemTyp getType() {
        return ItemTyp.TESTEXECUTION;
    }

    public static Collection<TestExecution> extractTestExecutions(Collection<Item> allExtractedItems) {
        return allExtractedItems
                .stream()
                .filter(x -> x.getType() == ItemTyp.TESTEXECUTION)
                .map(x -> (TestExecution) x)
                .collect(Collectors.toList());
    }

}
