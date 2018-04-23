package de.maibornwolff.ste.testVille.inputFileParsing;

import de.maibornwolff.ste.testVille.application.AnalysisRunSetting;
import de.maibornwolff.ste.testVille.domainModell.ComposedItem;
import de.maibornwolff.ste.testVille.inputFileParsing.hpALM.HpAlmParser;
import de.maibornwolff.ste.testVille.inputFileParsing.jiraXRAY.JiraXrayParser;

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

    public ComposedItem parse() throws Exception {
        Parser usedParser = this.createCorrespondentParser();
        ComposedItem parsedTree = null;

        if(usedParser != null) {
            parsedTree = usedParser.parse(this.setting.getExportFilePath(), this.setting.getConfigurationFilePath());
        }
        return parsedTree;
    }

}
