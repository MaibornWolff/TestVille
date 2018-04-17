package de.maibornwolff.ste.testVille.domainModell.jiraXray;

import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.ItemTyp;
import de.maibornwolff.ste.testVille.domainModell.TestCase;

import java.util.ArrayList;
import java.util.Collection;
import java.util.TreeSet;
import java.util.stream.Collectors;

public class JiraXrayTestCase extends TestCase {

    public JiraXrayTestCase(int localKey) {
        super(localKey);
    }

    public static Collection<JiraXrayTestCase> extractTestCases(Collection<Item> allItems) {

        //return CollectionVisitor.filterAndMap(allItems, x -> x.getType() == ItemTyp.TESTCASE, x -> (JiraXrayTestCase) x);
        return allItems
                .stream()
                .filter(x -> x.getType() == ItemTyp.TESTCASE)
                .map(x -> (JiraXrayTestCase) x)
                .collect(Collectors.toList());
    }

    public void addCountExecutionsAsMetric(Collection<Item> items) {
        Collection<TestExecution> allTestExecutions = TestExecution.extractTestExecutions(items);
        int countExecs = this.countTestCaseExecutions(allTestExecutions);
        this.addNewProperty("countExecutions", ""+countExecs);
    }

    private int countTestCaseExecutions(Collection<TestExecution> allKnownExecutions) {
        return allKnownExecutions
                .stream()
                .reduce(0,
                        (x, y) -> y.getAssociatedItemKeys().contains(this.getKey()) ? x+1 : x,
                        (x, y) -> x+y
                );
    }

    public static Collection<Item> extractLooseTestCases(Collection<Item> items) {
        Collection<Item> looseTestCases = new TreeSet<>();
        looseTestCases.addAll(extractDirectLooseTestCases  (items));
        looseTestCases.addAll(extractIndirectLooseTestCases(items));
        return looseTestCases;
    }

    private static Collection<JiraXrayTestCase> extractDirectLooseTestCases(Collection<Item> allExtractedItems) {
        Collection<String> alreadyAssociatedTestCaseKeys = Epic.extractEpics(allExtractedItems)
                .stream()
                .map(Item::getAssociatedItemKeys)
                .reduce(new ArrayList<>(), (x, y) -> {x.addAll(y); return x;});

        return allExtractedItems
                .stream()
                .filter(x->x.getType() == ItemTyp.TESTCASE && (!alreadyAssociatedTestCaseKeys.contains(x.getKey())))
                .map(x -> (JiraXrayTestCase) x)
                .collect(Collectors.toList());
    }

    private static Collection<JiraXrayTestCase> extractIndirectLooseTestCases(Collection<Item> allExtractedItems) {
        Collection<TestSet>   looseTestSets = TestSet.extractLooseTestSets(allExtractedItems);
        Collection<String> mergedKeysFromItemAssociatedToTestSets = looseTestSets
                .stream()
                .map(Item::getAssociatedItemKeys)
                .reduce(new ArrayList<>(), (x, y) -> {x.addAll(y); return x;});

        return allExtractedItems
                .stream()
                .filter(x->x.getType() == ItemTyp.TESTCASE && (!mergedKeysFromItemAssociatedToTestSets.contains(x.getKey())))
                .map(x -> (JiraXrayTestCase) x)
                .collect(Collectors.toList());
    }
}