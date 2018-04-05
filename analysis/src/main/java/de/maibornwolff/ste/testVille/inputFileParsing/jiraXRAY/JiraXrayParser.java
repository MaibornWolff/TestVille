package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY;

import de.maibornwolff.ste.testVille.configurationFileHandling.TranslationMapBuilder;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.Epic;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.JiraXrayTestCase;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.TestExecution;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.TestSet;
import de.maibornwolff.ste.testVille.inputFileParsing.Parser;
import de.maibornwolff.ste.testVille.inputFileParsing.common.IDGenerator;
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
    private final List<String>    xRayKnownUntranslatableFieldToExtract = List.of("assignee", "reporter");
    private final List<String>    xRayKnownUndesirableFields            = List.of("rank");
    private final IDGenerator     localIDGenerator                      = new IDGenerator();

    private int generateNextLocalKey() {
        return this.localIDGenerator.generateNextUniqueKey();
    }

    public JiraXrayParser() {
        super();
        this.collectedItems = new LinkedList<>();
        this.currentState   = JiraXrayParsingState.START;
        this.currentMap     = new HashMap<>();
        this.currentPair    = new Pair<>("", "");
    }

    /**
     *
     * @return Return all extracted Items from the .xml-export.
     */
    public List<Item> getCollectedItems() {
        return collectedItems;
    }

    /**
     * Setter vor currentState. This method is only used vor testing.
     * @param state: the new state.
     */
    public void setCurrentState(JiraXrayParsingState state) {
        this.currentState = state;
    }

    /**
     * Getter for currentMap
     * @return this.currentMap
     */
    public Map<String, String> getCurrentMap() {
        return currentMap;
    }

    /**
     * Setter for currentMap
     * @param map new value of currentMap
     */
    public void setCurrentMap(Map<String, String> map){
        this.currentMap = map;
    }

    /**
     * Getter for currentState
     * @return this.currentState
     */
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

    /**
     * this function perform the transition of the JiraXrayParser from the
     * START to the IN_ITEM_TAG state.
     */
    private void passFrom_START_to_IN_ITEM_TAG() {
        if(this.currentState == JiraXrayParsingState.START) {
            this.currentState = JiraXrayParsingState.IN_ITEM_TAG;
        }
    }

    /**
     * this function perform the transition of the JiraXrayParser from the
     * IN_ITEM_TAG to the START state.
     * This function is call at end of an item tag and
     * The following steps must done:
     *    1 -> translate all extracted data to an Item.
     *    2 -> by an correct Item the item is stored in this.collectedItems.
     *    3 -> state transition to START.
     *    4 -> reinitialisation of this.currentMap & this.currentPair.
     */
    private void passFrom_IN_ITEM_TAG_to_START() {
        if(this.currentState == JiraXrayParsingState.IN_ITEM_TAG) {
            this.setUpBeforeGoingBackToState_START();
            this.currentState = JiraXrayParsingState.START;
        }
    }

    /**
     * contain steps
     */
    private void setUpBeforeGoingBackToState_START() {
        this.saveExtractedItem();
        this.currentPair  = new Pair<>("", "");
        this.currentMap   = new HashMap<>();
    }

    /**
     * this function perform the transition of the JiraXrayParser from the
     * IN_ITEM_TAG to the IN_CUSTOMFIELDS_TAG state.
     */
    private void passFrom_IN_ITEM_TAG_to_IN_CUSTOMFIELDS_TAG() {
        if (this.currentState == JiraXrayParsingState.IN_ITEM_TAG) {
            this.currentState = JiraXrayParsingState.IN_CUSTOMFIELDS_TAG;
        }
    }

    /**
     * this function perform the transition of the JiraXrayParser from the
     * IN_CUSTOMFIELDS_TAG to the IN_ITEM_TAG state.
     */
    private void passFrom_IN_CUSTOMFIELDS_TAG_to_IN_ITEM_TAG() {
        if(this.currentState == JiraXrayParsingState.IN_CUSTOMFIELDS_TAG) {
            this.currentState = JiraXrayParsingState.IN_ITEM_TAG;
        }
    }

    /**
     * this function perform the transition of the JiraXrayParser from the
     * IN_CUSTOMFIELDS_TAG to the IN_CUSTOMFIELD_TAG state.
     */
    private void passFrom_IN_CUSTOMFIELDS_TAG_to_IN_CUSTOMFIELD_TAG() {
        if(this.currentState == JiraXrayParsingState.IN_CUSTOMFIELDS_TAG) {
            this.currentState = JiraXrayParsingState.IN_CUSTOMFIELD_TAG;
        }
    }

    /**
     * this function perform the transition of the JiraXrayParser from the
     * IN_CUSTOMFIELD_TAG to the IN_CUSTOMFIELDS_TAG state.
     * This function is call at end of an customfield tag and
     * The following steps must done:
     *    1 -> store the content of currentPair (if only correct) in currentMap.
     *    2 -> reinitialisation of this.currentPair.
     *    3 -> state transition to IN_CUSTOMFIELDS_TAG.
     */
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

    /**
     * this function perform the transition of the JiraXrayParser from the
     * IN_CUSTOMFIELD_TAG to the FIELD_NAME_EXTRACTION state.
     */
    private void passFrom_IN_CUSTOMFIELD_TAG_to_FIELD_NAME_EXTRACTION() {
        if(this.currentState == JiraXrayParsingState.IN_CUSTOMFIELD_TAG) {
            this.currentState = JiraXrayParsingState.FIELD_NAME_EXTRACTION;
        }
    }

    /**
     * this function perform the transition of the JiraXrayParser from the
     * FIELD_NAME_EXTRACTION to the IN_CUSTOMFIELD_TAG state.
     */
    private void passFrom_FIELD_NAME_EXTRACTION_to_IN_CUSTOMFIELD_TAG() {
        if(this.currentState == JiraXrayParsingState.FIELD_NAME_EXTRACTION) {
            this.currentState = JiraXrayParsingState.IN_CUSTOMFIELD_TAG;
        }
    }

    /**
     * this function perform the transition of the JiraXrayParser from the
     * IN_CUSTOMFIELD_TAG to the FIELD_VALUE_EXTRACTION state.
     */
    private void passFrom_IN_CUSTOMFIELD_TAG_to_FIELD_VALUE_EXTRACTION() {
        if(this.currentState == JiraXrayParsingState.IN_CUSTOMFIELD_TAG) {
            this.currentState = JiraXrayParsingState.FIELD_VALUE_EXTRACTION;
        }
    }

    /**
     * this function perform the transition of the JiraXrayParser from the
     * FIELD_VALUE_EXTRACTION to the IN_CUSTOMFIELD_TAG state.
     */
    private void passFrom_FIELD_VALUE_EXTRACTION_to_IN_CUSTOMFIELD_TAG() {
        if(this.currentState == JiraXrayParsingState.FIELD_VALUE_EXTRACTION) {
            this.currentState = JiraXrayParsingState.IN_CUSTOMFIELD_TAG;
        }
    }

    /**
     * this function is the first step by the extraction of an simple field.
     * it only fetch the name of the field in currentPair.first only when
     * the current simple field need to extracted (see {@link #listOfSimplesFields() listOfSimplesFields})
     * @param fieldName name of the simple field
     */
    private void prepareExtractionOfSimpleField(String fieldName) {
        if((this.currentState == JiraXrayParsingState.IN_ITEM_TAG) && listOfSimplesFields().contains(fieldName)) {
            this.currentPair.setFirst(fieldName.trim());
        }
    }

    /**
     * this function complete the extraction of an simple field.
     * before storing the current extracted data in the currentMap it check
     * the correctness of the data with {@link #listOfSimplesFields() listOfSimplesFields} and {@link #currentPairIsOkay() currentPairIsOkay}
     * @param fieldName name of the simple field
     */
    private void completeExtractionOfSimpleField(String fieldName) {
        if((this.currentState == JiraXrayParsingState.IN_ITEM_TAG)) {
            if (listOfSimplesFields().contains(fieldName) && this.currentPairIsOkay()) {
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


    /**
     * before fetching extracted data in this.currentMap the consistency of the data
     * is tested through this method.
     * @return true when this.currentPair contains consistent data else false.
     */
    private boolean currentPairIsOkay() {
        return !(this.currentPair.getFirst().isEmpty()
                || this.currentPair.getSecond().isEmpty()
                || xRayKnownUndesirableFields.stream().anyMatch(x -> this.currentPair.getFirst().toLowerCase().startsWith(x))
        );
    }

    @Override
    public void characters(char[] ch, int start, int length){
        String value = new String(ch, start, length);
        this.saveExtractedData(value);
    }

    /**
     * This method build an Item based on the current extracted data. this data are
     * stored in this.currentMap.
     * @return an Item-Object or null
     */
    public Item translateHashmapToItem() {
        String type = this.getCurrentMap().get("type");
        if(type == null){
            return null;
        }

        type = type.trim().toLowerCase();
        return translateMapContentToItemDependingOnItemTyp(type);
    }

    /**
     * This function shows the type of the current extracted item and depending on that,
     * calls the right creator method.
     * @param itemType: Type of the current extracted item as string.
     * @return        : a Item-Object or null.
     */
    private Item translateMapContentToItemDependingOnItemTyp(String itemType) {
        Item result = null;
        switch (itemType) {
            case "test"           : result = this.translateHashmapToTestCase();  break;
            case "epic"           : result = this.translateHashmapToEpic();      break;
            case "test set"       : result = this.translateHashmapToTestSet();   break;
            case "test execution" : result = this.translateMapToTestExecution(); break;
        }
        this.saveUntranslatableFields(result);
        return result;
    }

    /**
     * This method build an TestCase based on the current extracted data. this data are
     * stored in this.currentMap.
     * @return an TestCase-Object
     */
    private JiraXrayTestCase translateHashmapToTestCase() {
        JiraXrayTestCase result = new JiraXrayTestCase(this.generateNextLocalKey());
        this.setItemSettings(result);

        if(result.isItemInvalid()) {
            return null;
        }
        this.setItemAssociatedElementsKeys(result);
        this.setTestCasePropertyList(result);
        return result;
    }

    /**
     * This method build an Story based on the current extracted data. this data are
     * stored in this.currentMap.
     * @return an Story-Object
     */
    private Epic translateHashmapToEpic() {
        Epic result = new Epic(this.generateNextLocalKey());
        this.setItemSettings(result);
        if(result.isItemInvalid()) {
            return null;
        }
        return result;
    }

    /**
     * This method build an TestSet based on the current extracted data. this data are
     * stored in this.currentMap.
     * @return an TestSet-Object.
     */
    private TestSet translateHashmapToTestSet() {
        TestSet result = new TestSet(this.generateNextLocalKey());
        this.setItemSettings(result);
        if(result.isItemInvalid()) {
            return null;
        }
        this.setItemAssociatedElementsKeys(result);
        return result;
    }

    /**
     * This method build an TestExecution based on the current extracted data. this data are
     * stored in this.currentMap.
     * @return an TestExecution-Object.
     */
    private TestExecution translateMapToTestExecution() {
        TestExecution currentExtractedTestExecution = new TestExecution(this.generateNextLocalKey());
        this.setItemSettings(currentExtractedTestExecution);
        if(currentExtractedTestExecution.isItemInvalid()) {
            return null;
        }
        this.setItemAssociatedElementsKeys(currentExtractedTestExecution);
        return currentExtractedTestExecution;
    }

    private void saveUntranslatableFields(Item item) {
        if(item == null) return;

        for(String fieldName: this.xRayKnownUntranslatableFieldToExtract) {
            String maybeValue = this.currentMap.get(fieldName);
            if(maybeValue != null) item.addNewUntranslatableField(fieldName, maybeValue);
        }
    }

    /**
     * removes the setting data from the current map and assign testcase the rest
     * of the entries as properties.
     * @param testCase: Testcase which properties must be set.
     */
    private void setTestCasePropertyList(TestCase testCase) {
        this.removeExtractedItemSettings();
        testCase.setPropertyMap(this.currentMap);
    }

    /**
     * remove from current map the setting data.
     */
    private void removeExtractedItemSettings() {
        this.currentMap.remove("key");
        this.currentMap.remove("title");
        this.currentMap.remove("priority");
        this.currentMap.remove("type");
    }


    /**
     * shows the Item item the keys of items that it connected to.
     * @param item: linked item.
     */
    private void setItemAssociatedElementsKeys(Item item) {
        switch (item.getItemTyp()) {
            case TESTSET:       setTestSetAssociatedElementKeys      ((TestSet)       item); break;
            case TESTCASE:      setTestCaseAssociatedElementKeys     ((TestCase)      item); break;
            case TESTEXECUTION: setTestExecutionAssociatedElementKeys((TestExecution) item); break;
        }
    }

    private void setTestCaseAssociatedElementKeys(TestCase item) {
        String associatedElements = this.getCurrentMap().get("Test Sets association with a Test");
        completeAssociatedElementKeys(item, associatedElements);

        if(this.getCurrentMap().containsKey("Epic Link")){
            item.addAssociatedItemKeys(this.getCurrentMap().get("Epic Link"));
        }
    }

    private void setTestSetAssociatedElementKeys(TestSet item) {
        String associatedElements = this.getCurrentMap().get("Tests association with a Test Set");
        completeAssociatedElementKeys(item, associatedElements);

        if(this.getCurrentMap().containsKey("Epic Link")){
            item.addAssociatedItemKeys(this.getCurrentMap().get("Epic Link"));
        }
    }

    private void setTestExecutionAssociatedElementKeys(TestExecution item) {
        String associatedElements = this.getCurrentMap().get("Tests association with a Test Execution");
        completeAssociatedElementKeys(item, associatedElements);

        if(this.getCurrentMap().containsKey("Epic Link")){
            item.addAssociatedItemKeys(this.getCurrentMap().get("Epic Link"));
        }
    }

    private static void completeAssociatedElementKeys(Item item, String keyContainer) {
        if((keyContainer == null) || (keyContainer.isEmpty())) return;

        keyContainer = hardTrim(keyContainer);
        String[] li  = keyContainer.split(",");
        item.addAssociatedItemKeys(li);
    }

    private static String hardTrim(String str) {
        int begin = 0;
        int end   = str.length();

        for (Character c: str.toCharArray()) {
            if(!Character.isLetterOrDigit(c)) begin ++;
            else break;
        }


        for (int i = str.length()-1; i >= 0; i--) {
            if(!Character.isLetterOrDigit(str.charAt(i))) end --;
            else break;
        }
        if(begin < end)
            return str.substring(begin, end);
        return "";
    }

    public void setItemSettings(Item item) {
        item.setKey     (this.getCurrentMap().get("key"));
        item.setName    (this.getCurrentMap().get("title"));
        item.setPriority(this.getCurrentMap().get("priority"));
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
        Item extractedItem = this.translateHashmapToItem();
        if(extractedItem != null) {
            this.collectedItems.add(extractedItem);
        }
    }

    private void saveCurrentField() {
        if(this.currentPairIsOkay()) {
            this.currentMap.put(this.currentPair.getFirst(), this.currentPair.getSecond());
        }
    }

    @Override
    public Collection<Item> parse(String fileToParse, String configFilePath) throws Exception {
        JiraXrayParser jiraXrayParser = new JiraXrayParser();
        SAXParserFactory.newInstance().newSAXParser().parse(new File(fileToParse), jiraXrayParser);
        jiraXrayParser.manageExtractedItems(configFilePath);
        return extractAllEpics(jiraXrayParser.getCollectedItems());
    }

    private void manageExtractedItems(String configFilePath) throws Exception {
        associateEpicsToTestCases(this.getCollectedItems());
        letExtractedTestCasesKnowTheirCountExecutions(this.getCollectedItems());
        completeExtractedItemsWithDummyEpic();
        showExtractedTestCaseTheirTranslationMap(configFilePath);
    }

    private void showExtractedTestCaseTheirTranslationMap(String configFile) throws Exception {
        JiraXrayTestCase.translationMap = new TranslationMapBuilder(configFile, ManagementTool.JIRA_XRAY).getTranslationMap();
    }

    private void completeExtractedItemsWithDummyEpic() {
        this.collectedItems.add(createEpicForLooseTestCases(this.getCollectedItems()));
    }

    private Epic createEpicForLooseTestCases(Collection<Item> allExtractedItems) {
        Epic dummyEpic = this.createDummyEpic();
        dummyEpic.addAllAssociatedItems(extractLooseTestCases(allExtractedItems));
        return dummyEpic;
    }


    private static void associateEpicsToTestCases(Collection<Item> allExtractedItems) {
        allExtractedItems
                .stream()
                .filter(JiraXrayParser:: isEpic)
                .forEach(x -> associateEpicToTestCases((Epic) x, allExtractedItems));
    }

    private static boolean isEpic(Item maybeEpic) {
        return maybeEpic.getItemTyp() == ItemTyp.EPIC;
    }

    private static void associateEpicToTestCases(Epic epic, Collection<Item> allExtractedItems) {
        for(Item item: allExtractedItems) {
            if(isTestCase(item) && existsAssociationBetweenEpicUndTestCase(epic, item)) {
                epic.addAllAssociatedItems(item);
            }else if(isTestSet(item) && existsAssociationBetweenEpicUndTestSet(epic, item)) {
                Collection<Item> testCaseBelongEpic = extractTestCasesBelongTestSet(item, allExtractedItems);
                epic.addAllAssociatedItems(testCaseBelongEpic);
            }
        }
    }

    private static boolean existsAssociationBetweenEpicUndTestSet(Epic epic, Item testSet) {
        return testSet.getAssociatedItemKeys().contains(epic.getKey());
    }

    private static boolean existsAssociationBetweenEpicUndTestCase(Epic epic, Item test) {
        return test.getAssociatedItemKeys().contains(epic.getKey());
    }

    private static Collection<Item> extractTestCasesBelongTestSet(Item testSet, Collection<Item> items) {
        return items
                .stream()
                .filter(JiraXrayParser::isTestCase)
                .filter(x -> x.getAssociatedItemKeys().contains(testSet.getKey()))
                .collect(Collectors.toSet());
    }

    private static boolean isTestCase(Item maybeTestCase) {
        return maybeTestCase.getItemTyp() == ItemTyp.TESTCASE;
    }

    private static boolean isTestSet(Item maybeTestSet) {
        return maybeTestSet.getItemTyp() == ItemTyp.TESTSET;
    }


    private static Collection<Item> extractLooseTestCases(Collection<Item> allExtractedItems) {
        Collection<Item> looseTestCases = new ArrayList<>();
        looseTestCases.addAll(extractDirectLooseTestCases(  allExtractedItems));
        looseTestCases.addAll(extractIndirectLooseTestCases(allExtractedItems));
        return looseTestCases;
    }

    private static Collection<Item> extractDirectLooseTestCases(Collection<Item> allExtractedItems) {
        Collection<String> mergedKeysFromItemAssociatedToEpics = extractAllEpics(allExtractedItems)
                .stream()
                .map(Item::getAssociatedItemKeys)
                .reduce(new ArrayList<>(), (x, y) -> {x.addAll(y); return x;});
       return allExtractedItems
                .stream()
                .filter(x -> isTestCase(x) && (!mergedKeysFromItemAssociatedToEpics.contains(x.getKey())))
                .collect(Collectors.toList());
    }

    private static Collection<Item> extractIndirectLooseTestCases(Collection<Item> allExtractedItems) {
        Collection<Item>   looseTestSets = extractLooseTestSets(allExtractedItems);
        Collection<String> mergedKeysFromItemAssociatedToTestSets = looseTestSets
                .stream()
                .map(Item::getAssociatedItemKeys)
                .reduce(new ArrayList<>(), (x, y) -> {x.addAll(y); return x;});

        return allExtractedItems
                .stream()
                .filter(x -> isTestCase(x) && (!mergedKeysFromItemAssociatedToTestSets.contains(x.getKey())))
                .collect(Collectors.toList());
    }

    private static Collection<Item> extractLooseTestSets(Collection<Item> allExtractedItems) {
        Collection<String> mergedKeysFromItemAssociatedToEpics = extractAllEpics(allExtractedItems)
                .stream()
                .map(Item::getAssociatedItemKeys)
                .reduce(new ArrayList<>(), (x, y) -> {x.addAll(y); return x;});
        return allExtractedItems
                .stream()
                .filter(x -> isTestSet(x) && (!mergedKeysFromItemAssociatedToEpics.contains(x.getKey())))
                .collect(Collectors.toList());
    }

    private Epic createDummyEpic() {
        Epic dummyEpic = new Epic(this.generateNextLocalKey());
        dummyEpic.setKey("dummyKey-1");
        dummyEpic.setName("DummyEpic");
        dummyEpic.setPriority("Minor");
        return dummyEpic;
    }


    private static Collection<Item> extractAllEpics(Collection<Item> allExtractedItems) {
        return allExtractedItems.stream().filter(JiraXrayParser::isEpic).collect(Collectors.toList());
    }

    private static Collection<TestExecution> extractAllTestExecutions(Collection<Item> allExtractedItems) {
        return allExtractedItems
                .stream()
                .filter(JiraXrayParser::isTestExecution)
                .map(x -> (TestExecution) x)
                .collect(Collectors.toList());
    }

    private static boolean isTestExecution(Item maybeTestExecution) {
        return maybeTestExecution.getItemTyp() == ItemTyp.TESTEXECUTION;
    }

    private static void letExtractedTestCasesKnowTheirCountExecutions(Collection<Item> allItems) {
        Collection<TestExecution>    allTestExecutions = extractAllTestExecutions(allItems);
        Collection<JiraXrayTestCase> allTestCases      = extractAllTestCases(allItems);
        allTestCases.forEach(x -> x.addCountExecutionsAsTestCaseMetric(allTestExecutions));
    }

    private static Collection<JiraXrayTestCase> extractAllTestCases(Collection<Item> allItems) {
        return allItems.stream().filter(JiraXrayParser::isTestCase)
                .map(x -> (JiraXrayTestCase) x)
                .collect(Collectors.toList());
    }
}