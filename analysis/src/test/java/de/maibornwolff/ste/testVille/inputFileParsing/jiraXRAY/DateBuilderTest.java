package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.time.LocalDate;
import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DateBuilderTest {

    @DisplayName("should recognize an invalidate date")
    @Test
    void invalidDateCheck() {
        // arrange
        DateBuilder builder = new DateBuilder("Fri, 6 jkj 2018 08:41:46 +0200");

        // act && assert
        assertEquals(false, builder.containsValidDate(), "recognition of bad data failed");
    }

    @DisplayName("should build an valid date")
    @Test
    void buildOfDateCheck() {
        // arrange
        DateBuilder builder = new DateBuilder("Fri, 6 Apr 2018 08:35:43 +0200");
        LocalDate actual    = builder.buildLocalDate();
        LocalDate expected  = LocalDate.of(2018, 4, 6);

        // assert
        assertEquals(expected, actual, "built date is invalid");
    }

    @DisplayName("should validate conform dates")
    @TestFactory
    Collection<DynamicTest> datesValidation() {
        // arrange
        DateBuilder b1 = new DateBuilder("Thu, 5 Apr 2018 17:40:17 +0200");
        DateBuilder b2 = new DateBuilder("Fri, 6 Feb 2018 08:30:41 +0200");
        DateBuilder b3 = new DateBuilder("Wed, 31 Jan 2018 09:56:52 +0100");

        // act && assert
        DynamicTest dt1 = DynamicTest.dynamicTest("should validate", () -> assertEquals(true, b1.containsValidDate(), "Validation failed"));
        DynamicTest dt2 = DynamicTest.dynamicTest("should validate", () -> assertEquals(true, b2.containsValidDate(), "Validation failed"));
        DynamicTest dt3 = DynamicTest.dynamicTest("should validate", () -> assertEquals(true, b3.containsValidDate(), "Validation failed"));

        return List.of(dt1, dt2, dt3);
    }
}