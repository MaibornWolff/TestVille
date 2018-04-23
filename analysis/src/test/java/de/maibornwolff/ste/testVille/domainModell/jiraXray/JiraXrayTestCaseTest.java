package de.maibornwolff.ste.testVille.domainModell.jiraXray;

import de.maibornwolff.ste.testVille.domainModell.Item;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JiraXrayTestCaseTest {

    @DisplayName("Should compute the number of executions and add it as metric")
    @TestFactory
    Collection<DynamicTest> filterEmptyCollection() {
        // arrange
        JiraXrayTestCase tc1 = new JiraXrayTestCase(1);
        tc1.setKey("tc1");

        JiraXrayTestCase tc2 = new JiraXrayTestCase(9);
        tc2.setKey("tc2");

        TestExecution te1 = new TestExecution(2);
        te1.addAssociatedItemKeys("tc1");
        te1.addAssociatedItemKeys("tc12");

        TestExecution te2 = new TestExecution(3);
        te2.addAssociatedItemKeys("tc89");
        te2.addAssociatedItemKeys("tc1");

        Collection<Item> items = List.of(tc1, te2, te1, tc2);

        // act
        tc1.addCountExecutionsAsMetric(items);
        tc2.addCountExecutionsAsMetric(items);

        //assert
        DynamicTest dt1 = DynamicTest.dynamicTest("number of executions should be 2",
                () -> assertEquals("2", tc1.propertyMap.get("countExecutions")));

        DynamicTest dt2 = DynamicTest.dynamicTest("number of executions should be 0",
                () -> assertEquals("0", tc2.propertyMap.get("countExecutions")));
        return List.of(dt1, dt2);
    }

}