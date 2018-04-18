package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY;

import de.maibornwolff.ste.testVille.configurationFileHandling.TranslationMap;
import de.maibornwolff.ste.testVille.configurationFileHandling.TranslationMapBuilder;
import de.maibornwolff.ste.testVille.inputFileParsing.common.ManagementTool;
import de.maibornwolff.ste.testVille.inputFileParsing.common.Pair;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class TranslationMapBuilderTest {
    private List<Pair<String, Integer>> firstPairs = List.of(new Pair<>("todo", 6),
            new Pair<>("pass", 1),
            new Pair<>("executing", 2),
            new Pair<>("fail", 7),
            new Pair<>("aborted", 5),
            new Pair<>("blocked", 4),
            new Pair<>("pending_validation", 3),
            new Pair<>("in specification", 7)
    );

    private List<Pair<String, Integer>> secondPairs = List.of(new Pair<>("Minor", 2),
            new Pair<>("Major", 5),
            new Pair<>("Medium", 3),
            new Pair<>("High", 4),
            new Pair<>("Trivial", 1)
    );


    @DisplayName("Should build a valid TranslationMap from an given configuration file")
    @Test
    void buildTranslationMapTest() {
        // arrange
        TranslationMap expectedMap = new TranslationMap();
        TranslationMap actualMap   = null;

        // act
        expectedMap.addNewMetricTranslation("status", buildFirstHashMapFrom(firstPairs));
        expectedMap.addNewMetricTranslation("testRunStatus", buildFirstHashMapFrom(firstPairs));
        expectedMap.addNewMetricTranslation("priority", buildFirstHashMapFrom(secondPairs));

        try {
            actualMap = new TranslationMapBuilder("./src/test/resources/jiraXrayTestConfiguration.xml", ManagementTool.JIRA_XRAY).getTranslationMap();
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("TranslationMapBuilderTest can't run!");
        }

        // assert
        assertEquals(expectedMap.translationSource, actualMap.translationSource, "created TranslationMap is invalid!");
    }

    private Map<String, Integer> buildFirstHashMapFrom(List<Pair<String, Integer>> toTransform) {
        HashMap<String, Integer> map = new HashMap<>();
        for (Pair<String, Integer> p: toTransform) {
            map.putIfAbsent(p.getFirst(), p.getSecond());
        }
        return map;
    }
}