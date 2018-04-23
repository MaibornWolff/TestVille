package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY;

import de.maibornwolff.ste.testVille.configurationFileHandling.TranslationMap;
import de.maibornwolff.ste.testVille.configurationFileHandling.TranslationMapBuilder;
import de.maibornwolff.ste.testVille.inputFileParsing.common.ManagementTool;
import org.junit.jupiter.api.*;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class MetricTranslatorTest {

    // global arrange
    private Map<String, String>  toBeTranslated;
    private Map<String, Integer> expectedMap;
    private Map<String, Integer> actualMap;
    private TranslationMap translationMap;

    @BeforeEach
    void setUp() {
        toBeTranslated = new HashMap<>();
        expectedMap    = new HashMap<>();
        translationMap = buildTranslationMapFromConfigFile();
    }

    @DisplayName("Should return a correct translated property map")
    @Test
    void translationTest() {

        // act
        toBeTranslated.put("status",            "fail");
        toBeTranslated.put("testRunStatus",     "aborted");
        toBeTranslated.put("priority",          "Major");
        toBeTranslated.put("should be ignored", "Major");
        toBeTranslated.put("steps",             "13");

        expectedMap.put("status",        7);
        expectedMap.put("testRunStatus", 5);
        expectedMap.put("priority",      5);
        expectedMap.put("steps",         13);

        actualMap = MetricTranslator.translateMetrics(toBeTranslated, translationMap);

        // assert
        assertEquals(expectedMap, actualMap, "Translated Map is not valid!");
    }

    @DisplayName("Should return a correct translated property map, even if the originally map contains a date")
    @TestFactory
    Collection<DynamicTest> translationTestWithDateField() {

        // arrange
        DynamicTest dt1;
        DynamicTest dt2;

        // act
        toBeTranslated.put("created",           "hahahaha -> wed, 01 sep 2013 jhdjhsjhjdhjs");
        toBeTranslated.put("testRunStatus",     "ABORTED");

        actualMap = MetricTranslator.translateMetrics(toBeTranslated, translationMap);

        // asserts
        dt1 = DynamicTest.dynamicTest("translated map should contains 2 entries",
                () -> assertEquals(2, actualMap.size())
        );

        dt2 = DynamicTest.dynamicTest("the metric created should be correct translated",
                () -> assertEquals(true, actualMap.get("created") > 0)
        );

        return List.of(dt1, dt2);
    }

    private TranslationMap buildTranslationMapFromConfigFile() {

        TranslationMap translationMap = null;
        String usedConfigFileForTest = "./src/test/resources/jiraXrayDefaultConfiguration.xml";
        try {
            translationMap = new TranslationMapBuilder(usedConfigFileForTest, ManagementTool.JIRA_XRAY).getTranslationMap();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("TranslationTest can't run!");
        }
        return translationMap;
    }

}