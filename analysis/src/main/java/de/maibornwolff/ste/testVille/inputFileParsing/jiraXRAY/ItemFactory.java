package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY;

import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.Maintenance;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.Epic;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.JiraXrayTestCase;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.TestExecution;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.TestSet;
import de.maibornwolff.ste.testVille.inputFileParsing.common.IDGenerator;

import java.time.LocalDate;
import java.util.Collection;
import java.util.Map;

public class ItemFactory {

    private final IDGenerator localIDGenerator = new IDGenerator();

    public Item buildItemFromMap(Map<String, String> currentMap) {
        String type = currentMap.get("type");
        if(type == null){
            return null;
        }

        Item result = null;
        type = type.trim().toLowerCase();
        switch (type) {
            case "test"           : result = this.buildTestCaseFromMap(currentMap);  break;
            case "epic"           : result = this.buildEpicFromMap(currentMap);      break;
            case "test set"       : result = this.buildTestSetFromMap(currentMap);   break;
            case "test execution" : result = this.buildTestExecutionFromMap(currentMap); break;
        }

        if(result != null){
            completeItemAssociatedElementsKeys(result, currentMap);
            result.maintenance = buildMaintenanceInfo(currentMap);
        }
        return result;
    }

    Epic buildEpicFromLooseTestCases(Collection<Item> allLooseTestCases) {
        Epic dummyEpic = this.buildDummyEpic();
        dummyEpic.addAssociatedItems(allLooseTestCases);
        return dummyEpic;
    }

    private Epic buildDummyEpic() {
        Epic dummyEpic = new Epic(this.generateNextLocalKey());
        dummyEpic.setKey("dummyKey-1");
        dummyEpic.setName("DummyEpic");
        dummyEpic.setPriority("Minor");
        return dummyEpic;
    }

    private JiraXrayTestCase buildTestCaseFromMap(Map<String, String> currentMap) {
        JiraXrayTestCase result = new JiraXrayTestCase(this.generateNextLocalKey());
        result = (JiraXrayTestCase) configureItem(result, currentMap);

        if(result != null) result.fillPropertyMap(currentMap);
        return result;
    }

    private Epic buildEpicFromMap(Map<String, String> currentMap) {
        Epic result = new Epic(this.generateNextLocalKey());
        return (Epic) configureItem(result, currentMap);
    }

    private TestSet buildTestSetFromMap(Map<String, String> currentMap) {
        TestSet result = new TestSet(this.generateNextLocalKey());
        return (TestSet) configureItem(result, currentMap);
    }

    private TestExecution buildTestExecutionFromMap(Map<String, String> currentMap) {
        TestExecution testExecution = new TestExecution(this.generateNextLocalKey());
        return (TestExecution) configureItem(testExecution, currentMap);
    }

    private int generateNextLocalKey() {
        return this.localIDGenerator.generateNextUniqueKey();
    }

    private Item configureItem(Item item, Map<String, String> currentMap) {
        item.setKey     (currentMap.get("key"));
        item.setName    (currentMap.get("title"));
        item.setPriority(currentMap.get("priority"));
        return item.isInvalid() ? null : item;
    }



    private void completeItemAssociatedElementsKeys(Item item, Map<String, String> currentMap) {
        switch (item.getType()) {
            case TESTSET: completeItemAssociatedElementsKeysByEntryName (item, currentMap,"Tests association with a Test Set");
            break;
            case TESTCASE: completeItemAssociatedElementsKeysByEntryName (item, currentMap, "Test Sets association with a Test");
            break;
            case TESTEXECUTION: completeItemAssociatedElementsKeysByEntryName (item, currentMap, "Tests association with a Test Execution");
            break;
        }
    }

    private void completeItemAssociatedElementsKeysByEntryName(Item item, Map<String, String> currentMap, String entryName) {
        String mergedKeys = currentMap.get(entryName);
        mergedKeys = removeLeadingAndTrailingNonCharacters(mergedKeys);
        String[] associatedElementKeys = mergedKeys.split(",");
        item.addAssociatedItemKeys(associatedElementKeys);

        if(currentMap.containsKey("Epic Link")){
            item.addAssociatedItemKeys(currentMap.get("Epic Link"));
        }
    }

    private static String removeLeadingAndTrailingNonCharacters(String str) {
        String result = removeLeadingNonCharacters(str);
        result = removeTrailingNonCharacters(result);
        return result;
    }

    private static String removeLeadingNonCharacters(String str) {
        if(str == null) return "";
        int begin = 0;
        for (Character c: str.toCharArray()) {
            if(!Character.isLetterOrDigit(c)) begin ++;
            else break;
        }
        return begin < str.length() ? str.substring(begin) : "";
    }

    private static String removeTrailingNonCharacters(String str) {
        str = removeLeadingNonCharacters(new StringBuilder(str).reverse().toString());
        return new StringBuilder(str).reverse().toString();
    }

    private static Maintenance buildMaintenanceInfo(Map<String, String> currentMap) {
        String reporter        = currentMap.get("reporter");
        String assignee        = currentMap.get("assignee");
        LocalDate creationDate = new DateBuilder(currentMap.get("created")).buildLocalDate();
        LocalDate lastUpdate   = new DateBuilder(currentMap.get("updated")).buildLocalDate();
        return new Maintenance(reporter, assignee, creationDate, lastUpdate);
    }
}