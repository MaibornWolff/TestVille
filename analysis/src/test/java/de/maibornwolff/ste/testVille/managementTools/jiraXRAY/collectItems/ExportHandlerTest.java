package de.maibornwolff.ste.testVille.managementTools.jiraXRAY.collectItems;

import de.maibornwolff.ste.testVille.managementTools.jiraXRAY.component.Epic;
import de.maibornwolff.ste.testVille.managementTools.jiraXRAY.component.Item;
import de.maibornwolff.ste.testVille.managementTools.jiraXRAY.component.TestCase;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

class ExportHandlerTest {

    private ExportHandler exportHandler;

    // Global arrange
    private void setup() throws Exception {
        exportHandler = new ExportHandler("./src/test/resources/itemTest+.xml");
    }

    @Test
    @DisplayName("should extract an Item from the .xml-export included correct content")
    void extractionTest() throws Exception{
        // arrange
        ExportHandler exportHandler = new ExportHandler("./src/test/resources/itemTest.xml");

        // act
        List<TestCase> allItems = exportHandler.getAllTestCases();

        // assert
        assertEquals(expectTestResult(), allItems, "the extracted item content invalid data");
    }

    @TestFactory
    @DisplayName("should extract Items from the .xml-export an resolve associations")
    List<DynamicTest> extractionTestForManyItems () throws Exception {

        DynamicTest dt1, dt2, dt3;
        // arrange
        setup();

        // act
        List<Epic> allEpics         = exportHandler.getAllEpics();

        String firstKey = allEpics.stream().filter(x->x.getKey().equals("BMWGMDET-1424"))
                .map(x -> x.getAssociatedItems().iterator().next().getKey())
                .collect(Collectors.toList())
                .get(0)
        ;

        String secondKey = allEpics.stream().filter(x->x.getKey().equals("BMWGMDET-832"))
               .map(x -> x.getAssociatedItems().iterator().next().getKey())
               .collect(Collectors.toList())
               .get(0)
        ;

        dt1 = dtBuilder("should extract 2 Epics", 2, allEpics.size(), "Number of extracted Epic is invalid!");
        dt2 = dtBuilder("should associate TestCase to the right Epic", firstKey, "BMWGMDET-1405", "false Association");
        dt3 = dtBuilder("should associate TestCase to the right Epic", secondKey, "BMWGMDET-1404", "false Association");
        return List.of(dt1, dt2, dt3);
    }

    @TestFactory
    @DisplayName("should extract Items from the .xml-export an compute the number of executions")
    List<DynamicTest> extractTestCaseAndcomputeTheNumberOfExecutions () throws Exception {
        // arrange
        setup();

        DynamicTest dt1, dt2;
        List<TestCase> allTestCases = exportHandler.getAllTestCases();

        // act
        TestCase tc1 = allTestCases
                .stream()
                .filter(x -> x.getKey().equals("BMWGMDET-1405"))
                .collect(Collectors.toList()).get(0)
                ;
        TestCase tc2 = allTestCases
                .stream()
                .filter(x -> x.getKey().equals("BMWGMDET-1404"))
                .collect(Collectors.toList()).get(0)
                ;

        // asserts
        dt1 = dtBuilder("should determines the number of execution of a testcase", tc1.getPropertyMap().get("countExecution"), "2", "");
        dt2 = dtBuilder("should determines the number of execution of a testcase", tc2.getPropertyMap().get("countExecution"), "3", "");

        return List.of(dt1, dt2);
    }

    /**
     * The expected Item.
     */
    private List<Item> expectTestResult() {
        List<Item> expectedList = new ArrayList<>();
        TestCase expected = new TestCase();
        expected.setName("[BMWGMDET-1405] Fehlertoleranz");
        expected.setPriority("Minor");
        expected.setKey("BMWGMDET-1405");

        HashMap<String, String> map = new HashMap<>();
        map.put("created", "Wed, 31 Jan 2018 10:09:46 +0100");
        map.put("updated", "Wed, 31 Jan 2018 10:09:46 +0100");
        map.put("status", "To Do");
        map.put("Pre-Conditions association with a Test", "[]");
        map.put("Steps Count", "4.0");
        map.put("Test Plans associated with a Test", "[]");
        map.put("Test Sets association with a Test", "[BMWGMDET-1398]");
        map.put("Test Type", "Manual");
        map.put("TestRunStatus", "TODO");
        map.put("assignee", "Monika Nill");
        map.put("reporter", "Monika Nill");
        map.put("countExecution", "0");
        expected.setPropertyMap(map);
        expectedList.add(expected);
        return expectedList;
    }

    private DynamicTest dtBuilder(String s, Object e, Object a, String fm) {
        return DynamicTest.dynamicTest(s, () -> assertEquals(e, a, fm));
    }
}