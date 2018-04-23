package de.maibornwolff.ste.testVille.domainModell;


import static org.junit.jupiter.api.Assertions.assertEquals;

class DateExtractorTest {

    /*@Test
    @DisplayName("Should simplify a string-literal contains a buildFrom")
    void dateStringSimplificationTest1() {
        String dateAsString   = "wed, 31 Jan 2018 10:09:46 +0100";
        assertEquals("31-Jan-2018", DateBuilder.simplifyDateString(dateAsString), "Simplification failed");
    }

    @Test
    @DisplayName("Should simplify a string-literal contains a buildFrom")
    void dateStringSimplificationTest2() {
        String dateAsString  = "31 Jan 2018";
        assertEquals("31-Jan-2018", DateBuilder.simplifyDateString(dateAsString), "Simplification failed");
    }

    @Test
    @DisplayName("Should simplify a string-literal contains a buildFrom")
    void dateStringSimplificationTest3() {
        String dateAsString  =  "iififo, 1994 Jan 01";
        assertEquals("01-Jan-1994", DateBuilder.simplifyDateString(dateAsString), "Simplification failed");
    }

    @Test
    @DisplayName("Should transform month string to int")
    void monthTransformTest1() {
        assertEquals(1, DateBuilder.monthTranslation("JANuary"), "unexpected month string transformation");
    }

    @Test
    @DisplayName("Should transform month string to int")
    void monthTransformTest2() {
        assertEquals(4, DateBuilder.monthTranslation("Apr"), "unexpected month string transformation");
    }

    @Test
    @DisplayName("Should recognize any invalid month")
    void negativmonthTransformTest2() {
        assertEquals(-1, DateBuilder.monthTranslation("AprIlE"), "unexpected month string transformation");
    }

    @Test
    @DisplayName("Should transform a String to a buildFrom")
    void stringToDateTest1() {
        assertEquals(LocalDate.of(2010, 12, 1),
                DateBuilder.dateStringToLocaleDate("hdhdhd, DEc 01 2010 kjdkj"),
                "created buildFrom does't pass to the string");
    }

    @Test
    @DisplayName("Should transform a String to a buildFrom")
    void stringToDateTest2() {
        assertEquals(LocalDate.of(2010, 2, 1),
                DateBuilder.dateStringToLocaleDate("hdhdhd, 2010 feb 01 kjdkj"),
                "created buildFrom does't pass to the string");
    }

    @Test
    @DisplayName("Should transform a String to a buildFrom")
    void stringToDateTest3() {
        assertEquals(LocalDate.of(2011, 9, 11),
                DateBuilder.dateStringToLocaleDate("hdhdhd, 11 SEP 2011 kjdkj"),
                "created buildFrom does't pass to the string");
    }

    @Test
    @DisplayName("Should reject strings that not contains buildFrom")
    void negativeStringToDateTest() {
        assertEquals(null,
                DateBuilder.dateStringToLocaleDate("hdhdhd, 2010 kjdkj"),
                "created buildFrom does't pass to the string"
        );
    }*/

}