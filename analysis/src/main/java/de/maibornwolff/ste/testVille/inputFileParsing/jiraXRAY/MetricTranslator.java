package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY;

import de.maibornwolff.ste.testVille.configurationFileHandling.TranslationMap;

import java.util.*;

/**
 * This class contains functionality to perform the translation of extracted data from
 * the user-export. Some fields are in exports as string available and need to be translated
 * (numeric values) to convert them to metrics.
 *
 * To perform the translation a translation map created by  is needed.
 *
 * @since 2.0.0
 *
 * (c) maibornwolff, 2018
 */
public class MetricTranslator {

    public static Map<String, Integer> translateMetrics(Map<String, String> metricsMap, TranslationMap translationMap) {
        Map<String, Integer> translatedMetrics = new HashMap<>();

        metricsMap.forEach((metricName, metricValue) -> {
            metricName  = metricName.trim();
            metricValue = metricValue.trim();
            int translatedValue = translateMetric(metricName, metricValue, translationMap);
            if(isValueSuccessFullTranslated(translatedValue)) {
                translatedMetrics.putIfAbsent(metricName, translatedValue);
            }
        });

        return translatedMetrics;
    }

    private static boolean isValueSuccessFullTranslated(int translationResult) {
        return translationResult >= 0;
    }

    private static int translateMetric(String metricName, String metricValue, TranslationMap translationMap){
        if(!isMetricNameValid(metricName)) return -1;
        if (isDateMetric(metricName, metricValue)) return translateFieldDateMetric(metricValue);
        int directConversion = tryDirectConversionOfMetric(metricValue);
        if(directConversion >= 0){
            return directConversion;
        }else{
            return translateMetricWithTranslationMap(metricName, metricValue, translationMap);
        }
    }

    private static boolean isDateMetric(String propertyName, String fieldValue) {
        return (propertyName.equals("updated") || propertyName.equals("created")) &&
                new  DateBuilder(fieldValue).containsValidDate();
    }

    private static int translateFieldDateMetric(String metricValue) {
        return (int) new DateBuilder(metricValue).computeMetric();
    }

    private static int tryDirectConversionOfMetric(String fieldValue) {
        try {
            double r = Double.valueOf(fieldValue);
            return (int) r;
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    private static int translateMetricWithTranslationMap(String metricName, String metricValue, TranslationMap tm) {
        Integer result = tm.translateMetric(metricName, metricValue);
        return result == null ? 0 : result;
    }

    private static boolean isMetricNameValid(String name) {
        if(name == null) return false;
        for(char c: name.toCharArray()) {
            if(!Character.isLetterOrDigit(c)) return false;
        }
        return true;
    }
}