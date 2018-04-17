package de.maibornwolff.ste.testVille.domainModell.jiraXray;

import de.maibornwolff.ste.testVille.common.CollectionVisitor;
import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.ItemTyp;

import java.util.ArrayList;
import java.util.Collection;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class TestSet extends Item {

    public TestSet(int localKey) {super(localKey);}

    @Override
    public ItemTyp getType() {
        return ItemTyp.TESTSET;
    }

    public static Collection<TestSet> extractLooseTestSets(Collection<Item> allItems) {
        Collection<String> mergedKeysFromItemAssociatedToEpics = Epic.extractEpics(allItems)
                .stream()
                .map(Item::getAssociatedItemKeys)
                .reduce(new ArrayList<>(), (x, y) -> {x.addAll(y); return x;});

        return allItems
                .stream()
                .filter(x->x.getType() == ItemTyp.TESTSET && (!mergedKeysFromItemAssociatedToEpics.contains(x.getKey())))
                .map(x -> (TestSet) x)
                .collect(Collectors.toList());
    }

    public Collection<JiraXrayTestCase> extractBelongingTestCases(Collection<Item> allItems) {
        Predicate<Item> shouldBeATestCase   = x -> x.getType() == ItemTyp.TESTCASE;
        Predicate<Item> shouldBelongTestSet = x -> x.getAssociatedItemKeys().contains(this.getKey());
        return CollectionVisitor.filterAndMap(allItems, shouldBeATestCase.and(shouldBelongTestSet), x -> (JiraXrayTestCase) x);
    }

    public static Collection<TestSet> extractTestSets(Collection<Item> items) {
        return CollectionVisitor.filterAndMap(items, x -> x.getType() == ItemTyp.TESTSET, x -> (TestSet) x);
    }
}