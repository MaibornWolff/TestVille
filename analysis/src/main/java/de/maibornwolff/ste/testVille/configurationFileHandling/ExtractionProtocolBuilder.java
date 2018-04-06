package de.maibornwolff.ste.testVille.configurationFileHandling;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;

public class ExtractionProtocolBuilder {

    private Map<String, Integer> extractionProtocol;

    public ExtractionProtocolBuilder(String configFilePath) throws ParserConfigurationException, SAXException, IOException {
        this.setExtractionProtocol(configFilePath);
    }

    private void setExtractionProtocol(String configFilePath) throws IOException, SAXException, ParserConfigurationException {
        Node node = TranslationMapBuilder.buildDocumentNodeFrom(configFilePath);
        List<Node> extractableNodes = extractAllFromExcelExportExtractableFields(node);
        this.extractionProtocol = buildExtractionProtocol(extractableNodes);
    }

    public Map<String, Integer> getExtractionProtocol() {
        return extractionProtocol;
    }

    private static Predicate<Node> buildPredicateForExtractableFields() {
        return x -> (x.getNodeType() == Node.ELEMENT_NODE)
                && (x.getAttributes().getNamedItem("column") != null);
    }

    private List<Node> extractAllFromExcelExportExtractableFields(Node node) {
        return TranslationMapBuilder.extractQualifiedElementNodes(node, buildPredicateForExtractableFields());
    }

    private Map<String, Integer> buildExtractionProtocol(List<Node> extractableFieldsNodes) {
        return extractableFieldsNodes
                .stream()
                .reduce(new HashMap<> (),
                        ExtractionProtocolBuilder::extractNeededInfosFromNodeAndCompleteCurrentMap,
                        ExtractionProtocolBuilder::mergeMaps
                );
    }

    private static HashMap<String, Integer> extractNeededInfosFromNodeAndCompleteCurrentMap(HashMap<String, Integer> toComplete, Node node) {
        String fieldName = extractFieldName(node);
        int fieldCanBeExtractedAt = extractFieldColumn(node);
        toComplete.putIfAbsent(fieldName, fieldCanBeExtractedAt);
        return toComplete;
    }

    private static String extractFieldName(Node node) {
        NamedNodeMap attributes = node.getAttributes();
        if(attributes.getNamedItem("name") != null) return attributes.getNamedItem("name").getNodeValue().trim();
        return node.getNodeName().trim();
    }

    private static int extractFieldColumn(Node node) {
        return Integer.valueOf(node.getAttributes().getNamedItem("column").getNodeValue());
    }

    private static <A, B> HashMap<A, B> mergeMaps(HashMap<A, B> firstMap, HashMap<A, B> secondMap) {
        secondMap.forEach(firstMap::putIfAbsent);
        return firstMap;
    }
}
