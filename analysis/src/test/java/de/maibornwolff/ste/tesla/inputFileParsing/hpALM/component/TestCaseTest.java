package de.maibornwolff.ste.tesla.inputFileParsing.hpALM.component;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class TestCaseTest {

    @Test
    void strRepresentationOfATest() {
        // arrange
        TestCase test = new TestCase();
        final String expected = "{\"name\":\"test is funny || id: 222\",\"type\": \"test\",\"priority\":\"2::2-high\",\"id\":\"111\",\"attributes\":{\"testSteps\":2, \"runCount\":3, \"foundedBugs\":5}}";

        // act
        test.setLocalID(111);
        test.setName("test is funny");
        test.setComponentAttributes("testId", "222");
        test.setComponentAttributes("Priority", "2-high");

        test.addNewProperty("2");
        test.addNewProperty("3");
        test.addNewProperty("5");

        final String actual = test.testCaseAsStringForIO(List.of("testSteps", "runCount", "foundedBugs")).toString();

        // assert
        assertEquals(expected, actual, "the string representation of a testCase is incorrect");
    }


}