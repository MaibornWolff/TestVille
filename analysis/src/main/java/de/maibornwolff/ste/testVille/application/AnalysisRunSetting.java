package de.maibornwolff.ste.testVille.application;

import de.maibornwolff.ste.testVille.inputFileParsing.common.*;

import java.util.Objects;


public class AnalysisRunSetting {

    private final String configurationFilePath;
    private final String exportFilePath;
    private String visualizationFilePath;
    private final ManagementTool exportOrigin;

    public AnalysisRunSetting(String configurationFilePath, String exportFilePath, String visualizationFilePath, ManagementTool exportOrigin) {
        this.configurationFilePath = configurationFilePath;
        this.exportFilePath = exportFilePath;
        this.visualizationFilePath = visualizationFilePath;
        this.completeVisualizationFilePathIfNecessary();
        this.exportOrigin = exportOrigin;
    }

    public ManagementTool getExportOrigin() {
        return exportOrigin;
    }

    public String getExportFilePath() {
        return exportFilePath;
    }

    public String getConfigurationFilePath() {
        return configurationFilePath;
    }

    public String getVisualizationFilePath() {
        return visualizationFilePath;
    }

    private boolean areManagementToolAndExportFileConsistent() {
        return this.hpAlmConsistencyCheck() || this.jiraXrayConsistencyCheck();
    }

    private boolean hpAlmConsistencyCheck() {
        return (this.getExportOrigin() == ManagementTool.HP_ALM) && this.exportFilePath.endsWith(".xls");
    }

    private boolean jiraXrayConsistencyCheck() {
        return (this.getExportOrigin() == ManagementTool.JIRA_XRAY) && this.exportFilePath.endsWith(".xml");
    }

    private boolean isConfigurationFilePathValid() {
        return (!Objects.isNull(this.configurationFilePath))
                && (!this.configurationFilePath.startsWith("-"))
                && this.configurationFilePath.endsWith(".xml");
    }

    private boolean isExportFilePathValid() {
        return this.exportFilePath != null;
    }

    private boolean isVisualizationFilePathValid() {
        return this.visualizationFilePath != null;
    }

    boolean isValid () {
        return this.isConfigurationFilePathValid()
                && this.isExportFilePathValid()
                && this.isVisualizationFilePathValid()
                && this.areManagementToolAndExportFileConsistent();
    }

    private void completeVisualizationFilePathIfNecessary () {
        if(doesVisualizationFilePathNeedsCompletion()) {
            this.visualizationFilePath = this.visualizationFilePath.concat(".json");
        }
    }

    private boolean doesVisualizationFilePathNeedsCompletion() {
        return (!Objects.isNull(this.visualizationFilePath)) && !(this.visualizationFilePath.endsWith(".json"));
    }

    @Override
    public boolean equals(Object object) {
        if(object instanceof AnalysisRunSetting) {
            AnalysisRunSetting setting = (AnalysisRunSetting) object;
            return this.configurationFilePath.equals(setting.configurationFilePath)
                    && this.visualizationFilePath.equals(setting.visualizationFilePath)
                    && this.exportFilePath.equals(setting.exportFilePath)
                    && this.exportOrigin.equals(setting.exportOrigin);
        }
        return false;
    }
}