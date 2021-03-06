package de.maibornwolff.ste.testVille;

import de.maibornwolff.ste.testVille.application.AnalysisRunSetting;
import de.maibornwolff.ste.testVille.application.OptionHandler;
import de.maibornwolff.ste.testVille.domainModell.ComposedItem;
import de.maibornwolff.ste.testVille.inputFileParsing.TestVilleParser;
import de.maibornwolff.ste.testVille.vizualisationFileWriting.VisualizationFileGenerator;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        tryToGenerateTheVisualizationFile(args);
    }

    private static void tryToGenerateTheVisualizationFile(String... args) {
        try {
            AnalysisRunSetting         setting   = new OptionHandler(List.of(args)).getRunSetting();
            if(setting == null) return;
            TestVilleParser            parser    = new TestVilleParser(setting);
            ComposedItem parseResult = parser.parse();
            VisualizationFileGenerator generator = new VisualizationFileGenerator(parseResult, setting.getVisualizationFilePath());
            generator.generateVisualizationFile();
        }catch (Exception e) {
            printError(e);
        }
    }

    private static void printError(Exception e) {
        System.err.println(e.getMessage());
        System.err.println("the visualization file could not be generated!");
        System.exit(0);
    }
}
