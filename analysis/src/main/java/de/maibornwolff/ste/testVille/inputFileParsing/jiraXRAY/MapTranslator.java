package de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY;


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
public class MapTranslator {



    public static HashMap<String, Integer> translateTestCasePropertyHashMap(Map<String, String> originalPropertyMap,
            Map<String, Map<String, Integer>> translationMap) {

        Set<Map.Entry<String, String>> set = originalPropertyMap.entrySet();
        return translateEntrySetToHashMap(set, translationMap);
    }

    private static HashMap<String, Integer> translateEntrySetToHashMap(Set<Map.Entry<String, String>> propertySet,
                                                                Map<String, Map<String, Integer>> translationMap) {

        final HashMap<String, Integer> translatedHashMap = new HashMap<>();

        propertySet.forEach(entry -> {
            String fieldName  = entry.getKey().trim();
            String fieldValue = entry.getValue().trim();
            int metricValue   = translateFieldValueToMetricValue(fieldName, fieldValue, translationMap);
            if(isValueSuccessFullTranslated(metricValue)) {
                translatedHashMap.putIfAbsent(entry.getKey().trim(), metricValue);
            }
        });
        return translatedHashMap;
    }

    private static boolean isValueSuccessFullTranslated(int translationResult) {
        return (translationResult == -2) || (translationResult >= 0);
    }

    private static int translateFieldValueToMetricValue(String fieldName, String fieldValue,
                                                        Map<String, Map<String, Integer>> translationMap){

        if (isDateProperty(fieldName, fieldValue)) return translateFieldDateValueToMetricValue(fieldValue);
        int directConversion = tryDirectConversionToMetricValue(fieldValue);
        if(directConversion >= 0){
            return directConversion;
        }else{
            return translateMappableFieldToMetricValue (fieldName, fieldValue, translationMap);
        }
    }

    private static boolean isDateProperty(String propertyName, String fieldValue) {
        return (propertyName.equals("updated") || propertyName.equals("created")) &&
                DateExtractor.isValidDate(fieldValue);
    }

    private static int translateFieldDateValueToMetricValue(String fieldValue) {
        return (int) DateExtractor.translateDateStringToMetric(fieldValue);
    }

    private static int tryDirectConversionToMetricValue(String fieldValue) {
        try {
            double r = Double.valueOf(fieldValue);
            return (int) r;
        } catch (NumberFormatException nfe) {
            return -1;
        }
    }

    private static int translateMappableFieldToMetricValue(String fieldName, String fieldValue,
                                                           Map<String, Map<String, Integer>> HashMap) {
        Map<String, Integer> internHashMap = HashMap.get(fieldName);
        Integer result = -1;
        if(internHashMap != null){
            result = internHashMap.get(fieldValue);
        }
        return (result == null) ? 0 : result;
    }
}