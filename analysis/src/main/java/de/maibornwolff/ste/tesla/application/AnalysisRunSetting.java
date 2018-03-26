package de.maibornwolff.ste.tesla.optionSwitchAndHandle;

import de.maibornwolff.ste.tesla.managementTools.common.ManagementTool;

public class AnalysisRunSetting {

    private final String configurationFilePath;
    private final String exportFilePath;
    private String visualizationFileTarget;
    private final ManagementTool exportOrigin;

    AnalysisRunSetting(String configurationFilePath, String exportFilePath, String visualizationFileTarget, ManagementTool exportOrigin) {
        this.configurationFilePath = configurationFilePath;
        this.exportFilePath = exportFilePath;
        this.visualizationFileTarget = visualizationFileTarget;
        this.completeVisualizationFileTarget();
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

    public String getVisualizationFileTarget() {
        return visualizationFileTarget;
    }

    private boolean areManagementToolAndExportFileConsistent() {
        return ((this.getExportOrigin() == ManagementTool.HP_ALM) && this.exportFilePath.endsWith(".xls"))
                || ((this.getExportOrigin() == ManagementTool.JIRA_XRAY) && this.exportFilePath.endsWith(".xml"));
    }

    private boolean isConfigurationFilePathValid() {
        return (this.configurationFilePath != null)
                && (!this.configurationFilePath.startsWith("-"))
                && this.configurationFilePath.endsWith(".xml");
    }

    private boolean isExportFilePathValid() {
        return this.exportFilePath != null;
    }

    private boolean isVisualizationFileTargetValid() {
        return this.visualizationFileTarget != null;
    }

    boolean isValid () {
        return this.isConfigurationFilePathValid()
                && this.isExportFilePathValid()
                && this.isVisualizationFileTargetValid()
                && this.areManagementToolAndExportFileConsistent();
    }

    private void completeVisualizationFileTarget () {
        if(!this.visualizationFileTarget.endsWith(".json")) {
            this.visualizationFileTarget = this.visualizationFileTarget.concat(".json");
        }
    }
}