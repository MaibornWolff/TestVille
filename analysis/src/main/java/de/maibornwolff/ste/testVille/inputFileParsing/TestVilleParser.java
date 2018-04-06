package de.maibornwolff.ste.testVille.inputFileParsing;

import de.maibornwolff.ste.testVille.application.AnalysisRunSetting;
import de.maibornwolff.ste.testVille.domainModell.ComposedItem;
import de.maibornwolff.ste.testVille.domainModell.Item;
import de.maibornwolff.ste.testVille.inputFileParsing.hpALM.HpAlmParser;
import de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY.JiraXrayParser;

import java.util.Collection;
import java.util.stream.Collectors;

public class TestVilleParser {
    private AnalysisRunSetting setting;

    public TestVilleParser(AnalysisRunSetting setting) {
        this.setting = setting;
    }

    private Parser createCorrespondentParser() throws Exception {
        switch (this.setting.getExportOrigin()) {
            case HP_ALM:    return new HpAlmParser ();
            case JIRA_XRAY: return new JiraXrayParser();
            default:        return null;
        }
    }

    public VisualizationTree parse() throws Exception {
        Parser usedParser = this.createCorrespondentParser();
        return this.parseWithGivenParser(usedParser);
    }

    private VisualizationTree parseWithGivenParser(Parser usedParser) throws Exception {
        if(usedParser != null) {
            Collection<Item> parseResult = usedParser.parse(this.setting.getExportFilePath(), this.setting.getConfigurationFilePath());
            parseResult = rmEmptyComposedItems(parseResult);
            return VisualizationTree.buildVisualizationTreeFrom(parseResult);
        }
        return null;
    }

    // the visualization module is yet not able to represent empty composedItem
    private static Collection<Item> rmEmptyComposedItems(Collection<Item> parseResult) {
        return parseResult
                .stream()
                .filter(x -> (x instanceof ComposedItem) && (! ((ComposedItem) x).getAssociatedItems().isEmpty()))
                .collect(Collectors.toList());
    }
}
