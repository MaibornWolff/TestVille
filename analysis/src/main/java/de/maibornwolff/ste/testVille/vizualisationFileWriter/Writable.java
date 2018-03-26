package de.maibornwolff.ste.testVille.vizualisationFileWriter;

import de.maibornwolff.ste.testVille.managementTools.common.Pair;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This interface is a general visualization file(in json-Format) writer designed with the pattern <b>composite</b>.
 * For each management tool this interface can be used to write the visualization file. For example this interface
 * is used to generate the visualization file for Xray and hpAlm exports.
 *
 * This mean that for any extension of the project for another management tool this interface
 * can be easily use instead of re-implementing the generation of the visualization file.
 *
 * Children of the seem node are grouped by priority before writing. The interface also takes care of the
 * generation of unique ID's for each node.
 *
 * @since 2.0.0
 *
 * (c) maibornwolff, TestVille, 2018
 */
public interface Writable {
    /**
     * Returns all writables contained in this writable
     * @return List of writable children oder null when this writable has any children.
     */
    List<Writable> getWritableChildren();

    /**
     * Returns the priority of this writable.
     * @return Priority as String.
     */
    String getWritablePriority();

    /**
     * Returns the id of this writable.
     * @return ID as String.
     */
    String getWritableID();

    /**
     * Returns the name of this writable.
     * @return WritableName
     */
    String getWritableName();

    /**
     * Returns the type of this writable.
     * @return WritableType as String.
     */
    String getWritableType();

    /**
     * Returns all properties of this writable.
     * @return A hashMap contained all settings.
     */
    Map<String, Integer> getMetricsToBeWrite(Map<String, Map<String, Integer>> translationMap);

    /**
     * Returns all properties of this writable, that must be write
     * without translation.
     * @return A Map contained all untranslatable properties.
     */
    Map<String, String> getUntranslatableProperties();

    /**
     * This method produce a string representation of this writable.
     * @param idGenerator        Generate the writable id.
     * @param translationMap     Map that contains all information to translate properties of writable.
     * @param priorityRankingMap Maps priorities to their ranks.
     * @return                   String representation that can be wrote direct in the visualization file.
     */
    private StringBuilder produceWritableStringRepresentation(AtomicInteger idGenerator,
                                                              Map<String, Map<String, Integer>> translationMap,
                                                              Map<String, Integer> priorityRankingMap) {

        StringBuilder puffer = new StringBuilder("{");
        puffer.append(this.produceSettingsStringRepresentation(idGenerator, priorityRankingMap));
        puffer.append(this.produceStringRepresentationOfUntranslatableProperties());
        puffer.append(produceMetricsStringRepresentation(this.getMetricsToBeWrite(translationMap)));

        List<Writable> writables = this.getWritableChildren();
        puffer.append(", \"children\" : [");
        if((writables != null) && (!writables.isEmpty())) {
            puffer.append(produceWritablesStringRepresentation(writables, idGenerator, translationMap, priorityRankingMap));
        }
        puffer.append("]\n");

        puffer.append("}\n");
        return puffer;
    }

    /**
     * Produce a string representation of the settings of this writable to write in the visualization file.
     * @param idGenerator        Generates the writable id.
     * @param priorityRankingMap Maps priorities to their ranks.
     * @return String representation all WritableSettings.
     */
    private StringBuilder produceSettingsStringRepresentation(AtomicInteger idGenerator, Map<String, Integer> priorityRankingMap) {
        StringBuilder result = new StringBuilder();
        String priority = producePriorityStringRepresentation(this.getWritablePriority(), priorityRankingMap);
        int writableId  = idGenerator.getAndIncrement();
        result.append(produceFieldString("name", writableId + "||" + this.getWritableName()));
        result.append(", ");
        result.append(produceFieldString("id",   writableId + "||" + this.getWritableID()));
        result.append(", ");
        result.append(produceFieldString("type", this.getWritableType()));
        result.append(", ");
        result.append(produceFieldString("priority", priority));
        return result;
    }

    /**
     * This method produces a string representation of the priority to write in the visualisation file.
     * @param priority           Priority of this writable to write.
     * @param priorityRankingMap Maps priorities to their rank.
     * @return                   String representation of the priority of this writable for the visualization file.
     */
    private static String producePriorityStringRepresentation(String priority, Map<String, Integer> priorityRankingMap) {
        return priorityRankingMap.get(priority.toLowerCase()) + ":: " + priority;
    }

    /**
     * This method produce a string representation of metrics.
     * @param metricsMap contains all metrics of this writable.
     * @param <A>        Type of metricKey.
     * @param <B>        Type of metricValue
     * @return           String representation of all metrics contained in metricsMap.
     */
    private static <A, B> String produceMetricsStringRepresentation(Map<A, B> metricsMap) {
        if((metricsMap == null) || metricsMap.isEmpty()) return ", \"attributes\": {}";

        List<Map.Entry<A, B>> entries = new ArrayList<>(metricsMap.entrySet());
        Stream<String> fieldStrings   = entries.stream().map(x -> placeStringInQuotes((String)x.getKey()) + " : " + x.getValue());//produceFieldString(x.getKey(), x.getValue()));
        return ", \"attributes\": {"+fieldStrings.reduce("",  (x, y) -> x.isEmpty() ? y : x + ", " + y)+"}";
    }

    /**
     * This method produce a string representation for a field.
     * @param fieldKey   The key (mostly name) of the field.
     * @param fieldValue The value of the field.
     * @param <A>        Key type.
     * @param <B>        Value type.
     * @return           "\"fieldKey\" : \"fieldValue\""
     */
    private static <A, B> String produceFieldString(A fieldKey, B fieldValue) {
        String fieldRepresentation = "";
        String keyAsString   = correctStringContent(fieldKey.toString());
        String valueAsString = correctStringContent(fieldValue.toString());
        fieldRepresentation = fieldRepresentation.concat(placeStringInQuotes(keyAsString));
        fieldRepresentation = fieldRepresentation.concat(" : ");
        fieldRepresentation = fieldRepresentation.concat(placeStringInQuotes(valueAsString));
        return fieldRepresentation;
    }

    /**
     * This method place a given string in quotes.
     * @param str string to place in qotes.
     * @return    "str"
     */
    private static String placeStringInQuotes(String str) {
        str = str == null ? "" : str;
        return "\"" + str + "\"";
    }

    /**
     * Entry point of this interface.
     * Produces a string representation of a given list of writables(for the visualization file),
     * and this occurs like following:
     *   1 -> group the writables by priority.
     *   2 -> write the grouped writables.
     * @param writables          Writables to write in visualization file.
     * @param idGenerator        Generates id.
     * @param translationMap     Map that contains all information to translate properties of writable.
     * @param priorityRankingMap Map that maps priorities to their rank.
     * @return                   String representation of writables that can be wrote direct in the visualization file.
     */
    static StringBuilder produceWritablesStringRepresentation(List<Writable> writables,
                                                              AtomicInteger idGenerator,
                                                              Map<String, Map<String, Integer>> translationMap,
                                                              Map<String, Integer> priorityRankingMap) {

        Set<String> allPriorities = extractAllPriorities(writables);
        Stream<Pair<String, List<Writable>>> allGroups = extractAllGroupsByPriority(writables, allPriorities).stream();
        Stream<StringBuilder> strings = allGroups
                .map(x -> produceGroupedWritablesStringRepresentation(x, idGenerator, translationMap, priorityRankingMap))
                ;
        return strings.reduce(new StringBuilder(),
                (x, y) -> x.length() == 0 ? x.append(y) : x.append(", ").append(y))
                ;
    }

    /**
     * This method groups writables by priority.
     * @param writables           Writables to group
     * @param availablePriorities All know priorities.
     * @return                    List of Pairs [(Priority, ListOfWritable)].
     */
    private static List<Pair<String, List<Writable>>> extractAllGroupsByPriority(List<Writable> writables,
                                                                                 Set<String> availablePriorities){
        Stream<Pair<String, List<Writable>>> allGroups;
        allGroups = availablePriorities.stream().map(x -> extractGroupByPriority(writables, x));
        return allGroups.collect(Collectors.toList());
    }

    /**
     * This method build a new group of writable based on priority.
     * @param writables List that probably contains writables with priority priority.
     * @param priority  Priority with which the group is extracted.
     * @return          Group of writable as Pair(priority, ListOfWritables)
     */
    private static Pair<String, List<Writable>> extractGroupByPriority(List<Writable> writables, String priority) {
        Stream<Writable> qualifiedWritables = writables.stream().filter(x -> x.getWritablePriority().equals(priority));
        return new Pair<>(priority, qualifiedWritables.collect(Collectors.toList()));
    }

    /**
     * This method compute a set of all properties contained in the writableList.
     * @param writableList List of Writable to write.
     * @return             Set of priorities.
     */
    private static Set<String> extractAllPriorities(List<Writable> writableList) {
        return writableList.stream().map(Writable::getWritablePriority).collect(Collectors.toCollection(TreeSet::new));
    }

    /**
     * This method produce a String representation for a group of writables.
     * @param writableGroup   A pair(Priority, ListOfWritable): this is a group.
     * @param idGenerator     Generate the group id.
     * @param translationMap  Map that contains all information to translate properties of writable.
     * @param priorityRanking Map that maps priorities to their rank.
     * @return                String representation of the writableGroup.
     */
    private static StringBuilder produceGroupedWritablesStringRepresentation(Pair<String, List<Writable>> writableGroup,
                                                                             AtomicInteger idGenerator,
                                                                             Map<String, Map<String, Integer>> translationMap,
                                                                             Map<String, Integer> priorityRanking) {
        String groupName             = writableGroup.getFirst();
        final String groupPriority   = producePriorityStringRepresentation(groupName, priorityRanking);
        StringBuilder result         = writableGroupStringHeader(groupName, groupPriority, idGenerator);
        List<Writable> groupElements = writableGroup.getSecond();
        result.append(groupElements.stream()
                .map(x -> x.produceWritableStringRepresentation(idGenerator, translationMap, priorityRanking))
                .reduce(new StringBuilder(), (x, y) -> x.length() == 0 ? y : x.append(", ").append(y))
        );

        result.append("]}");
        return result;
    }

    /**
     * Produce the headerString of a group of Writables
     * @param  groupName    Name of the WritableGroup.
     * @param groupPriority Priority of the WritableGroup.
     * @param idGenerator   Generate the group id.
     * @return              A header of the representation of the group.
     */
    private static StringBuilder writableGroupStringHeader(String groupName, String groupPriority, AtomicInteger idGenerator) {
        StringBuilder buffer = new StringBuilder();
        int groupId = idGenerator.getAndIncrement();
        buffer.append("{\"name\": \"").append(groupId).append("||").append(groupName).append("\",");
        buffer.append("\"id\": \"").append(groupId).append("\",");
        buffer.append("\"type\": \"ItemGroup\",");
        buffer.append("\"priority\":").append("\"").append(groupPriority).append("\", ");
        buffer.append("\"attributes\": {},");
        buffer.append("\"children\":\n[");
        return buffer;
    }

    /**
     * This method checks a given string and removes all not conform character in the string.
     * @param str string to correct.
     * @return    string without not conform character.
     */
    private static String correctStringContent(String str) {
        StringBuilder puffer = new StringBuilder();
        for(Character c: str.toCharArray()) {
            if(c != '\"') puffer.append(c);
        }
        return puffer.toString();
    }

    /**
     * This method produces a string representation of untranslatable properties of this writable.
     * @return String that contains all untranslatable properties.
     */
    private String produceStringRepresentationOfUntranslatableProperties() {
        if(this.getUntranslatableProperties().isEmpty()) return "";

        Set<Map.Entry<String, String>> entries = this.getUntranslatableProperties().entrySet();
        return entries.stream()
                .reduce(new StringBuilder(),
                        (x, y) -> x.append(", ").append(produceFieldString(y.getKey(), y.getValue())),
                        (x, y) -> x.append(", ").append(y))
                .toString()
                ;
    }
}