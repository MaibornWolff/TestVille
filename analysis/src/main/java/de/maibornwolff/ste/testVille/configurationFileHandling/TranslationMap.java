package de.maibornwolff.ste.testVille.configurationFileHandling;

import java.util.HashMap;
import java.util.Map;

public class TranslationMap {

    public Map<String, Map<String, Integer>> translationSource;

    public TranslationMap() {
        this.translationSource = new HashMap<>();
    }

    public void addNewMetricTranslation(String metricName, Map<String, Integer> translationInfo) {
        Map<String, Integer> newTranslationInfo = new HashMap<>();
        translationInfo.forEach((name, value) -> newTranslationInfo.putIfAbsent(name.toLowerCase().trim(), value));
        this.translationSource.putIfAbsent(metricName.toLowerCase().trim(), newTranslationInfo);
    }

    public Map<String, Integer> getTranslationInfoOf(String metricName) {
        return this.translationSource.get(metricName);
    }

    public Integer translateMetric(String metricName, String metricValue) {
        Map<String, Integer> metricTranslationInfo = this.translationSource.get(metricName.toLowerCase().trim());
        if(metricTranslationInfo == null) return -1;
        return metricTranslationInfo.get(metricValue.toLowerCase().trim());
    }
}