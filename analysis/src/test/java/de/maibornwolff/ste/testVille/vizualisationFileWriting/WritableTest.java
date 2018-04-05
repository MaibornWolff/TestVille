package de.maibornwolff.ste.testVille.vizualisationFileWriting;

import de.maibornwolff.ste.testVille.domainModell.ComposedItem;
import de.maibornwolff.ste.testVille.domainModell.TestCase;
import de.maibornwolff.ste.testVille.domainModell.jiraXray.Epic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.*;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@DisplayName("Writables string representation checks")
class WritableTest {
    private TestCase tc1;
    private TestCase tc2;

    @BeforeAll
    void setUp() {

        tc1 = new TestCase(123);
        tc1.setName("funny");
        tc1.setKey("tcü\"1");
        tc1.setPriority("Major");
        tc1.addNewProperty("countExecutions", "34");
        tc1.addNewProperty("updated", "23");
        tc1.addNewUntranslatableField("reporte\"r", "JNiada");
        tc1.addNewUntranslatableField("assignee", "JNiada");

        tc2 = new TestCase(32);
        tc2.setName("should work");
        tc2.setKey("tp'1");
        tc2.setPriority("Minor");
        tc2.addNewProperty("countExecutions", "11");
        tc2.addNewProperty("updated", "2");
    }

    @Test
    @DisplayName("it should produce an valid string representation of a TestCase")
    void stringRepresentationOfItemCheck() {
        //arrange
        String expected   = "{\"name\":\"funny(id:tcü1)\",\"id\":\"123\",\"type\":\"TESTCASE\",\"priority\":\"Major\"" +
                ",\"assignee\":\"JNiada\",\"reporter\":\"JNiada\",\"attributes\":{\"countExecutions\":34,\"updated\":23},\"children\":[]}";

        // act
        String actual = tc1.produceWritableStringRepresentation().toString().replaceAll("\\s+", "");

        //assert
        assertEquals(expected, actual, "Invalid String-representation of TestCase");
    }

    @Test
    @DisplayName("it should produce an valid string representation of a Epic")
    void stringRepresentationOfComposedItemCheck() {
        //arrange
        ComposedItem epic = new Epic(123);
        String expected   = "{\"name\":\"TestContainer(id:e12)\",\"id\":\"123\",\"type\":\"EPIC\",\"priority\":\"Major\"" +
                ",\"assignee\":\"JNiada\",\"reporter\":\"JNiada\",\"attributes\":{},\"children\":[" +
                tc1.produceWritableStringRepresentation() + /* already tested in stringRepresentationOfItemCheck */
                ","+ tc2.produceWritableStringRepresentation() + /* already tested in stringRepresentationOfItemCheck */
                "]}";

        // act
        epic.setName("TestContainer");
        epic.setKey("e12");
        epic.setPriority("Major");
        epic.addNewUntranslatableField("reporte\"r", "JNiada");
        epic.addNewUntranslatableField("assignee", "JNiada");
        epic.addAllAssociatedItems(tc1, tc2);
        String actual = epic.produceWritableStringRepresentation().toString().replaceAll("\\s+", "");
        expected = expected.replaceAll("\\s+", "");

        //assert
        assertEquals(expected, actual, "Invalid String-representation of TestCase");
    }



}