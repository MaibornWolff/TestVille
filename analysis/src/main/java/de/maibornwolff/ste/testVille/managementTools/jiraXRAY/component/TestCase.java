package de.maibornwolff.ste.testVille.managementTools.jiraXRAY.component;

import de.maibornwolff.ste.testVille.managementTools.jiraXRAY.helpers.MapTranslator;
import de.maibornwolff.ste.testVille.vizualisationFileWriter.Writable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class TestCase extends Item implements Writable {

    private Map<String, String> propertyMap;

    public TestCase() {
        super();
        this.propertyMap = new HashMap<>();
    }

    public void addNewProperty(String key, String value) {
        this.propertyMap.putIfAbsent(key, value);
    }

    public void setPropertyMap(Map<String, String> map) {this.propertyMap = map;}

    int getPropertyNumber() {
        return this.propertyMap.size();
    }

    public Map <String, String> getPropertyMap() {
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

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder();
        result.append("\n{ name -> ").append(this.getName()).append("\n");
        result.append("  key -> ").append(this.getKey()).append("\n");
        result.append("  priority -> ").append(this.getPriority()).append("}\n");
        this.propertyMap.forEach((String x, String y) -> result.append(x).append(" -> ").append(y).append("\n"));
        return result.toString();
    }

    @Override
    public List<Writable> getWritableChildren() {
        return null;
    }

    @Override
    public String getWritablePriority() {
        return this.getPriority();
    }

    @Override
    public String getWritableID() {
        return this.getKey();
    }

    @Override
    public String getWritableName() {
        return this.getName();
    }

    @Override
    public String getWritableType() {
        return this.getItemTyp().name();
    }

    @Override
    public Map<String, Integer> getMetricsToBeWrite(Map<String, Map<String, Integer>> translationMap) {
        return MapTranslator.translateTestCasePropertyHashMap(this.getPropertyMap(), translationMap);
    }

    @Override
    public Map<String, String> getUntranslatableProperties() {
        return this.getUntranslatableFields();
    }
}