package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY;

import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.TestCase;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.Epic;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.TestSet;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class ItemFactoryTest {

    private ItemFactory builder;
    @BeforeEach
    void setup(){
        builder = new ItemFactory();
    }

    @Test
    @DisplayName("Should transform the extracted data to a Story-object")
    void translationToEpicTest() {
        // arrange
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "i-194");
        map.put("title", "setTest");
        map.put("priority", "high");
        map.put("type", "epic");
        Epic expectedItem = new Epic(1);

        // act
        Item actualItem   = builder.buildItemFromMap(map);
        expectedItem.setName("setTest");
        expectedItem.setPriority("high");
        expectedItem.setKey("i-194");

        // assert
        assertEquals(actualItem, expectedItem, "created Story is invalid");
    }

    @Test
    @DisplayName("Should transform the extracted data to a TestSet-object")
    void translationToTestsetTest() {
        // arrange
        Map<String, String> map = new HashMap<>();
        map.put("key", "i-194");
        map.put("title", "setTest");
        map.put("priority", "high");
        map.put("Tests association with a Test Set", "[i-1994; aba12; 199io]");
        map.put("type", "test set");
        TestSet expectedItem = new TestSet(1);

        // act
        Item actualItem   = builder.buildItemFromMap(map);
        expectedItem.setName("setTest");
        expectedItem.setPriority("high");
        expectedItem.setKey("i-194");
        expectedItem.addAssociatedItemKeys("i-1994", "aba12", "199io");

        // assert
        assertEquals(actualItem, expectedItem, "created TestSet is invalid");
    }

    @Test
    @DisplayName("Should transform the extracted data to a Testcase-object")
    void translationToTestCase () {
        // arrange
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "i-194");
        map.put("title", "setTest");
        map.put("priority", "high");
        map.put("runs", "12");
        map.put("lastTestRunStatus", "1");
        map.put("type", "test");
        HashMap<String, String> expectedMap = new HashMap<>();
        expectedMap.put("lastTestRunStatus", "1");
        expectedMap.put("runs", "12");
        TestCase expectedItem = new TestCase(1);

        // act
        Item actualItem  = new ItemFactory().buildItemFromMap(map);
        expectedItem.setName("setTest");
        expectedItem.setPriority("high");
        expectedItem.setKey("i-194");
        expectedItem.setPropertyMap(expectedMap);

        // assert
        assertEquals(actualItem, expectedItem, "created Testcase is invalid");
    }

    @TestFactory
    @DisplayName("Should notice uncompleted extracted data an translate to any Items")
    Collection<DynamicTest> negativeTranslationTest() {
        // arrange
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "test");

        // act
        final Item actualItem0 = builder.buildItemFromMap(map);
        final Item actualItem1 = builder.buildItemFromMap(map);
        map.replace("type", "test", "testset");
        final Item actualItem2 = builder.buildItemFromMap(map);
        map.replace("type", "testset", "story");
        final Item actualItem3 = builder.buildItemFromMap(map);

        // assert
        DynamicTest dt0 = JiraXrayParserTest.testFactoryHelper("translation should fail and evaluate to null",
                "unexpected created Item", null, actualItem0
        );

        // assert
        DynamicTest dt1 = JiraXrayParserTest.testFactoryHelper("translation should fail and evaluate to null",
                "unexpected created Item", null, actualItem1
        );

        DynamicTest dt2 = JiraXrayParserTest.testFactoryHelper("translation should fail and evaluate to null",
                "unexpected created Item", null, actualItem2
        );

        DynamicTest dt3 = JiraXrayParserTest.testFactoryHelper("translation should fail and evaluate to null",
                "unexpected created Item",null, actualItem3
        );
        return List.of(dt0, dt1, dt2, dt3);
    }

}