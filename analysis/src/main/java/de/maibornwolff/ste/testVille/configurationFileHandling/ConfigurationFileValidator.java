package de.maibornwolff.ste.testVille.configurationFileHandling;

import de.maibornwolff.ste.testVille.inputFileParsing.common.ManagementTool;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

public class ConfigurationFileValidator {

    public static void validateConfigurationFile(String filePath, ManagementTool managementTool) throws Exception {
        Schema usedSchema = createValidationSchema(managementTool);
        Source fileSource = new StreamSource(filePath);
        tryToValidateConfigurationFile(usedSchema, fileSource);
    }

    private static void tryToValidateConfigurationFile(Schema usedSchema, Source toValidate) throws Exception {
        if(usedSchema == null) throw new Exception("Configuration file validation failed: Validator creation failed");

        try {
            usedSchema.newValidator().validate(toValidate);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            throw new Exception("Configuration file validation failed: invalid Configuration file");
        }
    }

    private static Schema createValidationSchema(ManagementTool managementTool) throws MalformedURLException, SAXException {
        switch (managementTool) {
            case HP_ALM:    return createHpAlmValidationSchema();
            case JIRA_XRAY: return createJiraXrayValidationSchema();
            default:        return null;
        }
    }

    private static Schema createHpAlmValidationSchema() throws MalformedURLException, SAXException {
        URL usedSchemaUrl = new File("./src/main/resources/HpAlmConfigurationFileSchema.xsd").toURI().toURL();
        return createValidationSchemaFromURL(usedSchemaUrl);
    }

    private static Schema createJiraXrayValidationSchema() throws MalformedURLException, SAXException {
        URL usedSchemaUrl = new File("./src/main/resources/JiraXrayConfigurationFileSchema.xsd").toURI().toURL();
        return createValidationSchemaFromURL(usedSchemaUrl);
    }

    private static Schema createValidationSchemaFromURL(URL schemaUrl) throws SAXException {
        return SchemaFactory
                .newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI)
                .newSchema(schemaUrl);
    }
}