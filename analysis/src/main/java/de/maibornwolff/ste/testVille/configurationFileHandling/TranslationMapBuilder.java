package de.maibornwolff.ste.testVille.configurationFileHandling;

import de.maibornwolff.ste.testVille.inputFileParsing.common.ManagementTool;
import de.maibornwolff.ste.testVille.inputFileParsing.common.Pair;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

/**
 * This class contains methods to build a translation map based of a configuration file.
 *
 * The externally visible method  returns a translation map,
 * with which the translation of extracted data (fields) is performed.
 *
 * Before building the translation map the given configuration file is checked with the {@link ConfigurationFileValidator}
 *
 * @since   2.0.0
 *
 * (c) maibornwolff, 2018
 */
public class TranslationMapBuilder {

    private TranslationMap translationMap;
    private Map<String, Integer> priorityRanking;

    public TranslationMapBuilder(String configFilePath, ManagementTool managementTool) throws Exception {
        ConfigurationFileValidator.validateConfigurationFile(configFilePath, managementTool);
        this.translationMap  = buildTranslationMap(configFilePath);
        this.priorityRanking = this.buildPriorityRanking(managementTool);
    }

    private Map<String, Integer> buildPriorityRanking(ManagementTool managementTool) {
        Map<String, Integer> priorityTranslationMap = null;
        if(managementTool == ManagementTool.JIRA_XRAY) {
            priorityTranslationMap = this.translationMap.getTranslationInfoOf("priority");
        } else if (managementTool == ManagementTool.HP_ALM) {
            priorityTranslationMap = this.translationMap.getTranslationInfoOf("testcasepriority");
        }
        return PriorityRankingBuilder.buildPriorityRankingMap(priorityTranslationMap);
    }

    public TranslationMap getTranslationMap() {
        return translationMap;
    }

    public Map<String, Integer> getPriorityRanking() {
        return priorityRanking;
    }

    private static TranslationMap buildTranslationMap(String configFilePath) throws ParserConfigurationException, IOException, SAXException {
        Node root = buildDocumentNodeFrom(configFilePath);
        List<Node> allAvailableNodes = extractAllFieldNodes(root);
        return buildTranslationInfoFrom(allAvailableNodes);
    }

    public static Node buildDocumentNodeFrom(String xmlFilePath) throws ParserConfigurationException, IOException, SAXException {
        return DocumentBuilderFactory
                .newInstance()
                .newDocumentBuilder()
                .parse(new File(xmlFilePath));
    }

    private static List<Node> extractAllFieldNodes(Node node) {
        return extractElementNodesByName(node, "field");
    }

    private static TranslationMap buildTranslationInfoFrom(List<Node> fieldNodes) {
        TranslationMap tm = new TranslationMap();
        fieldNodes.forEach(x -> tm.addNewMetricTranslation(getFieldName(x), buildTranslationInfoFrom(x)));
        return tm;
    }

    private static String getFieldName(Node fieldNode) {
        return fieldNode
                .getAttributes()
                .getNamedItem("name")
                .getNodeValue()
                .trim();
    }

    private static Map<String, Integer> buildTranslationInfoFrom(Node fieldNode) {
        List<Node> containedValueTranslationNodes = extractValueTranslationNodes(fieldNode);
        List<Pair<String, Integer>> translationPairs = buildValueTranslationPairs(containedValueTranslationNodes);
        return transformTranslationPairsToTranslationMap(translationPairs);
    }

    private static List<Node> extractValueTranslationNodes(Node node) {
        return extractElementNodesByName(node, "valueTranslation");
    }

    private static List<Pair<String, Integer>> buildValueTranslationPairs(List<Node> valueTranslationNodes) {
        return valueTranslationNodes
                .stream()
                .map(TranslationMapBuilder::buildValueTranslationPair)
                .collect(Collectors.toList());
    }

    private static Pair<String, Integer> buildValueTranslationPair(Node valueTranslationNode) {
        NamedNodeMap attributes  = valueTranslationNode.getAttributes();
        String translatableValue = attributes.getNamedItem("stringValue").getNodeValue();
        int translatedValue      = Integer.valueOf(attributes.getNamedItem("numValue").getNodeValue());
        return new Pair<>(translatableValue, translatedValue);
    }


    private static List<Node> extractElementNodesByName(Node node, String searchedNodeName) {
        List<Node> qualifiedNodes = new ArrayList<>();
        Predicate<Node> qualificationPredicate = buildQualificationPredicate(searchedNodeName);
        extractQualifiedElementNodes(node, qualificationPredicate, qualifiedNodes);
        return qualifiedNodes;
    }

    private static Predicate<Node> buildQualificationPredicate(String nodeName) {
        return x -> (x.getNodeType() == Node.ELEMENT_NODE) && x.getNodeName().trim().equals(nodeName);
    }

    public static List<Node> extractQualifiedElementNodes(Node node, Predicate<Node> qualificationPredicate) {
        List<Node> qualifiedNodes = new ArrayList<>();
        extractQualifiedElementNodes(node, qualificationPredicate, qualifiedNodes);
        return qualifiedNodes;
    }

    private static void extractQualifiedElementNodes(Node node, Predicate<Node> qualificationPredicate, List<Node> qualifiedNodes) {
        if(qualificationPredicate.test(node)) {
            qualifiedNodes.add(node);
        }

        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            extractQualifiedElementNodes(children.item(i), qualificationPredicate, qualifiedNodes);
        }
    }

    private static Map<String, Integer> transformTranslationPairsToTranslationMap(List<Pair<String, Integer>> pairs) {
        Map<String, Integer> translationMap = new HashMap<>();
        for (Pair<String, Integer> p: pairs) {
            translationMap.putIfAbsent(p.getFirst(), p.getSecond());
        }
        return translationMap;
    }
}