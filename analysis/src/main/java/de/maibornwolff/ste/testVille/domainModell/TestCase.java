package de.maibornwolff.ste.testVille.domainModell;

import de.maibornwolff.ste.testVille.configurationFileHandling.TranslationMap;
import de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY.MetricTranslator;
import de.maibornwolff.ste.testVille.vizualisationFileWriting.Writable;

import java.util.*;

public class TestCase extends Item implements Writable {

    public static TranslationMap translationMap;
    public Map<String, String> propertyMap;

    public TestCase(int localKey) {
        super(localKey);
        this.propertyMap = new HashMap<>();
    }

    public void addNewProperty(String key, String value) {
        this.propertyMap.putIfAbsent(key, value);
    }

    public void setPropertyMap(Map<String, String> map) {this.propertyMap = map;}

    public void fillPropertyMap(Map<String, String> map) {
        map.forEach((key, value) -> {if(isPropertyName(key))this.propertyMap.putIfAbsent(key, value);});
    }

    private static boolean isPropertyName(String name) {
        return ! (name.equals("title") || name.equals("priority") || name.equals("key") || name.equals("type"));
    }

    private Map <String, String> getPropertyMap() {
        return propertyMap;
    }

    @Override
    public ItemTyp getType() {
        return ItemTyp.TESTCASE;
    }

    @Override
    public boolean equals(Object object) {
        if(!(object instanceof TestCase)) return false;
        TestCase testCase = (TestCase) object;
        return super.equals(object) && (this.propertyMap.equals(testCase.propertyMap));
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), propertyMap);
    }

    @Override
    public List<Writable> getWritableChildren() {
        return emptyList();
    }

    @Override
    public String getWritablePriority() {
        return this.getPriority();
    }

    @Override
    public String getWritableID() {
        return ""+this.getLocalId();
    }

    @Override
    public String getWritableName() {
        return this.getName() + " (id: "+this.getKey()+")";
    }

    @Override
    public String getWritableType() {
        return this.getType().name();
    }

    @Override
    public StringBuilder getWritableMetricsAsString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(produceMetricsStringRepresentationHeader());
        buffer.append(produceMetricsStringRepresentationBody(translationMap));
        buffer.append(produceMetricsStringRepresentationFooter());
        return buffer;
    }

    private String produceMetricsStringRepresentationBody(TranslationMap translationMap) {
        Map<String, Integer> translatedMetrics = MetricTranslator
                .translateMetrics(this.getPropertyMap(), translationMap);
        return produceMetricsStringRepresentation(translatedMetrics.entrySet());
    }

    private static <A> String produceMetricsStringRepresentation(Set<Map.Entry<String, A>> fields) {
        return fields.stream()
                .sorted(Comparator.comparing(Map.Entry::getKey)) // alphabetical sort of metrics
                .map(x -> TestCase.produceMetricStringRepresentation(x.getKey(), x.getValue()))
                .reduce("", (x, y) -> x.isEmpty() ? x.concat(y) : x.concat(", ").concat(y));
    }

    private static <A>  String produceMetricStringRepresentation(String metricName, A metricValue) {
        return "\"" + metricName + "\"" + ": " + metricValue.toString();
    }

    @Override
    public String getWritableUntranslatableFieldsAsString() {
        return super.getMaintenanceInfo();
    }

    private static String produceMetricsStringRepresentationHeader() {
        return "\"attributes\": { ";
    }

    private static String produceMetricsStringRepresentationFooter() {
        return "}";
    }

    private List<Writable> emptyList() {
        return List.of();
    }

}