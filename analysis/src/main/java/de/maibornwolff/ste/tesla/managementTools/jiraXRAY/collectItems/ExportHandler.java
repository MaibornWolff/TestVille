package de.maibornwolff.ste.tesla.managementTools.jiraXRAY.collectItems;


import de.maibornwolff.ste.tesla.managementTools.jiraXRAY.component.*;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class handles all extracted items through the {@link ItemCollector}.
 * Provides method that returns the extracted item.
 *
 * @since 2.0.0
 *
 * (c) maibornwolff, TestVile, 2018
 */
public class ExportHandler{

    private final String     exportFileName;
    private final List<Item> inExportAvailableItems;

    public ExportHandler(String fileName) throws Exception {
        this.exportFileName = fileName;
        this.inExportAvailableItems = this.extractAllItems();
        this.completeTestCaseMapWithExecutionCount(); // adds countExecution as property for each TestCase.
        this.associateEpicsToTheirTestCases();       // associates the association Epic to TestCase (1 ---> *).
    }

    /**
     * this method start the state-machine {@link ItemCollector} and runs it on the input file {@link #exportFileName}.
     * @return           The List of all extracted Items from the {@link #exportFileName}.
     * @throws Exception FileNotFoundException or XmlNonConformException.
     */
    private List<Item> extractAllItems() throws Exception{
        ItemCollector itemCollector = new ItemCollector();
        SAXParserFactory.newInstance().newSAXParser().parse(new File(this.exportFileName), itemCollector);
        return itemCollector.getCollectedItems();
    }

    /**
     *
     * @return A List of all extracted TestSets from the {@link #exportFileName}.
     */
    private List<TestSet> getAllTestSets() {
        return this.getAllMatchedItems(x -> x.getItemTyp() == ItemTyp.TESTSET)
                .map(x -> (TestSet) x)
                .collect(Collectors.toList())
        ;
    }

    /**
     *
     * @return A List of all extracted TestEpics from the {@link #exportFileName}.
     */
    public List<Epic> getAllEpics() {
        return this.getAllMatchedItems(x -> x.getItemTyp() == ItemTyp.EPIC)
                .map(x -> (Epic) x)
                .collect(Collectors.toList())
        ;
    }

    /**
     *
     * @return A List of all extracted TestCases from the {@link #exportFileName}.
     */
    public List<TestCase> getAllTestCases() {
        return this.getAllMatchedItems(x -> x.getItemTyp() == ItemTyp.TESTCASE)
                .map(x -> (TestCase) x)
                .collect(Collectors.toList())
        ;
    }

    /**
     *
     * @return A List of all extracted TestExecutions from the {@link #exportFileName}.
     */
    private List<TestExecution> getAllTestExecutions() {
        return this.getAllMatchedItems(x -> x.getItemTyp() == ItemTyp.TESTEXECUTION)
                .map(x -> (TestExecution) x)
                .collect(Collectors.toList())
        ;
    }

    /**
     * This method produces a stream of items that match the given predicate.
     * @param toMatch : Qualification predicate to match.
     * @return        : All matched Items as stream.
     */
    private Stream<Item> getAllMatchedItems(Predicate<Item> toMatch) {
        return this.inExportAvailableItems.stream().filter(toMatch);
    }

    /**
     * resolves the association Epic to TestSets (1 ---> *). Shows the Epic epic
     * all TestSets that are connected to him.
     * @param epic Epic whose connected TestSets must be found.
     * @return     A List of all TestSets associated to .
     */
    private List<TestSet> showEpicTheirAssociatedTestSets(Epic epic) {
        List<TestSet> allTestSets = this.getAllTestSets();
        Predicate<TestSet> selectionPredicate = createPredicateToAssociateEpicAndTestSets(epic.getKey());
        Stream<TestSet> selectedTestSets      = allTestSets.stream().filter(selectionPredicate);
        return selectedTestSets.collect(Collectors.toList());
    }

    /**
     * resolves the association Epic to TestCases (1 ---> *). Shows the Epic epic
     * all TestCases that are connected to him.
     * @param epic Epic whose connected TestCases must be found.
     */
    private void showEpicTheirAssociatedTestCases(Epic epic) {
        List<TestSet> associatedTestSets = showEpicTheirAssociatedTestSets(epic);

        List<TestCase> indirectAssociatedTests = associatedTestSets
                .stream()
                .map(this::showTestSetTheirAssociatedTestCases)
                .reduce(new LinkedList<>(), (x, y) -> {x.addAll(y); return x;})
        ;

        List<TestCase> allTestCases = this.getAllTestCases();
        List<TestCase> directAssociatedTests = allTestCases.stream()
                .filter(x -> x.getAssociatedElementKeys().contains(epic.getKey()))
                .collect(Collectors.toList())
        ;

        epic.addAllAssociatedElements(indirectAssociatedTests);
        epic.addAllAssociatedElements(directAssociatedTests);
    }

    /**
     * resolves the association TestSet to TestCases (1 ---> *). Shows the TestSet testSet
     * all TestCases that are connected to him.
     * @param testSet TestSet whose connected TestCases must be found.
     * @return        A List of all TestCases associated to testSet.
     */
    private List<TestCase> showTestSetTheirAssociatedTestCases(TestSet testSet) {
        List<TestCase> allTestCases = this.getAllTestCases();
        Predicate<TestCase> selectionPredicate = createPredicateToAssociateTestSetAndTheirTestCase(testSet.getKey());
        Stream<TestCase> associatedTestCases   = allTestCases.stream().filter(selectionPredicate);
        return associatedTestCases.collect(Collectors.toList());
    }

    /**
     * Creates a predicate to associate a Epic to TestSets (1 ---> *).
     * @param epicKey Key of the Epic that must be associated.
     * @return        Predicate to check which TestSets belong to the Epic.
     */
    private static Predicate<TestSet> createPredicateToAssociateEpicAndTestSets(String epicKey) {
        return x -> x.getAssociatedElementKeys().contains(epicKey);
    }

    /**
     * Creates a predicate to associate a TestSet to TestCases (1 ---> *).
     * @param testSetKey Key of the TestSet that must be associated.
     * @return           Predicate to check which TestCase belong to the TestSet.
     */
    private static Predicate<TestCase> createPredicateToAssociateTestSetAndTheirTestCase(String testSetKey) {
        return x -> x.getAssociatedElementKeys().contains(testSetKey);
    }

    /**
     * resolves the association Epics to TestCases (1 ---> *). Shows each epic
     * all TestCase that are connected to him.
     */
    private void associateEpicsToTheirTestCases() {
        Set<TestCase> alreadyAssociatedTests = new TreeSet<>();
        for (Epic epic: this.getAllEpics()) {
            this.showEpicTheirAssociatedTestCases(epic);
            alreadyAssociatedTests.addAll(epic.getAssociatedItems());
        }
        this.createDummyEpicForLooseTestCase(alreadyAssociatedTests);
    }

    /**
     * This method associates all loose TestCases to a dummy Epic.
     * @param alreadyAssociatedTestCases contains non-loose TestCases.
     */
    private void createDummyEpicForLooseTestCase(Set<TestCase> alreadyAssociatedTestCases) {
        List<TestCase> looseTestCases = getAllTestCases()
                .stream()
                .filter(x -> !alreadyAssociatedTestCases.contains(x))
                .collect(Collectors.toList())
        ;

        if(!looseTestCases.isEmpty()) {
            Epic dummyEpic = createDummyEpic();
            dummyEpic.addAllAssociatedElements(looseTestCases);
            this.inExportAvailableItems.add(dummyEpic);
        }
    }

    /**
     * creates a dummy Epic.
     * @return A Dummy Epic.
     */
    private static Epic createDummyEpic() {
        Epic dummyEpic = new Epic();
        dummyEpic.setKey("dummyKey-1");
        dummyEpic.setName("DummyEpic");
        dummyEpic.setPriority("Minor");
        return dummyEpic;
    }

    /**
     * This method produce a list of keys of executed TestCases. The resulting can contain
     * a key several times when the correspondent test was executed several times.
     * @return List of TestCase keys.
     */
    private  List<String> mergeExecutedTestcaseKeys() {
        return this.getAllTestExecutions()
                .stream()
                .map(Item::getAssociatedElementKeys)
                .reduce(new LinkedList<>(), (x, y) -> {x.addAll(y); return x;})
        ;
    }

    /**
     * This method build a Test execution Map (TestCaseKey, NumberOfExecution).
     * @return A Map to map each executed TestCase key to the correspondent number of executions.
     */
    private Map<String, Integer> buildMapForTestExecution() {
        Map<String, Integer> countExecutionMap = new HashMap<>();
        for(String testCaseKey: this.mergeExecutedTestcaseKeys()) {
            if(!countExecutionMap.containsKey(testCaseKey)) {
                countExecutionMap.put(testCaseKey, 1);
            }else{
                int oldValue = countExecutionMap.get(testCaseKey);
                countExecutionMap.replace(testCaseKey, oldValue, 1 + oldValue);
            }
        }
        return countExecutionMap;
    }

    /**
     * This method is used to complete the propertyMap of TestCase with the property countExecution.
     * This new property will be later transform to a metric for TestCases.
     */
    private void completeTestCaseMapWithExecutionCount() {
        Map<String, Integer> execMap = this.buildMapForTestExecution();
        for(TestCase testCase: this.getAllTestCases()) {
            Integer maybeExec = execMap.get(testCase.getKey());
            if(maybeExec == null) testCase.addNewProperty("countExecution", "0");
            else testCase.addNewProperty("countExecution", maybeExec.toString());
        }
    }
}