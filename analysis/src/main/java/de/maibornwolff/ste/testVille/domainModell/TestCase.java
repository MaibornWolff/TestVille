package de.maibornwolff.ste.testVille.domainModell;

import de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY.MapTranslator;
import de.maibornwolff.ste.testVille.vizualisationFileWriting.Writable;

import java.util.*;

public class TestCase extends Item implements Writable {

    public static Map<String, Map<String, Integer>> translationMap;
    public Map<String, String> propertyMap;

    public TestCase(int localKey) {
        super(localKey);
        this.propertyMap = new HashMap<>();
    }

    public void addNewProperty(String key, String value) {
        this.propertyMap.putIfAbsent(key, value);
    }

    public void setPropertyMap(Map<String, String> map) {this.propertyMap = map;}

    private Map <String, String> getPropertyMap() {
        return propertyMap;
    }

    @Override
    public ItemTyp getItemTyp() {
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

    /*@Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("\n{ name -> ").append(this.getName()).append("\n");
        result.append("  key -> ").append(this.getKey()).append("\n");
        result.append("  priority -> ").append(this.getPriority()).append("}\n");
        this.propertyMap.forEach((String x, String y) -> result.append(x).append(" -> ").append(y).append("\n"));
        return result.toString();
    }*/

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
        return this.getItemTyp().name();
    }

    @Override
    public StringBuilder getWritableMetricsAsString() {
        StringBuilder buffer = new StringBuilder();
        buffer.append(produceMetricsStringRepresentationHeader());
        buffer.append(produceMetricsStringRepresentationBody(translationMap));
        buffer.append(produceMetricsStringRepresentationFooter());
        return buffer;
    }

    private String produceMetricsStringRepresentationBody(Map<String, Map<String, Integer>> translationMap) {
        Map<String, Integer> translatedMetrics = MapTranslator
                .translateTestCasePropertyHashMap(this.getPropertyMap(), translationMap);
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
        return super.getUntranslatableFieldsAsString();
    }

    private static String produceMetricsStringRepresentationHeader() {
        return "\"attributes\": { ";
    }

    private static String produceMetricsStringRepresentationFooter() {
        return "}";
    }

    private List<Writable> emptyList() {return List.of();}

}