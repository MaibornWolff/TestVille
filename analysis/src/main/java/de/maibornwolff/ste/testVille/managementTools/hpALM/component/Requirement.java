package de.maibornwolff.ste.testVille.managementTools.hpALM.component;

import de.maibornwolff.ste.testVille.managementTools.common.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Requirement extends Component{

    /* All to this requirement associated TestCases */
    private final List<TestCase> myTests;

    /**
     * To build a new and default Requirement
     */
    public Requirement() {
        super(-1);
        this.myTests = new ArrayList<>();
    }

    /**
     * this method add a new associated test.xml
     * @param tc the id of the test.xml to be add to the list of associated tests
     */
    public void addNewTestCase(TestCase tc) {
        this.myTests.add(tc);
    }

    /**
     * @return the list of the associated tests
     */
    public List<TestCase> getMyTests() {
        return this.myTests;
    }

    /**
     * define how we compare two Requirement. this method use the id of Requirement for comparison.
     * @param o the Component we want compare
     * @return value equal zero by same id otherwise another value
     */
    private int compareTo(Component o) {

        if((this.getId() == o.getId())) {
            return 0;
        }
        return (this.getId() - o.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hash(myTests);
    }

    @Override
    public boolean equals(Object obj) {

        if(!(obj instanceof Requirement)) {
            return false;
        }

        Requirement req = (Requirement)obj;
        return (this.compareTo(req) == 0);
    }

    /**
     * This method is used for the repartition of all known (to this requirement associated testCases) by priority.
     * @return an List of Pairs(CategoryName, ListOfTestCases).
     */
    List<Pair<String, List<TestCase>>> testCaseRepartitionByPriority() {

        List<Pair<String, List<TestCase>>> result = new ArrayList<>();
        int index;

        for (TestCase tc: this.myTests) {

            if ((index = existenceCheck(result, tc.getPriority())) != -1) {
                //result.get(index).second.add(tc);
                result.get(index).applyFunctionToSecond(x -> { x.add(tc); return x;});
            } else {
               Pair<String, List<TestCase>> newP = new Pair<>(tc.getPriority(), new ArrayList<>());
               //newP.second.add(tc);
                newP.applyFunctionToSecond(x -> { x.add(tc); return x; });
               result.add(newP);
            }

        }

        return result;
    }

    /**
     * This method is used to check the existence of an given category.
     * @param categories all known categories.
     * @param str        name of the searched category
     * @return           the index of the category by success of -1.
     */
    private static int existenceCheck(List<Pair<String, List<TestCase>>> categories, String str) {

        int index = 0;
        for (Pair<String, List<TestCase>> p: categories) {
            if(p.getFirst().equals(str)) return index;
            index ++;
        }
        return -1;
    }

    /**
     * This method create an StringBuilder-representation of an requirement.
     * @param testPropertyNames the name of the stored properties of testCases.
     * @return                  an StringBuilder that can be write in the json-visualization-file.
     */
    public StringBuilder requirementAsStringForIO(List<String> testPropertyNames) {

        StringBuilder result = new StringBuilder();
        result.append("{\"name\": \"").append(this.getName()).append(" || id: ").append(getId()).append("\",");
        result.append("\"id\": \"")   .append(this.getLocalID()).append("\",");
        result.append("\"type\": \"requirement\",");
        result.append("\"priority\":\"").append(Component.correctPriorityName(this.getPriority())).append("\",");
        result.append("\"attributes\":{},");
        result.append("\"children\":\n[");

        List<Pair<String, List<TestCase>>> repartition = this.testCaseRepartitionByPriority();
        StringBuilder subResult = repartition.stream()
                .map(x -> categoryOfTestCasesAsStringForIO(this.getLocalID(), x, testPropertyNames))
                .reduce(new StringBuilder(), (x, y) -> x.length() == 0 ? x.append(y) : x.append(", ").append(y))
        ;
        result.append(subResult).append("]}");
        return result;
    }

    /**
     * This function is used to generate an StringBuilder-representation of an category of TestCases.
     * @param requirementId      the id of the current requirement.
     * @param pair               the category of TestCases.
     * @param testPropertyNames  the names of the properties of each TestCases.
     * @return                   an StringBuilder than can be write in the json-visualization-file.
     */
    static StringBuilder categoryOfTestCasesAsStringForIO(int requirementId, Pair<String, List<TestCase>> pair,
                                                                 List<String> testPropertyNames) {
        StringBuilder buffer = new StringBuilder();
        String categoryName = pair.getFirst();
        buffer.append("{\"name\": \"").append(categoryName).append("\",");
        buffer.append("\"id\": \"")   .append(categoryName).append(requirementId).append("\",");
        buffer.append("\"type\": \"testCaseCategory\",");
        buffer.append("\"priority\":\"").append(Component.correctPriorityName(categoryName)).append("\",");
        buffer.append("\"attributes\": {},");
        buffer.append("\"children\":\n[");

        StringBuilder subBuffer = pair.getSecond().stream()
                .map(x -> x.testCaseAsStringForIO(testPropertyNames))
                .reduce(new StringBuilder(), (x, y) -> x.length() == 0 ? y : x.append(", ").append(y))
        ;
        buffer.append(subBuffer).append("]}");
        return buffer;
    }


    public static StringBuilder produceRequirementCategoryHead(String categoryName, int id) {

        StringBuilder buffer = new StringBuilder();
        buffer.append("{\"name\": \"").append(categoryName).append("\",");
        buffer.append("\"id\": \"")   .append(categoryName).append(id).append("\",");
        buffer.append("\"type\": \"requirementCategory\",");
        buffer.append("\"priority\":\"").append(Component.correctPriorityName(categoryName)).append("\",");
        buffer.append("\"attributes\": {},");
        buffer.append("\"children\":\n[");

        return buffer;
    }

}