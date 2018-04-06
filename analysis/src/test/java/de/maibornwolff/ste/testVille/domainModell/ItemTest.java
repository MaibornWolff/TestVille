package de.maibornwolff.ste.testVille.domainModell;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import static org.junit.jupiter.api.Assertions.assertEquals;
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemTest {

    private Item item;

    @BeforeAll
    void setUp() {
        item = new TestCase(1);
        item.addNewUntranslatableField("compiler", "funny Compiler");
        item.addNewUntranslatableField("reporter", "James Bond");
        item.addNewUntranslatableField("assignee", "Tommy Lee");
    }

    @Test
    @DisplayName("it should produce an valid string representation of item untranslatable field")
    void stringRepresentationOfUntranslatableFields() {
        // arrange
        String expected = "\"assignee\": \"Tommy Lee\", \"compiler\": \"funny Compiler\", \"reporter\": \"James Bond\", ";
        String actual   = item.getUntranslatableFieldsAsString();

        //assert
        assertEquals(expected, actual, "produced string representation of untranslatable fields is invalid!");
    }
}