package de.maibornwolff.ste.testVille.managementTools.hpALM.component;

import de.maibornwolff.ste.testVille.managementTools.common.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import static java.util.List.of;
import static org.junit.jupiter.api.Assertions.assertEquals;

class RequirementTest {

    private Requirement requirement;

    @BeforeEach
    void setUp(){
        requirement = new Requirement();
        requirement.addNewTestCase(new TestCase());
    }

    @TestFactory
    Collection<DynamicTest> equalsTest() {
        Component component = new TestCase();
        DynamicTest dt1 = DynamicTest.dynamicTest("Requirements must be different",
                () -> assertEquals(false, requirement.equals(component)));

        DynamicTest dt2 = DynamicTest.dynamicTest("Requirements must be equals",
                () -> assertEquals(true, requirement.equals(new Requirement())));

        Requirement req = new Requirement();
        req.setId(23);

        DynamicTest dt3 = DynamicTest.dynamicTest("Requirements must be equals",
                () -> assertEquals(false, requirement.equals(req)));

        return of(dt1, dt2, dt3);
    }

    @TestFactory
    Collection<DynamicTest> repartitionCheck() {
        //arrange
        TestCase tc1 = new TestCase();
        tc1.setPriority("1-Critical");
        tc1.setName("tc1");
        TestCase tc2 = new TestCase();
        tc2.setPriority("1-Critical");
        tc2.setName("tc2");

        TestCase tc3 = new TestCase();
        tc3.setPriority("2-High");
        tc3.setName("tc3");
        TestCase tc4 = new TestCase();
        tc4.setPriority("2-High");
        tc4.setName("tc4");

        TestCase tc5 = new TestCase();
        tc5.setPriority("3-Medium");
        tc5.setName("tc5");
        TestCase tc6 = new TestCase();
        tc6.setPriority("3-Medium");
        tc6.setName("tc6");

        requirement.addNewTestCase(tc6);
        requirement.addNewTestCase(tc3);
        requirement.addNewTestCase(tc2);
        requirement.addNewTestCase(tc1);
        requirement.addNewTestCase(tc5);
        requirement.addNewTestCase(tc4);

        //act & assert
        List<Pair<String, List<TestCase>>> pairList = requirement.testCaseRepartitionByPriority();
        DynamicTest dt1 = DynamicTest.dynamicTest("4 partitions",
                () -> assertEquals(4, pairList.size()));

        DynamicTest dt2 = DynamicTest.dynamicTest("the third group in the partition must be [tc3, tc4] ",
                () -> assertEquals("tc3tc4",
                        pairList.get(2).getSecond().stream().map(Component::getName).reduce("", (x, y) -> x+y)))
                ;

        DynamicTest dt3 = DynamicTest.dynamicTest("the scd group in the partition must be [tc3, tc4] ",
                () -> assertEquals("tc6tc5",
                        pairList.get(1).getSecond().stream().map(Component::getName).reduce("", (x, y) -> x+y)))
                ;

        DynamicTest dt4 = DynamicTest.dynamicTest("the list of associated test must be [tc6, tc3, tc2, tc1, tc5, tc4]",
                () -> assertEquals("tc6tc3tc2tc1tc5tc4",
                        requirement.getMyTests().stream().map(Component::getName).reduce("", (x, y) -> x+y)))
                ;
        return List.of(dt1, dt2, dt3, dt4);
    }


    @Test
    void stringRepresentationForGroupedReq() {
        //arrange
        final String expected = "{\"name\": \"4-Low\",\"id\": \"4-Low9091\",\"type\": \"requirementCategory\",\"priority\":\"4::4-Low\",\"attributes\": {},\"children\":\n[";
        final String actual   = Requirement.produceRequirementCategoryHead("4-Low", 9091).toString();

        //assert
        assertEquals(expected, actual, "the head of the string representation for grouped requirement is incorrect");
    }

    @Test
    void strRepresentationForGroupedTestCase() {
        final String expected   = "{\"name\": \"1-Critical\",\"id\": \"1-Critical1229\",\"type\": \"testCaseCategory\",\"priority\":\"1::1-Critical\",\"attributes\": {},\"children\":\n[]}";
        final StringBuilder act = Requirement.categoryOfTestCasesAsStringForIO(1229, new Pair<>("1-Critical", new ArrayList<>()), new ArrayList<>());
        final String actual     = act.toString();
        assertEquals(expected, actual, "the string representation of grouped tests is incorrect");
    }

    @Test
    void strRepresentationOfRequirement() {
        // arrange
        final String expected = "{\"name\": \"reqII || id: 1294\",\"id\": \"29\",\"type\": \"requirement\",\"priority\":\"1::1-Critical\",\"attributes\":{},\"children\":\n[]}";
        final Requirement act = new Requirement();

        // act
        act.setComponentAttributes("requirementName", "reqII");
        act.setComponentAttributes("requirementId", "1294");
        act.setLocalID(29);
        act.setComponentAttributes("Priority", "1-Critical");
        final String actual = act.requirementAsStringForIO(new ArrayList<>()).toString();
        //assert

        assertEquals(expected, actual, "the string representation of an requirement is incorrect");
    }

}