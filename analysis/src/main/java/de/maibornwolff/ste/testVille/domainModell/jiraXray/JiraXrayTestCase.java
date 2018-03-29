package de.maibornwolff.ste.testVille.domainModell.jiraXray;

import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.domainModell.TestCase;
import de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY.MapTranslator;

import java.util.Collection;
import java.util.Map;

public class JiraXrayTestCase extends TestCase {

    public static Map<String, Map<String, Integer>> translationMap;

    public JiraXrayTestCase(int localKey) {
        super(localKey);
    }


    private int countTestCaseExecutions(Collection<TestExecution> allKnownExecutions) {
        String testCaseKey = this.getKey();
        return allKnownExecutions
                .stream()
                .reduce(0,
                        (x, y) -> y.getAssociatedItemKeys().contains(testCaseKey) ? x+1 : x,
                        (x, y) -> x+y
                );
    }

    public void addCountExecutionsAsTestCaseMetric(Collection<TestExecution> allKnownExecutions) {
        int countExecs = this.countTestCaseExecutions(allKnownExecutions);
        this.addNewProperty("countExecutions", ""+countExecs);
    }

    @Override
    public StringBuilder getWritableMetricsAsString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(TestCase.produceMetricsStringRepresentationHeader());
        buffer.append(produceMetricsStringRepresentationBody(translationMap));
        buffer.append(TestCase.produceMetricsStringRepresentationFooter());
        return buffer;
    }

    private String produceMetricsStringRepresentationBody(Map<String, Map<String, Integer>> translationMap) {
        Map<String, Integer> translatedMetrics = MapTranslator
                .translateTestCasePropertyHashMap(this.getPropertyMap(), translationMap);
        return Item.produceStringRepresentationOfFields(translatedMetrics.entrySet());
    }
}
