package de.maibornwolff.ste.testVille.domainModell;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.junit.jupiter.api.Assertions.*;

class MaintenanceTest {

    @DisplayName("Should correct invalid maintenance data by name")
    @Test
    void createMaintenanceWithInvalidName() {
        // arrange
        Maintenance actual = new Maintenance("", "JNiada", LocalDate.of(1994, 12, 12), LocalDate.of(1994, 12, 23));
        Maintenance expected = new Maintenance("UNKNOWN", "JNiada", LocalDate.of(1994, 12, 12), LocalDate.of(1994, 12, 23));

        // assert
        assertEquals(expected, actual, "auto-correction of invalid data failed!");
    }


    @DisplayName("Should replace invalid date with unknown")
    @Test
    void createMaintenanceWithInvalidDate() {
        // arrange
        Maintenance actual = new Maintenance("Johnny", "JNiada", null, LocalDate.of(1994, 12, 23));
        String expected = "\"reporter\": \"Johnny\", \"assignee\": \"JNiada\", \"created\": \"UNKNOWN\", \"updated\": \"1994-12-23\"";

        // assert
        assertEquals(expected, actual.toString(), "auto-correction of invalid data failed!");
    }

}