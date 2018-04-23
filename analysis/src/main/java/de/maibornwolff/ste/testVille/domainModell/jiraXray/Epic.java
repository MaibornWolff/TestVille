package de.maibornwolff.ste.testVille.domainModell.jiraXray;

import de.maibornwolff.ste.testVille.common.CollectionVisitor;
import de.maibornwolff.ste.testVille.domainModell.ComposedItem;
import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.ItemTyp;

import java.util.Collection;
import java.util.function.Function;

public class Epic extends ComposedItem {

    public Epic(int localKey) {
        super(localKey);
    }

    @Override
    public ItemTyp getType() {
        return ItemTyp.EPIC;
    }

    public static Collection<Epic> extractEpics(Collection<Item> allExtractedItems) {
        return CollectionVisitor.filterAndMap(allExtractedItems, x -> x.getType() == ItemTyp.EPIC, x -> (Epic) x);
    }

    public static Collection<Epic> associateEpicsToTestCasesAndExtractIt(Collection<Item> items) {
        Collection<Epic> allEpics = CollectionVisitor.filterAndMap(items, x -> x.getType() == ItemTyp.EPIC, x -> (Epic) x);
        allEpics.forEach(x -> x.associateBelongingTestCases(items));
        return allEpics;
    }

    private void associateBelongingTestCases(Collection<Item> items) {
        Collection<JiraXrayTestCase> directAssociatedTestCases = this.extractDirectBelongingTestCases(items);
        Collection<JiraXrayTestCase> indirectAssociatedTestCases = this.extractIndirectBelongingTestCases(items);

        this.addAssociatedItems(directAssociatedTestCases);
        this.addAssociatedItems(indirectAssociatedTestCases);
    }

    private Collection<JiraXrayTestCase> extractDirectBelongingTestCases(Collection<Item> items) {
        Collection<JiraXrayTestCase> testCases = JiraXrayTestCase.extractTestCases(items);
        return CollectionVisitor.filterAndMap(testCases, this:: existsAssociationBetweenEpicUndTestCase, Function.identity());
    }

    private Collection<JiraXrayTestCase> extractIndirectBelongingTestCases(Collection<Item> items) {
        Collection<TestSet> testSets = TestSet.extractTestSets(items);
        Collection<TestSet> associatedTestSets = CollectionVisitor.filterAndMap(testSets,
                this::existsAssociationBetweenEpicUndTestSet,
                Function.identity());

        return CollectionVisitor.mapAndMerge(associatedTestSets, x -> x.extractBelongingTestCases(items));
    }

    private boolean existsAssociationBetweenEpicUndTestSet(Item testSet) {
        return testSet.getAssociatedItemKeys().contains(this.getKey());
    }

    private boolean existsAssociationBetweenEpicUndTestCase(JiraXrayTestCase test) {
        return test.getAssociatedItemKeys().contains(this.getKey());
    }
}