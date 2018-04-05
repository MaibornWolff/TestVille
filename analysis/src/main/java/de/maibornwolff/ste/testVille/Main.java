package de.maibornwolff.ste.testVille;

import de.maibornwolff.ste.testVille.application.AnalysisRunSetting;
import de.maibornwolff.ste.testVille.application.OptionHandler;
import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.inputFileParsing.TestVilleParser;
import de.maibornwolff.ste.testVille.inputFileParsing.VisualizationTree;
import de.maibornwolff.ste.testVille.vizualisationFileWriting.VisualizationFileGenerator;

import java.util.Collection;
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
            VisualizationTree parseResult        = parser.parse();
            VisualizationFileGenerator generator = new VisualizationFileGenerator(parseResult, setting.getVisualizationFileTarget());
            generator.generateVisualizationFile();
        }catch (Exception e) {
            printError(e);
        }
    }

    private static void printError(Exception e) {
        System.err.println(e.toString());
        System.err.println("the visualization file could not be generated!");
        System.exit(0);
    }
}
