package de.maibornwolff.ste.testVille.application;

import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;

/**
 *
 */
public class ConfigurationFileValidator {

    public static boolean isConfigurationFileValid(String filePath) throws Exception {
        if(!isConfigurationFilePathValid(filePath)) {
            throw new Exception("Configuration file does not found or is not readable!");
        }

        Node documentNode = buildXmlNodeFromConfigurationFile(new File(filePath));
        return isDocumentNodeValid(documentNode);
    }

    private static boolean isConfigurationFilePathValid(String filePath) {
        File file = new File(filePath);
        return file.exists() && file.isFile() && file.canRead();
    }

    private static Node buildXmlNodeFromConfigurationFile(File configFile) throws Exception {
        return DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(configFile);
    }

    private static boolean isDocumentNodeValid(Node node) throws Exception{

        return areDocumentNodeChildrenValid(node);
    }

    private static boolean areDocumentNodeChildrenValid(Node node) throws Exception {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(!isDocumentNodeChildValid(child)) {
                return false; //throw new Exception("The default configuration file is not valid!");
            }
        }
        return true;
    }

    private static boolean isDocumentNodeChildValid(Node node) throws Exception {
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            return isXmlElementNodeWithNameTranslationConfigurationValid(node);
        }
        return isValidTextNodeOrCommentNode(node);
    }

    private static boolean isXmlElementNodeWithNameTranslationConfigurationValid(Node node) throws Exception{
       if(!areTranslationConfigurationAttributesValid(node)){
           throw new Exception("TranslationConfiguration ");
       }

       return areTranslationConfigurationChildrenValid(node);
    }

    private static boolean areTranslationConfigurationAttributesValid(Node node) {
        return (node.getAttributes().getLength() == 0);
    }

    private static boolean areTranslationConfigurationChildrenValid(Node node) throws Exception {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(!isTranslationConfigurationChildValid(child)){
                return false;//throw new Exception("TranslationConfiguration contains invalid nodes");
            }
        }
        return true;
    }

    private static boolean isTranslationConfigurationChildValid(Node node) throws Exception {
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            return isXmlElementNodeWithNameFieldValid(node);
        }
        return isValidTextNodeOrCommentNode(node);
    }

    private static boolean isXmlElementNodeWithNameFieldValid(Node node) throws Exception {
        if(!isFieldAttributeValid(node)) {
            throw new Exception("Field Attributes map is not valid!");
        }
        return areFieldChildrenValid(node);
    }

    private static boolean isFieldAttributeValid(Node node) {
        NamedNodeMap attributes = node.getAttributes();

        boolean result = (attributes.getLength() == 1);
        result &= attributes.item(0).getNodeName().trim().toLowerCase().equals("name");

        return result;
    }

    private static boolean areFieldChildrenValid(Node node) throws Exception {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(!isFieldChildValid(child)){
                return false; //throw new Exception("Field contains invalid nodes");
            }
        }
        return true;
    }

    private static boolean isFieldChildValid(Node node) throws Exception {
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            return isXmlElementNodeWithNameValueMappingValid(node);
        }
        return isValidTextNodeOrCommentNode(node);
    }

    private static boolean isXmlElementNodeWithNameValueMappingValid(Node node) throws Exception{
        if(!isValueMappingAttributeValid(node)) {
            throw new Exception("Field Attributes map is not valid!");
        }
        return areValueMappingChildrenValid(node);
    }

    private static boolean isValueMappingAttributeValid(Node node) {
        return node.getAttributes().getLength() == 0;
    }

    private static boolean areValueMappingChildrenValid(Node node) throws Exception {
        NodeList children = node.getChildNodes();
        for (int i = 0; i < children.getLength(); i++) {
            Node child = children.item(i);
            if(!isValueMappingChildValid(child)){
                return false; //throw new Exception("valueMapping contains invalid nodes");
            }
        }
        return true;
    }

    private static boolean isValueMappingChildValid(Node node) throws Exception {
        if(node.getNodeType() == Node.ELEMENT_NODE) {
            return isXmlElementNodeWithNameValueTranslationValid(node);
        }
        return isValidTextNodeOrCommentNode(node);
    }

    private static boolean isXmlElementNodeWithNameValueTranslationValid(Node node) throws Exception {
        if(!areValueTranslationAttributesValid(node)) {
            throw new Exception("A valueTranslation node contains invalid attributes!");
        }
        return node.getChildNodes().getLength() == 0;
    }

    /**
     * This function check the attributes of an given node that name is TranslationValue.
     * @param node Node to be checked.
     * @return True if the attributes of node are conform else false.
     */
    private static boolean areValueTranslationAttributesValid(Node node) {
        NamedNodeMap attributes = node.getAttributes();

        boolean result = (attributes.getLength() == 2);

        result &= attributes.item(1).getNodeName().trim().equals("stringValue");
        result &= attributes.item(0).getNodeName().trim().equals("numValue");

        return result;
    }

    /**
     * All Text nodes in the configuration file should be empty or white spaces.
     * This method verify this condition.
     * @param node Node to be checked.
     * @return True if the condition is respected else false.
     */
    private static boolean isXmlTextNodeValid(Node node) {
        return (node.getNodeType() == Node.TEXT_NODE) && (node.getNodeValue().trim().length() == 0);
    }

    /**
     * This check if the given node is a comment node.
     * @param node Node to be checked.
     * @return True if node is from type Comment node else false.
     */
    private static boolean isXmlCommentNode(Node node) {
        return node.getNodeType() == Node.COMMENT_NODE;
    }

    /**
     * This method combine both methods {@link #isXmlTextNodeValid(Node) isXmlTextNodeValid} and
     * {@link #isXmlCommentNode(Node) isXmlCommentNode} as alternative.
     * @param node Node to be checked.
     * @return True if node is from type Comment node or a valid Text node else false.
     */
    private static boolean isValidTextNodeOrCommentNode(Node node) {
        return isXmlTextNodeValid(node) || isXmlCommentNode(node);
    }
}