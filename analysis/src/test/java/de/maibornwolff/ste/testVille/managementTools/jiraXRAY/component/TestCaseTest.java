package de.maibornwolff.ste.testVille.managementTools.jiraXRAY.component;


import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import java.util.Collection;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TestCaseTest {

    @TestFactory
    Collection<DynamicTest> addTest () {
        // arrange
        TestCase tc = new TestCase();

        // act
        tc.setKey("tc-12u13");
        tc.setName("hello");
        tc.setPriority("Major");
        tc.addNewProperty("runs", "23");
        tc.addNewProperty("hey", "21");
        /*DynamicTest dt1 = DynamicTest.dynamicTest("property existence & value",
                () -> assertEquals("23", tc.getPropertyValueByName("runs")))
        ;*/
        DynamicTest dt2 = DynamicTest.dynamicTest("property number check",
                () -> assertEquals(2, tc.getPropertyNumber()))
        ;

        DynamicTest dt3 = DynamicTest.dynamicTest("TestCase name check",
                () -> assertEquals("hello", tc.getName()))
        ;

        DynamicTest dt4 = DynamicTest.dynamicTest("TestCase priority check",
                () -> assertEquals("Major", tc.getPriority()))
        ;

        DynamicTest dt5 = DynamicTest.dynamicTest("TestCase key check",
                () -> assertEquals("tc-12u13", tc.getKey()))
        ;

        DynamicTest dt6 = DynamicTest.dynamicTest("TestCase Itemtyp check",
                () -> assertEquals(ItemTyp.TESTCASE, tc.getItemTyp()))
        ;

        //assert
        return List.of(dt2, dt3, dt4, dt5, dt6);
    }

}