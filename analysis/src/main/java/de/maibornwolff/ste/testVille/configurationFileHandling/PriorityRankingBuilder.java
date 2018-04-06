package de.maibornwolff.ste.testVille.configurationFileHandling;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PriorityRankingBuilder {

    public static Map<String, Integer> buildPriorityRankingMap(Map<String, Integer> priorityTranslationMap) {
        List<Map.Entry<String, Integer>> sortedEntries = sortMapEntriesByValue(priorityTranslationMap);
        return buildPriorityRankingBasedOnSortedPriorityEntries(sortedEntries);
    }

    private static List<Map.Entry<String, Integer>> sortMapEntriesByValue(Map<String, Integer> toSort) {
        if(toSort == null) return new ArrayList<>();
        return toSort
                .entrySet()
                .stream()
                .sorted((x, y) -> y.getValue() - x.getValue())
                .collect(Collectors.toList());
    }

    private static Map<String, Integer> buildPriorityRankingBasedOnSortedPriorityEntries(List<Map.Entry<String, Integer>> sortedPriorityEntries) {
        Map<String, Integer> priorityRanking = new HashMap<>();
        for (int i = 0; i < sortedPriorityEntries.size(); i++) {
            Map.Entry<String, Integer> entry = sortedPriorityEntries.get(i);
            priorityRanking.putIfAbsent(entry.getKey(), i+1);
        }
        return priorityRanking;
    }
}
