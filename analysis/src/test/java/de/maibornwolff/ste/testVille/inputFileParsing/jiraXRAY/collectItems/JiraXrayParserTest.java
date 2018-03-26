package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY.collectItems;

import de.maibornwolff.ste.testVille.domainModell.*;
import de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY.JiraXrayParser;
import de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY.JiraXrayParsingState;
import org.junit.jupiter.api.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class JiraXrayParserTest {

    /**
     * state-based testing. A JiraXrayParser has many states

    @TestFactory
    @DisplayName("should extract fields (name, value)")
    Collection<DynamicTest> characterTest() {
        // arrange
        JiraXrayParser itemCollector = new JiraXrayParser("item.xml");
        char [] charArray = {'d', 'e', 'r', 'k', 'ö', 'n', 'i', 'g', 'k', 'o', 'm', 'm', 't', '!'};

        // act & assert
        itemCollector.setCurrentState(JiraXrayParsingState.IN_ITEM_TAG);
        itemCollector.setCurrentPair(new Pair<>("hey", ""));
        itemCollector.characters(charArray, 0, 4);
        final Pair<String, String> actual1 = new Pair<>(itemCollector.currentPair.first, itemCollector.currentPair.second);
        DynamicTest dt1 = testFactoryHelper("should extract the right field value in state IN_ITEM_TAG",
                "extracted field value is not correct", new Pair<>("hey", "derk"), actual1);

        itemCollector.setCurrentState(JiraXrayParsingState.FIELD_NAME_EXTRACTION);
        itemCollector.currentPair.second = "common";
        itemCollector.characters(charArray, 3, 5);
        final Pair<String, String> actual2 = new Pair<>(itemCollector.currentPair.first, itemCollector.currentPair.second);
        DynamicTest dt2 = testFactoryHelper("should extract the right field name in state FIELD_NAME_EXTRACTION",
                "extracted field name is invalid", new Pair<>("könig", "common"), actual2)
        ;

        itemCollector.setCurrentState(JiraXrayParsingState.FIELD_VALUE_EXTRACTION);
        itemCollector.currentPair.first = "mann";
        itemCollector.characters(charArray, 8, 5);
        final Pair<String, String> actual3 = new Pair<>(itemCollector.currentPair.first, itemCollector.currentPair.second);
        DynamicTest dt3 = testFactoryHelper("should extract the right field value in state FIELD_VALUE_EXTRACTION",
                "extracted field value is invalid", new Pair<>("mann", "kommt"), actual3)
        ;

        return List.of(dt1, dt2, dt3);
    }*/

    @TestFactory
    @DisplayName("should configure(set name, key, priority) a created item")
    Collection<DynamicTest> setItemTest() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "i-194");
        map.put("title", "setTest");
        map.put("priority", "high");
        Item item = new Epic();

        // act
        jiraXrayParser.setCurrentMap(map);
        jiraXrayParser.setItemSettings(item);

        // assert
        DynamicTest dt1 = testFactoryHelper("item name check", "item name is invalid","setTest", item.getName());
        DynamicTest dt2 = testFactoryHelper("item priority check", "item value is invalid","high", item.getPriority());
        DynamicTest dt3 = testFactoryHelper("item key check", "item key is invalid","i-194", item.getKey());

        return List.of(dt1, dt2, dt3);
    }

    @Test
    @DisplayName("Should transform the extracted data to a Story-object")
    void translationToEpicTest() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();
        HashMap<String, String> map = new HashMap<>();
        map.put("key", "i-194");
        map.put("title", "setTest");
        map.put("priority", "high");
        //map.put("linked", "[i-1994; aba12; 199io]");
        map.put("type", "epic");
        Epic expectedItem = new Epic();

        // act
        jiraXrayParser.setCurrentMap(map);
        Item actualItem   = jiraXrayParser.translateHashmapToItem();
        expectedItem.setName("setTest");
        expectedItem.setPriority("high");
        expectedItem.setKey("i-194");
        //expectedItem.addAssociatedElements("i-1994", "aba12", "199io");

        // assert
        assertEquals(actualItem, expectedItem, "created Story is invalid");
    }

    @Test
    @DisplayName("Should transform the extracted data to a TestSet-object")
    void translationToTestsetTest() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();
        Map<String, String> map = new HashMap<>();
        map.put("key", "i-194");
        map.put("title", "setTest");
        map.put("priority", "high");
        map.put("Tests association with a Test Set", "[i-1994; aba12; 199io]");
        map.put("type", "test set");
        TestSet expectedItem = new TestSet();

        // act
        jiraXrayParser.setCurrentMap(map);
        Item actualItem   = jiraXrayParser.translateHashmapToItem();
        expectedItem.setName("setTest");
        expectedItem.setPriority("high");
        expectedItem.setKey("i-194");
        expectedItem.addAssociatedElementKeys("i-1994", "aba12", "199io");

        // assert
        assertEquals(actualItem, expectedItem, "created TestSet is invalid");
    }

    @Test
    @DisplayName("Should transform the extracted data to a Testcase-object")
    void translationToTestCase () {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();
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
        TestCase expectedItem = new TestCase();

        // act
        jiraXrayParser.setCurrentMap(map);
        Item actualItem  = jiraXrayParser.translateHashmapToItem();
        expectedItem.setName("setTest");
        expectedItem.setPriority("high");
        expectedItem.setKey("i-194");
        expectedItem.setPropertyMap(expectedMap);

        // assert
        assertEquals(actualItem, expectedItem, "created Testcase is invalid");
    }

    @Test
    @DisplayName("The JiraXrayParser should notice the end of an customfields-tag and change his state")
    void endElementTestI() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();

        // act
        jiraXrayParser.setCurrentState(JiraXrayParsingState.IN_CUSTOMFIELDS_TAG);
        jiraXrayParser.endElement("", "", "customfields");

        assertEquals(JiraXrayParsingState.IN_ITEM_TAG,
                jiraXrayParser.getCurrentState(),
                "the state translation is invalid"
        );
    }

    @Test
    @DisplayName("The JiraXrayParser should notice a invalid end-tag and does't not change his state")
    void negativeEndElementTestI_I() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();

        // act
        jiraXrayParser.setCurrentState(JiraXrayParsingState.IN_CUSTOMFIELD_TAG);
        jiraXrayParser.endElement("", "", "customfields");

        assertEquals(JiraXrayParsingState.IN_CUSTOMFIELD_TAG, jiraXrayParser.getCurrentState(), "unexpected state changing");
    }

    @Test
    @DisplayName("The JiraXrayParser should notice a invalid end-tag and does't not change his state")
    void negativeEndElementTestI_II() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();

        // act
        jiraXrayParser.setCurrentState(JiraXrayParsingState.IN_CUSTOMFIELDS_TAG);
        jiraXrayParser.endElement("", "", "item");

        assertEquals(JiraXrayParsingState.IN_CUSTOMFIELDS_TAG, jiraXrayParser.getCurrentState(), "unexpected state changing");
    }

    @TestFactory
    @DisplayName("Should notice the item-end-tag and build appropriated Item-Object")
    Collection<DynamicTest> endElementTestII() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();
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
        TestCase expectedItem = new TestCase();


        // act
        jiraXrayParser.setCurrentState(JiraXrayParsingState.IN_ITEM_TAG);
        jiraXrayParser.setCurrentMap(map);
        expectedItem.setName("setTest");
        expectedItem.setPriority("high");
        expectedItem.setKey("i-194");
        expectedItem.setPropertyMap(expectedMap);
        jiraXrayParser.endElement("", "", "item");

        // asserts
        DynamicTest dt1 = DynamicTest.dynamicTest(
                "state after an closing item-tag should be start",
                () -> assertEquals(JiraXrayParsingState.START, jiraXrayParser.getCurrentState())
        );

        /*DynamicTest dt2 = DynamicTest.dynamicTest(
                "the currentPair should be reset to (\"\", \"\")",
                () -> assertEquals(new Pair<>("", ""), jiraXrayParser.currentPair)
        );*/

        DynamicTest dt3 = DynamicTest.dynamicTest(
                "the currentPair should be reset",
                () -> assertEquals(new HashMap<String, String>(), jiraXrayParser.getCurrentMap())
        );

        DynamicTest dt4 = DynamicTest.dynamicTest(
                "an Testcase should be extracted and available in currentMap",
                () -> assertEquals(expectedItem, jiraXrayParser.getCollectedItems()
                        .get(jiraXrayParser.getCollectedItems().size()-1)
                )
        );

        return List.of(dt1, /*dt2,*/ dt3, dt4);
    }

   /* @TestFactory
    @DisplayName("Should transfer the extracted data from currentPair to currentMap")
    Collection<DynamicTest> endElementTestIII() {
        // arrange
        JiraXrayParser itemCollector = new JiraXrayParser("....xml");
        itemCollector.currentPair = new Pair<>("hey", "1221");

        // act
        itemCollector.setCurrentState(JiraXrayParsingState.IN_CUSTOMFIELD_TAG);
        itemCollector.endElement("", "", "customfield");

        // asserts
        DynamicTest dt1 = testFactoryHelper("the length of the map should be 1",
                "length of currentMap is invalid",1, itemCollector.getCurrentMap().size()
        );

        DynamicTest dt2 = testFactoryHelper("the map should contains the tupel (\"hey\", \"1221\")",
                "missing an extracted tupel in currentMap","1221", itemCollector.getCurrentMap().get("hey")
        );

        DynamicTest dt3 = testFactoryHelper("state after an closing customfield-tag should be  IN_CUSTOMFIELDS_TAG",
                "unexpected state of JiraXrayParser", JiraXrayParsingState.IN_CUSTOMFIELDS_TAG, itemCollector.getCurrentState()
        );

        return List.of(dt1, dt2, dt3);
    }*/

    @Test
    @DisplayName("The JiraXrayParser should notice a invalid end-tag and does't not change his state")
    void negativEndElementTestIII_I() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();

        // act
        jiraXrayParser.setCurrentState(JiraXrayParsingState.IN_ITEM_TAG);
        jiraXrayParser.endElement("", "", "customfield");

       assertEquals(JiraXrayParsingState.IN_ITEM_TAG, jiraXrayParser.getCurrentState(), "unexpected state transition");
    }

    @Test
    @DisplayName("The JiraXrayParser should notice a invalid end-tag and does't not change his state")
    void negativEndElementTestIII_II() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();

        // act
        jiraXrayParser.setCurrentState(JiraXrayParsingState.FIELD_VALUE_EXTRACTION);
        jiraXrayParser.endElement("", "item", "");


        assertEquals(JiraXrayParsingState.FIELD_VALUE_EXTRACTION, jiraXrayParser.getCurrentState(), "unexpected state transition");
    }

    @TestFactory
    @DisplayName("Should notice uncompleted extracted data an translate to any Items")
    Collection<DynamicTest> negativeTranslationTest() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();
        HashMap<String, String> map = new HashMap<>();
        map.put("type", "test");

        // act
        final Item actualItem0 = jiraXrayParser.translateHashmapToItem();
        jiraXrayParser.setCurrentMap(map);
        final Item actualItem1 = jiraXrayParser.translateHashmapToItem();
        map.replace("type", "test", "testset");
        final Item actualItem2 = jiraXrayParser.translateHashmapToItem();
        map.replace("type", "testset", "story");
        final Item actualItem3 = jiraXrayParser.translateHashmapToItem();

        // assert
        DynamicTest dt0 = testFactoryHelper("translation should fail and evaluate to null",
                "unexpected created Item", null, actualItem0
        );

        // assert
        DynamicTest dt1 = testFactoryHelper("translation should fail and evaluate to null",
                "unexpected created Item", null, actualItem1
        );

        DynamicTest dt2 = testFactoryHelper("translation should fail and evaluate to null",
                "unexpected created Item", null, actualItem2
        );

        DynamicTest dt3 = testFactoryHelper("translation should fail and evaluate to null",
                "unexpected created Item",null, actualItem3
        );
        return List.of(dt0, dt1, dt2, dt3);
    }

    @Test
    @DisplayName("Should change the state to state IN_ITEM_TAG by an opening item-tag")
    void startElementTestI() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();

        // act
        jiraXrayParser.setCurrentState(JiraXrayParsingState.START);
        jiraXrayParser.startElement("", "", "item", null);

        //assert
        assertEquals(JiraXrayParsingState.IN_ITEM_TAG,
                jiraXrayParser.getCurrentState(),
                "unexpected state changing"
        );
    }

    @Test
    @DisplayName("Should notice the inconsistency between the current state and the opening-tag")
    void negativeStartElementTestI_I() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();

        // act
        jiraXrayParser.setCurrentState(JiraXrayParsingState.START);
        jiraXrayParser.startElement("", "", "customfields", null);

        //assert
        assertEquals(JiraXrayParsingState.START, jiraXrayParser.getCurrentState(), "unexpected state changes");
    }

    @Test
    @DisplayName("Should notice the inconsistency between the current state and the opening-tag")
    void negativeStartElementTestI_II() {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();

        // act
        jiraXrayParser.setCurrentState(JiraXrayParsingState.FIELD_VALUE_EXTRACTION);
        jiraXrayParser.startElement("", "", "item", null);

        //assert
        assertEquals(JiraXrayParsingState.FIELD_VALUE_EXTRACTION,
                jiraXrayParser.getCurrentState(),
                "unexpected state changes"
        );
    }

    @Test
    @DisplayName("Should change the state from IN_ITEM_TAG to IN_CUSTOMFIELDS_TAG by an opening customfields-tag")
    void startElementTestII() {
        JiraXrayParsingState actual = startElementTestTemplate(JiraXrayParsingState.IN_ITEM_TAG,"customfields");
        // assert
        assertEquals(JiraXrayParsingState.IN_CUSTOMFIELDS_TAG, actual, "unexpected state changes");
    }

    @Test
    @DisplayName("Should notice the inconsistency between the current state and the opening-tag")
    void negativeStartElementTestII_I() {
        JiraXrayParsingState actual = startElementTestTemplate(JiraXrayParsingState.IN_CUSTOMFIELD_TAG,"customfields");
        // assert
        assertEquals(JiraXrayParsingState.IN_CUSTOMFIELD_TAG, actual, "unexpected state changes");
    }

    @Test
    @DisplayName("Should notice the inconsistency between the current state and the opening-tag")
    void negativeStartElementTestII_II() {
        JiraXrayParsingState actual = startElementTestTemplate(JiraXrayParsingState.IN_ITEM_TAG,"haha");
        // assert
        assertEquals(JiraXrayParsingState.IN_ITEM_TAG, actual, "unexpected state changes");
    }

    @Test
    @DisplayName("Should change the state from IN_CUSTOMFIELDS_TAG to IN_CUSTOMFIELD_TAG by an opening customfields-tag")
    void StartElementTestIII() {
        JiraXrayParsingState actual = startElementTestTemplate(JiraXrayParsingState.IN_CUSTOMFIELDS_TAG,"customfield");
        assertEquals(JiraXrayParsingState.IN_CUSTOMFIELD_TAG, actual, "unexpected state changes");
    }

    @Test
    @DisplayName("Should notice the inconsistency between the current state and the opening-tag")
    void negativeStartElementTestIII() {
        JiraXrayParsingState actual = startElementTestTemplate(JiraXrayParsingState.IN_CUSTOMFIELD_TAG,"customfield");
        assertEquals(JiraXrayParsingState.IN_CUSTOMFIELD_TAG, actual, "unexpected state chnages");
    }

    @Test
    @DisplayName("Should change the state from IN_CUSTOMFIELD_TAG to FIELD_NAME_EXTRACTION by an opening customfieldname-tag")
    void StartElementTestIV() {
        JiraXrayParsingState actual = startElementTestTemplate(JiraXrayParsingState.IN_CUSTOMFIELD_TAG,"customfieldname");
        assertEquals(JiraXrayParsingState.FIELD_NAME_EXTRACTION, actual, "unexpected state change");
    }

    @Test
    @DisplayName("Should notice the inconsistency between the current state and the opening-tag")
    void negativeStartElementTestIV() {
        JiraXrayParsingState actual = startElementTestTemplate(JiraXrayParsingState.IN_CUSTOMFIELDS_TAG,"customfieldname");
        assertEquals(JiraXrayParsingState.IN_CUSTOMFIELDS_TAG, actual, "unexpected state changes");
    }

    @Test
    @DisplayName("Should change the state from IN_CUSTOMFIELD_TAG to FIELD_VALUE_EXTRACTION by an opening customfieldvalue-tag")
    void StartElementTestV() {
        JiraXrayParsingState actual = startElementTestTemplate(JiraXrayParsingState.IN_CUSTOMFIELD_TAG,"customfieldvalue");
        assertEquals(JiraXrayParsingState.FIELD_VALUE_EXTRACTION, actual, "unexpected state chamges");
    }

    @Test
    @DisplayName("Should notice the inconsistency between the current state and the opening-tag")
    void negativeStartElementTestV() {
        JiraXrayParsingState actual = startElementTestTemplate(JiraXrayParsingState.FIELD_VALUE_EXTRACTION,"customfieldvalue");
        assertEquals(JiraXrayParsingState.FIELD_VALUE_EXTRACTION, actual, "unexpected state changes");
    }

    @TestTemplate
    JiraXrayParsingState startElementTestTemplate(JiraXrayParsingState state, String name) {
        // arrange
        JiraXrayParser jiraXrayParser = new JiraXrayParser();

        // act
        jiraXrayParser.setCurrentState(state);
        jiraXrayParser.startElement("", "", name, null);

        return jiraXrayParser.getCurrentState();
    }

    @TestTemplate
    DynamicTest testFactoryHelper(String displayName, String failMessage, Object expected, Object actual) {
        return DynamicTest.dynamicTest(displayName, () -> assertEquals(expected, actual, failMessage));
    }
}