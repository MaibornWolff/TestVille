import de.maibornwolff.ste.testVille.vizualisationFileWriter.VisualizationFileGenerator;

import java.util.List;

public class Main {

    public static void main(String[] args) {
        List<String> commandLineArgs                          = List.of(args);
        VisualizationFileGenerator visualizationFileGenerator = null;
        try {
            visualizationFileGenerator = new VisualizationFileGenerator(commandLineArgs);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.exit(0);
        }
        tryToGenerateTheVisualizationFile(visualizationFileGenerator);
    }

    private static void tryToGenerateTheVisualizationFile(VisualizationFileGenerator writer) {
        try {
            writer.generateVisualizationFile();
        }catch (Exception e) {
            System.err.println(e.toString());
            System.err.println("the visualization file could not be generated!");
            System.exit(0);
        }
    }
}
