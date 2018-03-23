package de.maibornwolff.ste.tesla.managementTools.jiraXRAY.component;


import static org.junit.jupiter.api.Assertions.assertEquals;

class DateExtractorTest {

    /*@Test
    @DisplayName("Should simplify a string-literal contains a date")
    void dateStringSimplificationTest1() {
        String dateAsString   = "wed, 31 Jan 2018 10:09:46 +0100";
        assertEquals("31-Jan-2018", DateExtractor.simplifyDateString(dateAsString), "Simplification failed");
    }

    @Test
    @DisplayName("Should simplify a string-literal contains a date")
    void dateStringSimplificationTest2() {
        String dateAsString  = "31 Jan 2018";
        assertEquals("31-Jan-2018", DateExtractor.simplifyDateString(dateAsString), "Simplification failed");
    }

    @Test
    @DisplayName("Should simplify a string-literal contains a date")
    void dateStringSimplificationTest3() {
        String dateAsString  =  "iififo, 1994 Jan 01";
        assertEquals("01-Jan-1994", DateExtractor.simplifyDateString(dateAsString), "Simplification failed");
    }

    @Test
    @DisplayName("Should transform month string to int")
    void monthTransformTest1() {
        assertEquals(1, DateExtractor.monthTranslation("JANuary"), "unexpected month string transformation");
    }

    @Test
    @DisplayName("Should transform month string to int")
    void monthTransformTest2() {
        assertEquals(4, DateExtractor.monthTranslation("Apr"), "unexpected month string transformation");
    }

    @Test
    @DisplayName("Should recognize any invalid month")
    void negativmonthTransformTest2() {
        assertEquals(-1, DateExtractor.monthTranslation("AprIlE"), "unexpected month string transformation");
    }

    @Test
    @DisplayName("Should transform a String to a date")
    void stringToDateTest1() {
        assertEquals(LocalDate.of(2010, 12, 1),
                DateExtractor.dateStringToLocaleDate("hdhdhd, DEc 01 2010 kjdkj"),
                "created date does't pass to the string");
    }

    @Test
    @DisplayName("Should transform a String to a date")
    void stringToDateTest2() {
        assertEquals(LocalDate.of(2010, 2, 1),
                DateExtractor.dateStringToLocaleDate("hdhdhd, 2010 feb 01 kjdkj"),
                "created date does't pass to the string");
    }

    @Test
    @DisplayName("Should transform a String to a date")
    void stringToDateTest3() {
        assertEquals(LocalDate.of(2011, 9, 11),
                DateExtractor.dateStringToLocaleDate("hdhdhd, 11 SEP 2011 kjdkj"),
                "created date does't pass to the string");
    }

    @Test
    @DisplayName("Should reject strings that not contains date")
    void negativeStringToDateTest() {
        assertEquals(null,
                DateExtractor.dateStringToLocaleDate("hdhdhd, 2010 kjdkj"),
                "created date does't pass to the string"
        );
    }*/

}