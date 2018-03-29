package de.maibornwolff.ste.tesla.managementTools.jiraXRAY.component;

import de.maibornwolff.ste.tesla.managementTools.jiraXRAY.helpers.ConfigurationFileValidator;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;

import static org.junit.jupiter.api.Assertions.*;

class ConfigurationFileValidatorTest {

    @DisplayName("Should recognize a valid configuration file")
    @Test
    void validationCheck() throws Exception {
        // arrange
        String filePath = "./src/test/resources/defaultJiraXrayConfigFile.xml";

        // act
        boolean actual = configFileValidationTemplate(filePath);

        // assert
        assertEquals(true, actual, "Validation of the configuration file failed");
    }


    @TestTemplate
    boolean configFileValidationTemplate(String filePath) throws Exception {
        return ConfigurationFileValidator.isConfigurationFileValid(filePath);
    }
}