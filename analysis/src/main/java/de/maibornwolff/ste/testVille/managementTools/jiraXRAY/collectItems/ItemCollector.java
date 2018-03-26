package de.maibornwolff.ste.testVille.managementTools.jiraXRAY.collectItems;

import de.maibornwolff.ste.testVille.managementTools.common.Pair;
import de.maibornwolff.ste.testVille.managementTools.jiraXRAY.component.*;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.DefaultHandler;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * This implementation extends the org.xml.sax.helpers.DefaultHandler.
 * The ItemCollector is working like a state-machine.
 *
 * <img src="./../../../../../../../../resources/main/itemCollector.png" alt="ItemCollector is a State-machine" width="90%">
 *
 * @since 2.0.0
 *
 * (c) maibornwolff, TestVille, 2018
 */
public class ItemCollector extends DefaultHandler {

    private final List<Item>        collectedItems;
    private ItemCollectorState      currentState;
    private Map<String, String>     currentMap;
    private Pair<String, String>    currentPair;
    private final List<String> xRayKnownUntranslatableFieldToExtract = List.of("assignee", "reporter");
    private final List<String> xRayKnownUndesirableFields = List.of("rank");

    ItemCollector() {
        super();
        this.collectedItems = new LinkedList<>();
        this.currentState   = ItemCollectorState.START;
        this.currentMap     = new HashMap<>();
        this.currentPair    = new Pair<>("", "");
    }

    /**
     *
     * @return Return all extracted Items from the .xml-export.
     */
    List<Item> getCollectedItems() {
        return collectedItems;
    }

    /**
     * Setter vor currentState. This method is only used vor testing.
     * @param state: the new state.
     */
    void setCurrentState(ItemCollectorState state) {
        this.currentState = state;
    }

    /**
     * Getter for currentMap
     * @return this.currentMap
     */
    Map<String, String> getCurrentMap() {
        return currentMap;
    }

    /**
     * Setter for currentMap
     * @param map new value of currentMap
     */
    void setCurrentMap(Map<String, String> map){
        this.currentMap = map;
    }

    /**
     * Getter for currentState
     * @return this.currentState
     */
    ItemCollectorState getCurrentState() {
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
     * this function perform the transition of the ItemCollector from the
     * START to the IN_ITEM_TAG state.
     */
    private void passFrom_START_to_IN_ITEM_TAG() {
        if(this.currentState == ItemCollectorState.START) {
            this.currentState = ItemCollectorState.IN_ITEM_TAG;
        }
    }

    /**
     * this function perform the transition of the ItemCollector from the
     * IN_ITEM_TAG to the START state.
     * This function is call at end of an item tag and
     * The following steps must done:
     *    1 -> translate all extracted data to an Item.
     *    2 -> by an correct Item the item is stored in this.collectedItems.
     *    3 -> state transition to START.
     *    4 -> reinitialisation of this.currentMap & this.currentPair.
     */
    private void passFrom_IN_ITEM_TAG_to_START() {
        if(this.currentState == ItemCollectorState.IN_ITEM_TAG) {
            this.setUpBeforeGoingBackToState_START();
            this.currentState = ItemCollectorState.START;
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
     * this function perform the transition of the ItemCollector from the
     * IN_ITEM_TAG to the IN_CUSTOMFIELDS_TAG state.
     */
    private void passFrom_IN_ITEM_TAG_to_IN_CUSTOMFIELDS_TAG() {
        if (this.currentState == ItemCollectorState.IN_ITEM_TAG) {
            this.currentState = ItemCollectorState.IN_CUSTOMFIELDS_TAG;
        }
    }

    /**
     * this function perform the transition of the ItemCollector from the
     * IN_CUSTOMFIELDS_TAG to the IN_ITEM_TAG state.
     */
    private void passFrom_IN_CUSTOMFIELDS_TAG_to_IN_ITEM_TAG() {
        if(this.currentState == ItemCollectorState.IN_CUSTOMFIELDS_TAG) {
            this.currentState = ItemCollectorState.IN_ITEM_TAG;
        }
    }

    /**
     * this function perform the transition of the ItemCollector from the
     * IN_CUSTOMFIELDS_TAG to the IN_CUSTOMFIELD_TAG state.
     */
    private void passFrom_IN_CUSTOMFIELDS_TAG_to_IN_CUSTOMFIELD_TAG() {
        if(this.currentState == ItemCollectorState.IN_CUSTOMFIELDS_TAG) {
            this.currentState = ItemCollectorState.IN_CUSTOMFIELD_TAG;
        }
    }

    /**
     * this function perform the transition of the ItemCollector from the
     * IN_CUSTOMFIELD_TAG to the IN_CUSTOMFIELDS_TAG state.
     * This function is call at end of an customfield tag and
     * The following steps must done:
     *    1 -> store the content of currentPair (if only correct) in currentMap.
     *    2 -> reinitialisation of this.currentPair.
     *    3 -> state transition to IN_CUSTOMFIELDS_TAG.
     */
    private void passFrom_IN_CUSTOMFIELD_TAG_to_IN_CUSTOMFIELDS_TAG() {
        if(this.currentState == ItemCollectorState.IN_CUSTOMFIELD_TAG) {
            this.setUpBeforeGoingBackToState_IN_CUSTOMFIELDS_TAG();
            this.currentState = ItemCollectorState.IN_CUSTOMFIELDS_TAG;
        }
    }

    private void setUpBeforeGoingBackToState_IN_CUSTOMFIELDS_TAG() {
        this.saveCurrentField();
        this.currentPair = new Pair<>("", "");
    }

    /**
     * this function perform the transition of the ItemCollector from the
     * IN_CUSTOMFIELD_TAG to the FIELD_NAME_EXTRACTION state.
     */
    private void passFrom_IN_CUSTOMFIELD_TAG_to_FIELD_NAME_EXTRACTION() {
        if(this.currentState == ItemCollectorState.IN_CUSTOMFIELD_TAG) {
            this.currentState = ItemCollectorState.FIELD_NAME_EXTRACTION;
        }
    }

    /**
     * this function perform the transition of the ItemCollector from the
     * FIELD_NAME_EXTRACTION to the IN_CUSTOMFIELD_TAG state.
     */
    private void passFrom_FIELD_NAME_EXTRACTION_to_IN_CUSTOMFIELD_TAG() {
        if(this.currentState == ItemCollectorState.FIELD_NAME_EXTRACTION) {
            this.currentState = ItemCollectorState.IN_CUSTOMFIELD_TAG;
        }
    }

    /**
     * this function perform the transition of the ItemCollector from the
     * IN_CUSTOMFIELD_TAG to the FIELD_VALUE_EXTRACTION state.
     */
    private void passFrom_IN_CUSTOMFIELD_TAG_to_FIELD_VALUE_EXTRACTION() {
        if(this.currentState == ItemCollectorState.IN_CUSTOMFIELD_TAG) {
            this.currentState = ItemCollectorState.FIELD_VALUE_EXTRACTION;
        }
    }

    /**
     * this function perform the transition of the ItemCollector from the
     * FIELD_VALUE_EXTRACTION to the IN_CUSTOMFIELD_TAG state.
     */
    private void passFrom_FIELD_VALUE_EXTRACTION_to_IN_CUSTOMFIELD_TAG() {
        if(this.currentState == ItemCollectorState.FIELD_VALUE_EXTRACTION) {
            this.currentState = ItemCollectorState.IN_CUSTOMFIELD_TAG;
        }
    }

    /**
     * this function is the first step by the extraction of an simple field.
     * it only fetch the name of the field in currentPair.first only when
     * the current simple field need to extracted (see {@link #listOfSimplesFields() listOfSimplesFields})
     * @param fieldName name of the simple field
     */
    private void prepareExtractionOfSimpleField(String fieldName) {
        if((this.currentState == ItemCollectorState.IN_ITEM_TAG) && listOfSimplesFields().contains(fieldName)) {
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
        if((this.currentState == ItemCollectorState.IN_ITEM_TAG)) {
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
    Item translateHashmapToItem() {
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
    private TestCase translateHashmapToTestCase() {
        TestCase result = new TestCase();
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
        Epic result = new Epic();
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
        TestSet result = new TestSet();
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
        TestExecution currentExtractedTestExecution = new TestExecution();
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

    /**
     * shows the Testcase item the keys of items that it connected to.
     * @param item: linked Testcase.
     */
    private void setTestCaseAssociatedElementKeys(TestCase item) {
        String associatedElements = this.getCurrentMap().get("Test Sets association with a Test");
        completeAssociatedElementKeys(item, associatedElements);

        if(this.getCurrentMap().containsKey("Epic Link")){
            item.addAssociatedElementKeys(this.getCurrentMap().get("Epic Link"));
        }
    }

    /**
     * shows the TestSet item the keys of items that it connected to.
     * @param item: linked TestSet.
     */
    private void setTestSetAssociatedElementKeys(TestSet item) {
        String associatedElements = this.getCurrentMap().get("Tests association with a Test Set");
        completeAssociatedElementKeys(item, associatedElements);

        if(this.getCurrentMap().containsKey("Epic Link")){
            item.addAssociatedElementKeys(this.getCurrentMap().get("Epic Link"));
        }
    }

    /**
     * shows the TestExecution item the keys of items that it connected to.
     * @param item: linked Testcase.
     */
    private void setTestExecutionAssociatedElementKeys(TestExecution item) {
        String associatedElements = this.getCurrentMap().get("Tests association with a Test Execution");
        completeAssociatedElementKeys(item, associatedElements);

        if(this.getCurrentMap().containsKey("Epic Link")){
            item.addAssociatedElementKeys(this.getCurrentMap().get("Epic Link"));
        }
    }

    private static void completeAssociatedElementKeys(Item item, String keyContainer) {
        if((keyContainer == null) || (keyContainer.isEmpty())) return;

        keyContainer = hardTrim(keyContainer);
        String[] li  = keyContainer.split(",");
        item.addAssociatedElementKeys(li);
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

    /**
     * this function fetch in item the current extracted settings(key, title, priority).
     * @param item item
     */
    void setItemSettings(Item item) {
        item.setKey     (this.getCurrentMap().get("key"));
        item.setName    (this.getCurrentMap().get("title"));
        item.setPriority(this.getCurrentMap().get("priority"));
    }

    /**
     *
     * @return all simple fields name that should be extracted.
     */
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

    /**
     * This method saves the extracted name of an field.
     * @param fieldName Name of the field.
     */
    private void saveFieldName(String fieldName) {
        this.currentPair.applyFunctionToFirst(x -> x + fieldName);//.first += fieldName;
    }

    /**
     * This method saves the extracted name of an field.
     * @param fieldValue Value of the field.
     */
    private void saveFieldValue(String fieldValue) {
        this.currentPair.applyFunctionToSecond(x -> x + fieldValue);
    }

    /**
     * this method get extracted data and saves it either as fieldName or as fieldValue.
     * @param data Received data.
     */
    private void saveExtractedData(String data) {
        data = data.trim();

        switch (this.currentState) {
            case IN_ITEM_TAG:            this.saveFieldValue(data); break;
            case FIELD_NAME_EXTRACTION:  this.saveFieldName(data);  break;
            case FIELD_VALUE_EXTRACTION: this.saveFieldValue(data); break;
        }
    }

    /**
     * Saves current extracted item in the list {@link #collectedItems} if the current item
     * is valid (!= null).
     */
    private void saveExtractedItem() {
        Item extractedItem = this.translateHashmapToItem();
        if(extractedItem != null) {
            this.collectedItems.add(extractedItem);
        }
    }

    /**
     * stores the value of the current extracted field value in {@link #currentPair}.
     */
    private void saveCurrentField() {
        if(this.currentPairIsOkay()) {
            this.currentMap.put(this.currentPair.getFirst(), this.currentPair.getSecond());
        }
    }
}