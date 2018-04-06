package de.maibornwolff.ste.testVille.vizualisationFileWriting;

import de.maibornwolff.ste.testVille.application.AnalysisRunSetting;
import de.maibornwolff.ste.testVille.inputFileParsing.TestVilleParser;
import de.maibornwolff.ste.testVille.inputFileParsing.VisualizationTree;
import de.maibornwolff.ste.testVille.inputFileParsing.common.ManagementTool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import static org.junit.jupiter.api.Assertions.*;

class VisualizationFileGeneratorTest {


    @Test
    @DisplayName("it should generate a valid visualization file from hpAlm data")
    void writeTest () throws Exception{
        // Arrange
        String testFilePath   = "./src/test/resources/TestS.xls";
        String configFilePath = "./src/test/resources/hpAlmDefaultConfiguration.xml";
        String target         = "./src/test/resources/TestSIstErg.json";
        String dueTarget      = "./src/test/resources/TestSSollErg.json";

        AnalysisRunSetting ars         = new AnalysisRunSetting(configFilePath, testFilePath, target, ManagementTool.HP_ALM);
        TestVilleParser    tvp         = new TestVilleParser(ars);
        VisualizationTree  parseResult = tvp.parse();
        VisualizationFileGenerator gen = new VisualizationFileGenerator(parseResult, ars.getVisualizationFileTarget());

        // Act
        gen.generateVisualizationFile();
        String expectedContent  = readUndReturnContentOf(dueTarget);
        String availableContent = readUndReturnContentOf(target);

        // Assert
        assertEquals(expectedContent, availableContent);
    }

    private String deleteAllWhiteSpaces(String str) {
        return str.replaceAll("\\s+", "").toLowerCase();
    }

    private String readUndReturnContentOf(String filePath) throws Exception {
        try {
            return deleteAllWhiteSpaces(extractFileContent(filePath));
        }catch (Exception e) {
            System.err.println(e.getMessage());
            throw new Exception("Test could not be executed!");
        }
    }

    private String extractFileContent(String fileName) throws IOException {
        BufferedReader buffR = new BufferedReader(new FileReader(fileName));
        StringBuilder strBuilder = new StringBuilder();
        String str;
        boolean b = true;

        while(b) {
            str = buffR.readLine();
            if(str != null) {
                strBuilder.append(str);
            }else {
                b = false;
            }
        }
        return strBuilder.toString();
    }

}