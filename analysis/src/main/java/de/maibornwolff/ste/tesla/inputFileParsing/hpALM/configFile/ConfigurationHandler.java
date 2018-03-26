package de.maibornwolff.ste.tesla.inputFileParsing.hpALM.configFile;
import de.maibornwolff.ste.tesla.inputFileParsing.hpALM.characteristics.Characteristic;
import de.maibornwolff.ste.tesla.inputFileParsing.common.Pair;
import de.maibornwolff.ste.tesla.inputFileParsing.hpALM.characteristics.Property;
import de.maibornwolff.ste.tesla.inputFileParsing.hpALM.characteristics.Setting;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.function.Predicate;


public class ConfigurationHandler {
    private final String configurationFileName;

    /**
     * To Construct a new ConfigurationHandler
     * @param fileName the name of the file we want to explore.
     */
    public ConfigurationHandler(String fileName) {
        this.configurationFileName = fileName;
    }

    /**
     * This method select all node in node, who satisfy predicate.
     * @param node      the node we want to explore.
     * @param predicate the predicate we use to select.
     * @param okList    the list to store node who satisfy predicate.
     */
    static void getNodeWithProperty(Node node, Predicate<Node> predicate, List<Node> okList) {
        if(predicate.test(node)) {
            okList.add(node);
        }

        NodeList kids = node.getChildNodes();
        for(int i = 0; i < kids.getLength(); i++) {
            getNodeWithProperty(kids.item(i), predicate, okList);
        }

    }

    private static List<Node> extractSettingNodes(Node node) {
        List<Node> container = new ArrayList<>();
        getNodeWithProperty(node, (x -> (x.getNodeType() == Node.ELEMENT_NODE) &&
                x.getNodeName().equals("settings")), container)
        ;
        return container;
    }

    private static List<Node> extractPropertyNodes(Node node) {
        List<Node> container = new ArrayList<>();
        getNodeWithProperty(node, (x -> (x.getNodeType() == Node.ELEMENT_NODE) &&
                x.getNodeName().equals("properties")), container)
        ;
        return container;
    }

    private static void settingNumberConstrain(List<Node> settings) throws Exception{
        if(settings.size() <= 0) {
            throw new Exception("There is no settingsNode in the configuration file!");
        }else if (settings.size() > 1) {
            throw new Exception("There are many settingsNode in the configuration file!");
        }
    }

    private static void propertyNumberConstrain(List<Node> properties) throws Exception{
        if(properties.size() <= 0) {
            throw new Exception("There is no PropertiesNode in the configuration file!");
        }else if (properties.size() > 1) {
            throw new Exception("There are many PropertiesNodes in the configuration file!");
        }
    }



    /**
     * this method is the principal method of this class. it allow to extract all characteristics of
     * an Testcase.
     * @return the list of Param, that the config-file contains.
     */
    public List<Characteristic> buildCharacteristicsList() throws Exception{
        Node node = buildDocumentNodeOfConfigFile();

        List<Node> settings = extractSettingNodes(node);
        settingNumberConstrain(settings);
        List<Characteristic> extractedSettings = extractTestSettings(settings.get(0));

        List<Node> properties = extractPropertyNodes(node);
        propertyNumberConstrain(properties);
        List<Characteristic> extractedProperties = extractTestProperties(properties.get(0));

        extractedSettings.addAll(extractedProperties);
        return extractedSettings;
    }



    private Node buildDocumentNodeOfConfigFile() throws Exception{
        try {
            return DocumentBuilderFactory
                    .newInstance()
                    .newDocumentBuilder()
                    .parse(this.getConfigurationFileName());
        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new Exception("can't parse the configuration file!");
        }

    }

    /**
     * The Settings will be extracted from the node and added to the list.
     * @param node : contains the Settings that will be extracted.
     */
    private List<Characteristic> extractTestSettings(Node node) {
        List<Characteristic> result = new ArrayList<>();
        NodeList children           = node.getChildNodes();

        for(int i = 0; i < children.getLength(); i++) {
            Node n = children.item(i);

            if ((n.getNodeType() == Node.ELEMENT_NODE) && (!n.getNodeName().trim().toLowerCase().equals("adapter"))) {
                result.add(extractSettingFromNode(n));
            }else if(n.getNodeName().trim().toLowerCase().equals("adapter")) {
                result.add(new Setting("adapter", "title", -1));
            }
        }
        return result;
    }

    private static Setting extractSettingFromNode(Node node) {
        NamedNodeMap settingAttributes = node.getAttributes();
        String settingDefaultValue = "";
        if(null != settingAttributes.getNamedItem("default")) {
            settingDefaultValue = settingAttributes.getNamedItem("default").getNodeValue();
        }
        String settingName   = settingNameCorrection(node.getNodeName().trim());
        int    settingColumn = Integer.parseInt(node.getTextContent());
        return new Setting(settingName, settingDefaultValue, settingColumn);
    }


    /**
     * The Property-Nodes will be extracted from the node.
     * @param node : contains the Property-Nodes that will be extracted.
     */
    List<Characteristic> extractTestProperties(Node node) {
        List<Node> children = nodeListToList(node.getChildNodes());

        return children.stream().reduce(new ArrayList<Characteristic>(),
                (x, y) -> { if(doesNodeContainsProperty(y)) x.add(extractPropertyFromNode(y)); return x; },
                (x, y) -> { x.addAll(y); return x;})
        ;
    }

    private static Property extractPropertyFromNode(Node node) {

        NamedNodeMap map    = node.getAttributes();
        String propertyName = map.getNamedItem("propertyName").getNodeValue();
        int propertyColumn  = Integer.parseInt(map.getNamedItem("column").getNodeValue());
        Property property   = new Property(propertyName,"", propertyColumn);

        if(map.getNamedItem("default") != null) {
            property.setDefaultValue(map.getNamedItem("default").getNodeValue());
        }

        setPropertyTranslationInformation(property, node.getChildNodes());
        return property;
    }

    private static void setPropertyTranslationInformation(Property property, NodeList maybeNumericalValueMappingNodes) {

        for(int j = 0; j < maybeNumericalValueMappingNodes.getLength(); j++) {
            Node n = maybeNumericalValueMappingNodes.item(j);

            if(doesNodeContainsNumericalValueMapping(n)) {
                NamedNodeMap map    = n.getAttributes();
                String valueToMap   = map.getNamedItem("valueKey").getNodeValue();
                int    mappedValue  = Integer.parseInt(map.getNamedItem("value").getNodeValue());
                property.completeMappingList(new Pair<>(valueToMap, mappedValue));
            }
        }
    }

    private static boolean doesNodeContainsProperty(Node node) {
        return (node.getNodeType() == Node.ELEMENT_NODE) && (node.getNodeName().trim().equals("property"));
    }

    private static boolean doesNodeContainsNumericalValueMapping(Node node) {
        return (node.getNodeType() == Node.ELEMENT_NODE)
                && (node.getNodeName().trim().toLowerCase(). equals("numericalvaluemapping"))
        ;
    }



    /**
     * @return the name of the config-file
     */
    String getConfigurationFileName() {
        return configurationFileName;
    }

    /**
     * This method sort the element of paramList. The method use the column field of each element of the list
     * to sort the list
     * @param chList the list of param to be sort
     */
    public static void sortByColumn(List<Characteristic> chList) {
        chList.sort(Comparator.comparingInt(Characteristic::getColumn));
    }


    /**
     * This Method split a list in 2 parts and return the part, that satisfy predicate.
     * @param chList      the list to be split.
     * @param predicate predicate to be satisfy
     * @return          the elements, who satisfy predicate in a list
     */
    public static List<Characteristic> splitList(List<Characteristic> chList, Predicate<Characteristic> predicate) {
        List<Characteristic> result = new ArrayList<>();

        for(Characteristic p: chList) {
            if(predicate.test(p)) {
                result.add(p);
            }
        }
        return result;
    }

    /**
     * this method correct the name of an setting.
     * @param str the name we want to correct.
     * @return    the corrected name.
     */
    static String settingNameCorrection(String str) {
        int i = str.toLowerCase().indexOf("column");
        return i == -1 ? str : str.substring(0, i);
    }


    public static String toConformStr(String str) {

        StringBuilder buffer = new StringBuilder("");
        int i = 0;

        while(i < str.length()) {
            if (((str.charAt(i)) != '\\') && ((str.charAt(i)) != '\"')) {
                buffer.append(str.charAt(i));
            }
            i++;
        }
        return buffer.toString();

    }

    private static List<Node> nodeListToList(NodeList nl) {
        List<Node> result = new ArrayList<>();
        for (int i = 0; i < nl.getLength(); i++) {
            result.add(nl.item(i));
        }
        return result;
    }
}
