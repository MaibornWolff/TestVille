package de.maibornwolff.ste.tesla.managementTools.jiraXRAY.helpers;

import de.maibornwolff.ste.tesla.managementTools.common.Pair;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * This class contains methods to build a translation map based of a configuration file.
 *
 * The externally visible method {@link #buildHashMapFromXmlDocument(String)} returns a translation map,
 * with which the translation of extracted data (fields) is performed.
 *
 * Before building the translation map the given configuration file is checked with the {@link ConfigurationFileValidator}
 *
 * @since   2.0.0
 *
 * (c) maibornwolff, 2018
 */
public class TranslationMapBuilder {

    public static Map<String, Map<String, Integer>> buildHashMapFromXmlDocument(String configurationFilePath) throws Exception{
        try {
            ConfigurationFileValidator.isConfigurationFileValid(configurationFilePath);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            throw new Exception("invalid configuration file");
        }

        Node root = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(new File(configurationFilePath));
        return buildHashMapFromXmlDocumentNode(root);
    }

    private static Map<String, Map<String, Integer>> buildHashMapFromXmlDocumentNode(Node documentNode) {
        List<Node> fieldNodes = extractFieldNodes(documentNode);
        return buildHashMapForFields(fieldNodes);
    }

    private static List<Node> extractFieldNodes(Node node) {
        return extractElementNodesByName(node, "field");
    }

    private static Map<String, Map<String, Integer>> buildHashMapForFields(List<Node> fieldNodes) {
        Map<String, Map<String, Integer>> globalMap   = new HashMap<>();
        List<Pair<String, HashMap<String, Integer>>> pairMaps = buildPairMapForFields(fieldNodes);

        for (Pair<String, HashMap<String, Integer>> pair: pairMaps) {
            globalMap.putIfAbsent(pair.getFirst(), pair.getSecond());
        }
        return globalMap;
    }

    private static List<Pair<String, HashMap<String, Integer>>> buildPairMapForFields(List<Node> fieldNodes) {
        Stream<Pair<String, HashMap<String, Integer>>> pairStream = fieldNodes.stream()
                .map(TranslationMapBuilder::buildPairMapForField)
        ;
        return pairStream.collect(Collectors.toList());
    }

    private static Pair<String, HashMap<String, Integer>> buildPairMapForField(Node fieldNode) {
        Node valueMappingNode                  = extractValueMappingNodes(fieldNode).get(0); //
        HashMap<String, Integer> internHashMap = buildInternHashMapForField(valueMappingNode);
        String fieldName                       = fieldNode.getAttributes().getNamedItem("name").getNodeValue().trim();
        return new Pair<>(fieldName.toLowerCase(), internHashMap);
    }

    private static List<Node> extractValueMappingNodes(Node node) {
        return extractElementNodesByName(node, "valueMapping");
    }

    private static HashMap<String, Integer> buildInternHashMapForField(Node valueMappingNode) {
        List<Node> valueTranslationNodes = extractValueTranslationNodes(valueMappingNode);
        List<Pair<String, Integer>> translationInfos = extractInfoFromValueTranslationNodes(valueTranslationNodes);
        return convertPairListToHashMap(translationInfos);
    }

    private static List<Pair<String, Integer>> extractInfoFromValueTranslationNodes(List<Node> valueTranslationNodes) {
        Stream<Pair<String, Integer>> translationPairs = valueTranslationNodes
                .stream()
                .map(TranslationMapBuilder::extractConvertedInfoFromValueTranslationNode)
                .filter(Objects::nonNull);
        return translationPairs.collect(Collectors.toList());
    }

    private static Pair<String, Integer> extractConvertedInfoFromValueTranslationNode(Node valueTranslationNode) {
        Pair<String, String> stringPair = extractInfoFromValueTranslationNode(valueTranslationNode);
        return translatePair(stringPair);
    }

    private static Pair<String, String> extractInfoFromValueTranslationNode(Node valueTranslationNode) {
        if(valueTranslationNode.getNodeType() == Node.ELEMENT_NODE
                && valueTranslationNode.getNodeName().toLowerCase().equals("valuetranslation")) {
            NamedNodeMap nodeMap = valueTranslationNode.getAttributes();
            String name  = nodeMap.getNamedItem("stringValue").getNodeValue().trim().toLowerCase();
            String value = nodeMap.getNamedItem("numValue")   .getNodeValue().trim().toLowerCase();
            return new Pair<>(name, value);
        }
        return null;
    }

    private static List<Node> extractElementNodesByName(Node node, String searchedNodeName) {
        List<Node> namedNodes = new ArrayList<>();
        Predicate<Node> qualificationPredicate = buildQualificationPredicate(searchedNodeName);
        extractQualifiedElementNodes(node, qualificationPredicate, namedNodes);
        return namedNodes;
    }

    private static Predicate<Node> buildQualificationPredicate(String nodeName) {
        return x -> (x.getNodeType() == Node.ELEMENT_NODE) && x.getNodeName().trim().equals(nodeName);
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

    private static List<Node> extractValueTranslationNodes(Node node) {
        return extractElementNodesByName(node, "valueTranslation");
    }

    private static HashMap<String, Integer> convertPairListToHashMap(List<Pair<String, Integer>> infos) {
        HashMap<String, Integer> result = new HashMap<>();
        for (Pair<String, Integer> info: infos) {
            result.putIfAbsent(info.getFirst(), info.getSecond());
        }
        return result;
    }

    private static Pair<String, Integer> translatePair(Pair<String, String> stringPair) {
        if(stringPair == null) return null;
        try {
            double value = Double.valueOf(stringPair.getSecond());
            return new Pair<>(stringPair.getFirst(), (int)value);
        } catch (NumberFormatException ex) {
            System.err.println(ex.getMessage());
            return null;
        }
    }
}