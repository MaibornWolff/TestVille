package de.maibornwolff.ste.testVille.application;

import de.maibornwolff.ste.testVille.inputFileParsing.common.ManagementTool;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class OptionHandlerTest {

    @Test
    @DisplayName("should create an conform AnalysisRunSetting object")
    void runSettingCreationTest1() throws Exception {
        // arrange
        List<String> argStrings = List.of("-alm", "-i", "in.xls", "-o", "out");
        OptionHandler handler = new OptionHandler(argStrings);
        AnalysisRunSetting expectedSetting = new AnalysisRunSetting("./src/main/resources/hpAlmDefaultConfiguration.xml",
                "in.xls", "out.json", ManagementTool.HP_ALM);

        // act
        AnalysisRunSetting actualSetting = handler.getRunSetting();

        // assert
        assertEquals(expectedSetting, actualSetting,"But the created AnalysisRunSetting is not conform!");
    }

    @Test
    @DisplayName("Should throw an exception due to missing visualisation file path")
    void mustThrowExceptionDueToMissingVisFilePath() throws Exception {
        // arrange
        List<String> argStrings = List.of("-alm", "-i", "in.xls", "-o");
        OptionHandler handler = new OptionHandler(argStrings);
        String expectedMsg = "Missing visualization file (target file) path!\nUse [-h/-help] for help";

        // act
        String actualMsg = null;
        try {
            handler.getRunSetting();
        }catch (Exception e) {
            actualMsg = e.getMessage();
        }

        // assert
        assertEquals(expectedMsg, actualMsg,"Unexpected warning msg!");
    }

    @Test
    @DisplayName("Should throw an exception due to missing option")
    void mustThrowExceptionDueToMissingOption() throws Exception {
        // arrange
        List<String> argStrings = List.of("-alm", "-i", "in.xls");
        OptionHandler handler = new OptionHandler(argStrings);
        String expectedMsg = "Missing visualization file (target file) path!\nUse [-h/-help] for help";

        // act
        String actualMsg = null;
        try {
            handler.getRunSetting();
        }catch (Exception e) {
            actualMsg = e.getMessage();
        }

        // assert
        assertEquals(expectedMsg, actualMsg,"Unexpected warning msg!");
    }

    @Test
    @DisplayName("Should throw an exception due to more than one input(export) files")
    void mustThrowExceptionDueToManyExportFiles() throws Exception {
        // arrange
        List<String> argStrings = List.of("-alm", "-i", "in.xls", "in2.xls", "-o", "out.json");
        OptionHandler handler = new OptionHandler(argStrings);
        String expectedMsg = "More than one input files are not allowed!\nUse [-h/-help] for help";

        // act
        String actualMsg = null;
        try {
            handler.getRunSetting();
        }catch (Exception e) {
            actualMsg = e.getMessage();
        }

        // assert
        assertEquals(expectedMsg, actualMsg,"Unexpected warning msg!");
    }

    @Test
    @DisplayName("Should throw an exception due to redundant options")
    void mustThrowExceptionDueToRedundantOptions() throws Exception {
        // arrange
        List<String> argStrings = List.of("-alm", "-i", "in.xls", "-i", "in2.xls", "-o", "out.json");
        OptionHandler handler = new OptionHandler(argStrings);
        String expectedMsg = "You may use the [-i/-input]-option only one time!\nUse [-h/-help] for help";

        // act
        String actualMsg = null;
        try {
            handler.getRunSetting();
        }catch (Exception e) {
            actualMsg = e.getMessage();
        }

        // assert
        assertEquals(expectedMsg, actualMsg,"Unexpected warning msg!");
    }

}