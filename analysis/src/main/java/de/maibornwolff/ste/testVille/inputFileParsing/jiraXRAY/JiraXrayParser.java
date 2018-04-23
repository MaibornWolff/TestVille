package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY;

import de.maibornwolff.ste.testVille.configurationFileHandling.TranslationMapBuilder;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.Epic;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.JiraXrayTestCase;
import de.maibornwolff.ste.testVille.inputFileParsing.Parser;
import de.maibornwolff.ste.testVille.inputFileParsing.common.ManagementTool;
import de.maibornwolff.ste.testVille.inputFileParsing.common.Pair;
import de.maibornwolff.ste.testVille.domainModell.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.*;
import java.util.stream.Collectors;

/**
 * This implementation extends the org.xml.sax.helpers.DefaultHandler.
 * The JiraXrayParser is working like a state-machine.
 *
 * <img src="./../../../../../../../../resources/main/itemCollector.png" alt="JiraXrayParser is a State-machine" width="90%">
 *
 * @since 2.0.0
 *
 * (c) maibornwolff, TestVille, 2018
 */
public class JiraXrayParser extends DefaultHandler implements Parser {
    private final List<Item>      collectedItems;
    private JiraXrayParsingState  currentState;
    private Map<String, String>   currentMap;
    public Pair<String, String>  currentPair;
    private final ItemFactory itemFactory;

    public JiraXrayParser() {
        super();
        this.collectedItems = new LinkedList<>();
        this.currentState   = JiraXrayParsingState.START;
        this.currentMap     = new HashMap<>();
        this.currentPair    = new Pair<>("", "");
        this.itemFactory    = new ItemFactory();
    }

    public List<Item> getCollectedItems() {
        return collectedItems;
    }

    public void setCurrentState(JiraXrayParsingState state) {
        this.currentState = state;
    }

    public Map<String, String> getCurrentMap() {
        return currentMap;
    }

    public void setCurrentMap(Map<String, String> map){
        this.currentMap = map;
    }

    public JiraXrayParsingState getCurrentState() {
        return currentState;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "item":             this.passFrom_START_to_IN_ITEM_TAG();                         break;
            case "customfields":     this.passFrom_IN_ITEM_TAG_to_IN_CUSTOMFIELDS_TAG();           break;
            case "customfield":      this.passFrom_IN_CUSTOMFIELDS_TAG_to_IN_CUSTOMFIELD_TAG();    break;
            case "customfieldname":  this.passFrom_IN_CUSTOMFIELD_TAG_to_FIELD_NAME_EXTRACTION();  break;
            case "customfieldvalue": this.passFrom_IN_CUSTOMFIELD_TAG_to_FIELD_VALUE_EXTRACTION(); break;
            default:                 prepareExtractionOfSimpleField(qName);
        }
    }

    private void passFrom_START_to_IN_ITEM_TAG() {
        if(this.currentState == JiraXrayParsingState.START) {
            this.currentState = JiraXrayParsingState.IN_ITEM_TAG;
        }
    }

    private void passFrom_IN_ITEM_TAG_to_START() {
        if(this.currentState == JiraXrayParsingState.IN_ITEM_TAG) {
            this.setUpBeforeGoingBackToState_START();
            this.currentState = JiraXrayParsingState.START;
        }
    }

    private void setUpBeforeGoingBackToState_START() {
        this.saveExtractedItem();
        this.currentPair  = new Pair<>("", "");
        this.currentMap   = new HashMap<>();
    }

    private void passFrom_IN_ITEM_TAG_to_IN_CUSTOMFIELDS_TAG() {
        if (this.currentState == JiraXrayParsingState.IN_ITEM_TAG) {
            this.currentState = JiraXrayParsingState.IN_CUSTOMFIELDS_TAG;
        }
    }

    private void passFrom_IN_CUSTOMFIELDS_TAG_to_IN_ITEM_TAG() {
        if(this.currentState == JiraXrayParsingState.IN_CUSTOMFIELDS_TAG) {
            this.currentState = JiraXrayParsingState.IN_ITEM_TAG;
        }
    }

    private void passFrom_IN_CUSTOMFIELDS_TAG_to_IN_CUSTOMFIELD_TAG() {
        if(this.currentState == JiraXrayParsingState.IN_CUSTOMFIELDS_TAG) {
            this.currentState = JiraXrayParsingState.IN_CUSTOMFIELD_TAG;
        }
    }

    private void passFrom_IN_CUSTOMFIELD_TAG_to_IN_CUSTOMFIELDS_TAG() {
        if(this.currentState == JiraXrayParsingState.IN_CUSTOMFIELD_TAG) {
            this.setUpBeforeGoingBackToState_IN_CUSTOMFIELDS_TAG();
            this.currentState = JiraXrayParsingState.IN_CUSTOMFIELDS_TAG;
        }
    }

    private void setUpBeforeGoingBackToState_IN_CUSTOMFIELDS_TAG() {
        this.saveCurrentField();
        this.currentPair = new Pair<>("", "");
    }

    private void passFrom_IN_CUSTOMFIELD_TAG_to_FIELD_NAME_EXTRACTION() {
        if(this.currentState == JiraXrayParsingState.IN_CUSTOMFIELD_TAG) {
            this.currentState = JiraXrayParsingState.FIELD_NAME_EXTRACTION;
        }
    }

    private void passFrom_FIELD_NAME_EXTRACTION_to_IN_CUSTOMFIELD_TAG() {
        if(this.currentState == JiraXrayParsingState.FIELD_NAME_EXTRACTION) {
            this.currentState = JiraXrayParsingState.IN_CUSTOMFIELD_TAG;
        }
    }

    private void passFrom_IN_CUSTOMFIELD_TAG_to_FIELD_VALUE_EXTRACTION() {
        if(this.currentState == JiraXrayParsingState.IN_CUSTOMFIELD_TAG) {
            this.currentState = JiraXrayParsingState.FIELD_VALUE_EXTRACTION;
        }
    }

    private void passFrom_FIELD_VALUE_EXTRACTION_to_IN_CUSTOMFIELD_TAG() {
        if(this.currentState == JiraXrayParsingState.FIELD_VALUE_EXTRACTION) {
            this.currentState = JiraXrayParsingState.IN_CUSTOMFIELD_TAG;
        }
    }

    private void prepareExtractionOfSimpleField(String fieldName) {
        if((this.currentState == JiraXrayParsingState.IN_ITEM_TAG) && listOfSimplesFields().contains(fieldName)) {
            this.currentPair.setFirst(fieldName.trim());
        }
    }

    private void completeExtractionOfSimpleField(String fieldName) {
        if((this.currentState == JiraXrayParsingState.IN_ITEM_TAG)) {
            if (listOfSimplesFields().contains(fieldName) && this.isCurrentPairValid()) {
                this.currentMap.put(this.currentPair.getFirst(), this.currentPair.getSecond());
            }
            this.currentPair = new Pair<>("", "");
        }
    }

    @Override
    public void endElement(String uri, String localName, String qName) {
        switch (qName) {
            case "customfields":     passFrom_IN_CUSTOMFIELDS_TAG_to_IN_ITEM_TAG();           break;
            case "item":             passFrom_IN_ITEM_TAG_to_START();                         break;
            case "customfield":      passFrom_IN_CUSTOMFIELD_TAG_to_IN_CUSTOMFIELDS_TAG();    break;
            case "customfieldname":  passFrom_FIELD_NAME_EXTRACTION_to_IN_CUSTOMFIELD_TAG();  break;
            case "customfieldvalue": passFrom_FIELD_VALUE_EXTRACTION_to_IN_CUSTOMFIELD_TAG(); break;
            default:                 completeExtractionOfSimpleField(qName);
        }
    }

    private boolean isCurrentPairValid() {
        return !(this.currentPair.getFirst().isEmpty() || this.currentPair.getSecond().isEmpty());
    }

    @Override
    public void characters(char[] ch, int start, int length){
        String value = new String(ch, start, length);
        this.saveExtractedData(value);
    }

    private static List<String> listOfSimplesFields() {
        return List.of("priority",
                "title",
                "key",
                "type",
                "created",
                "updated",
                "status",
                "reporter",
                "assignee"
        );
    }

    private void saveFieldName(String fieldName) {
        this.currentPair.applyFunctionToFirst(x -> x + fieldName);//.first += fieldName;
    }

    private void saveFieldValue(String fieldValue) {
        this.currentPair.applyFunctionToSecond(x -> x + fieldValue);
    }

    private void saveExtractedData(String data) {
        data = data.trim();

        switch (this.currentState) {
            case IN_ITEM_TAG:            this.saveFieldValue(data); break;
            case FIELD_NAME_EXTRACTION:  this.saveFieldName(data);  break;
            case FIELD_VALUE_EXTRACTION: this.saveFieldValue(data); break;
        }
    }

    private void saveExtractedItem() {
        Item extractedItem = this.itemFactory.buildItemFromMap(this.currentMap);

        if(extractedItem != null) {
            this.collectedItems.add(extractedItem);
        }
    }

    private void saveCurrentField() {
        if(this.isCurrentPairValid()) {
            this.currentMap.put(this.currentPair.getFirst(), this.currentPair.getSecond());
        }
    }

    @Override
    public ComposedItem parse(String fileToParse, String configFilePath) throws Exception {
        TranslationMapBuilder tmb = new TranslationMapBuilder(configFilePath, ManagementTool.JIRA_XRAY);
        this.showItemsTheirPriorityRanking(tmb);
        this.showTestCaseTheirTranslationMap(tmb);
        JiraXrayParser jiraXrayParser = new JiraXrayParser();
        SAXParserFactory.newInstance().newSAXParser().parse(new File(fileToParse), jiraXrayParser);
        jiraXrayParser.completeTestCasesMetricsWithCountExecutions();
        jiraXrayParser.completeExtractedItemsWithDummyEpic();
        return jiraXrayParser.buildParsedTree();
    }

    private ComposedItem buildParsedTree() {
        ComposedItem parsedTree = new ComposedItem(0);
        Collection<Epic> associatedEpics = Epic.associateEpicsToTestCasesAndExtractIt(this.collectedItems);
        associatedEpics = associatedEpics.stream().filter(x -> !x.getAssociatedItems().isEmpty()).collect(Collectors.toList());
        parsedTree.addAssociatedItems(associatedEpics);
        parsedTree.setKey(""+parsedTree.getLocalId());
        parsedTree.setName("root");
        parsedTree.setPriority("root");
        return parsedTree;
    }

    private void completeExtractedItemsWithDummyEpic() {
        this.collectedItems.add(this.itemFactory.buildEpicFromLooseTestCases(JiraXrayTestCase.extractLooseTestCases(this.getCollectedItems())));
    }

    private void completeTestCasesMetricsWithCountExecutions() {
        Collection<JiraXrayTestCase> testCases = JiraXrayTestCase.extractTestCases(this.collectedItems);
        testCases.forEach(x -> x.addCountExecutionsAsMetric(this.collectedItems));
    }


    private void showItemsTheirPriorityRanking(TranslationMapBuilder tmb) {
        Item.priorityRanking = tmb.getPriorityRanking();
    }

    private void showTestCaseTheirTranslationMap(TranslationMapBuilder tmb) {
        JiraXrayTestCase.translationMap = tmb.getTranslationMap();
    }
}