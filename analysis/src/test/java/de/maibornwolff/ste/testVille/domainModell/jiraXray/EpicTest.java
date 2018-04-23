package de.maibornwolff.ste.testVille.domainModell.jiraXray;

import de.maibornwolff.ste.testVille.domainModell.ComposedItem;
import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.ItemTyp;
import de.maibornwolff.ste.testVille.domainModell.TestCase;
import de.maibornwolff.ste.testVille.domainModell.hpALM.Requirement;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class EpicTest {

    @DisplayName("Should return an empty Collection")
    @Test
    void filterEmptyCollection() {
        // arrange
        List<Item> list = new ArrayList<>();

        // act
        Collection<Epic> allEpics = Epic.extractEpics(list);

        //assert
        assertEquals(true, allEpics.isEmpty(), "The filtered Collection is not empty");
    }

    @DisplayName("Should return a collection of Epics")
    @Test
    void epicsExtraction() {
        // arrange
        Epic epic1   = (Epic)    createItem(ItemTyp.EPIC, 1, "ep01");
        Epic epic2   = (Epic)    createItem(ItemTyp.EPIC, 12, "ep02");
        TestCase tc1 = (TestCase) createItem(ItemTyp.TESTCASE, 11, "tc01", "ts01", "ep01");
        TestExecution te = (TestExecution) createItem(ItemTyp.TESTEXECUTION, 25, "te01");

        List<Item> list = List.of(epic1, tc1, te, epic2);
        List<Item> expected = List.of(epic1, epic2);


        // act
        Collection<Epic> actual = Epic.extractEpics(list);

        //assert
        assertEquals(expected, actual, "The filtered Collection is inconsistent");
    }

    @DisplayName("Should associate epics to their testcases ans return it")
    @Test
    void associationTest() {
        // arrange
        Epic epic1 = (Epic) createItem(ItemTyp.EPIC, 23, "ep01");
        Epic epic2 = (Epic) createItem(ItemTyp.EPIC, 232, "ep02");

        Epic epic3 = (Epic) createItem(ItemTyp.EPIC, 23, "ep01");
        Epic epic4 = (Epic) createItem(ItemTyp.EPIC, 232, "ep02");

        TestSet testSet = (TestSet) createItem(ItemTyp.TESTSET, 123, "ts01", "tc01", "tc03", "ep01");

        TestCase tc1 = (TestCase) createItem(ItemTyp.TESTCASE, 11, "tc01", "ts01", "ep01");
        TestCase tc2 = (TestCase) createItem(ItemTyp.TESTCASE, 12, "tc02", "ep01");
        TestCase tc3 = (TestCase) createItem(ItemTyp.TESTCASE, 13, "tc03", "ts01");
        TestCase tc4 = (TestCase) createItem(ItemTyp.TESTCASE, 24, "tc04", "ep02");

        TestExecution te = (TestExecution) createItem(ItemTyp.TESTEXECUTION, 25, "te01");

        Collection<Item> allItems = List.of(epic1, epic2, testSet, tc1, tc2, tc3, tc4, te);

        // act
        Collection<Epic> actual    = Epic.associateEpicsToTestCasesAndExtractIt(allItems);
        epic3.addAssociatedItems(tc1, tc2, tc3);
        epic4.addAssociatedItems(tc4);
        Collection<Epic> expected = List.of(epic3, epic4);

        // assert
        assertEquals(expected, actual, "Epics extraction after association with testCase failed");
    }

    private Item createItem(ItemTyp type, int localId, String key, String... associatedItemKeys) {
        Item result;
        switch (type) {
            case EPIC: result = new Epic(localId); break;
            case TESTSET: result = new TestSet(localId); break;
            case TESTCASE: result = new JiraXrayTestCase(localId); break;
            case TESTEXECUTION: result = new TestExecution(localId); break;
            case REQUIREMENT: result = new Requirement(localId); break;
            default: result = new ComposedItem(localId);
        }
        result.setPriority("high");
        result.setName("not important");
        result.setKey(key);
        result.addAssociatedItemKeys(associatedItemKeys);
        return result;
    }
}