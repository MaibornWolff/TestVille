package de.maibornwolff.ste.testVille.managementTools.hpALM.component;

import java.util.ArrayList;
import java.util.List;

public class TestCase extends Component {

    /* All properties of this TestCase */
    private final List<String> properties;

    /**
     * To create a default TestCase.
     */
    public TestCase () {
        super(-1);
        this.properties = new ArrayList<>();
    }

    /**
     * this method add a new property to propertyStr
     * @param newProperty the property to be add
     */
    public void addNewProperty(String newProperty) {
        this.properties.add(newProperty);
    }

    /**
     * This method create an StringBuilder-representation of an TestCase.
     * @param propertyNames the name of the stored properties.
     * @return              an StringBuilder that can be write in the json-visualization-file.
     */
    public StringBuilder testCaseAsStringForIO(List<String> propertyNames) {

        if(propertyNames.size() != properties.size()) {
           System.err.println("the properties and their names have different sizes");
           System.exit(1);
        }

        StringBuilder buffer = new StringBuilder();
        buffer.append("{\"name\":");
        buffer.append("\"").append(this.getName()).append(" || id: ").append(this.getId()).append("\",");
        buffer.append("\"type\": \"test\",");
        buffer.append("\"priority\":\"").append(Component.correctPriorityName(this.getPriority())).append("\",");
        buffer.append("\"id\":" + "\"").append(this.getLocalID()).append("\",");
        buffer.append("\"attributes\":{");

        int i = 0;
        for (; i < propertyNames.size() -1; i++) {
            buffer.append(placeInQuote(propertyNames.get(i))).append(":").append(this.properties.get(i)).append(", ");
        }
        buffer.append(placeInQuote(propertyNames.get(i))).append(":").append(this.properties.get(i)).append("}}");

        return buffer;
    }

}
