package de.maibornwolff.ste.tesla.managementTools.jiraXRAY.helpers;

import de.maibornwolff.ste.tesla.managementTools.common.Pair;
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
        Map<String, Map<String, Integer>> expectedMap = new HashMap<>();
        Map<String, Map<String, Integer>>   actualMap = null;

        // act
        expectedMap.putIfAbsent("status", buildFirstHashMapFrom(firstPairs));
        expectedMap.putIfAbsent("testrunstatus", buildFirstHashMapFrom(firstPairs));
        expectedMap.putIfAbsent("priority", buildFirstHashMapFrom(secondPairs));

        try {
            actualMap = TranslationMapBuilder.buildHashMapFromXmlDocument("./src/test/resources/defaultJiraXrayConfigFile.xml");
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.err.println("TranslationMapBuilderTest can't run!");
        }

        // assert
        assertEquals(expectedMap, actualMap, "created TranslationMap is invalid!");
    }

    private HashMap<String, Integer> buildFirstHashMapFrom(List<Pair<String, Integer>> toTransform) {
        HashMap<String, Integer> map = new HashMap<>();
        for (Pair<String, Integer> p: toTransform) {
            map.putIfAbsent(p.getFirst().toLowerCase(), p.getSecond());
        }
        return map;
    }

}