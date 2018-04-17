package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY;

import de.maibornwolff.ste.testVille.domainModell.TestCase;
import de.maibornwolff.ste.testVille.inputFileParsing.common.Pair;
import org.junit.jupiter.api.*;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class JiraXrayParserTest {

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
    @DisplayName("Should notice the item-end-tag and build an appropriated Item-Object")
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
        TestCase expectedItem = new TestCase(1);


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

        DynamicTest dt2 = DynamicTest.dynamicTest(
                "the currentPair should be reset to (\"\", \"\")",
                () -> assertEquals(new Pair<>("", ""), jiraXrayParser.currentPair)
        );

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

        return List.of(dt1, dt2, dt3, dt4);
    }

    @TestFactory
    @DisplayName("Should transfer the extracted data from currentPair to currentMap")
    Collection<DynamicTest> endElementTestIII() {
        // arrange
        JiraXrayParser itemCollector = new JiraXrayParser();
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
    }

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
    public static DynamicTest testFactoryHelper(String displayName, String failMessage, Object expected, Object actual) {
        return DynamicTest.dynamicTest(displayName, () -> assertEquals(expected, actual, failMessage));
    }
}