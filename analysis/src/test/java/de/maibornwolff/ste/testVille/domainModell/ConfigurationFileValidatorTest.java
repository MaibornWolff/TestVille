package de.maibornwolff.ste.testVille.domainModell;

import de.maibornwolff.ste.testVille.configurationFileHandling.ConfigurationFileValidator;
import de.maibornwolff.ste.testVille.inputFileParsing.common.ManagementTool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationFileValidatorTest {

    @DisplayName("Should validate the xrayConfigFile schema configuration file")
    @Test
    void xRayValidationCheck() {
        // arrange
        String filePath = "./src/main/resources/jiraXrayDefaultConfiguration.xml";

        // act
        boolean actual = configFileValidationTemplate(filePath, ManagementTool.JIRA_XRAY);

        // assert
        assertEquals(true, actual, "Validation of the configuration file failed");
    }

    @DisplayName("Should validate the hpAlmConfigFile schema configuration file")
    @Test
    void almValidationCheck() {
        // arrange
        String filePath = "./src/main/resources/hpAlmDefaultConfiguration.xml";

        // act
        boolean actual = configFileValidationTemplate(filePath, ManagementTool.HP_ALM);

        // assert
        assertEquals(true, actual, "Validation of the configuration file failed");
    }


    @TestTemplate
    boolean configFileValidationTemplate(String filePath, ManagementTool managementTool) {
        try {
            ConfigurationFileValidator.validateConfigurationFile(filePath, managementTool);
            return true;
        }catch (Exception e) {
            return false;
        }
    }
}