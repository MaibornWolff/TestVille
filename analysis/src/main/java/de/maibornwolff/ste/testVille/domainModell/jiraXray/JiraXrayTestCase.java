package de.maibornwolff.ste.testVille.domainModell.jiraXray;

import de.maibornwolff.ste.testVille.domainModell.TestCase;
import de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY.MapTranslator;

import java.util.Collection;
import java.util.Map;

public class JiraXrayTestCase extends TestCase {

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
}
