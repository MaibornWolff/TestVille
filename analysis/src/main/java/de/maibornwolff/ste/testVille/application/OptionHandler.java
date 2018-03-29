package de.maibornwolff.ste.testVille.application;

import de.maibornwolff.ste.testVille.inputFileParsing.common.ManagementTool;

import java.util.List;
import java.util.stream.Collectors;

public class OptionHandler {
    private final List<String> commandLineArgs;

    public OptionHandler(List<String> commandLineArgs){
        this.commandLineArgs = commandLineArgs;
    }

    private static String helpMsg() {
        return "\nTestVille\n\t@version: 2.0.0\n\t@module:  analysis\n"+
        "\t@task:    extract necessary data from a export file and processes it to produce a visualization file.\n"+
        "\t@options:\n" +
                "\t\t-h/-help                                    print help msg.\n" +
                "\t\t-alm/-xray                                  precide the origin of the export file.\n" +
                "\t\t-i/-input  <export file path>               precise the export file.\n" +
                "\t\t-c/-config <custom configuration file path> precise the config file to use. When empty a default config is used.\n" +
                "\t\t-o/-output <visualization file target>      precise the target for visualization file.";
    }

    private boolean userNeedHelp() {
        return (this.commandLineArgs.size() == 1) && isHelpOption(this.getArg(0));
    }

    private boolean isHelpOption(String option) {
        option = optionCorrection(option);
        return Options.H.toString().toLowerCase().equals(option)
                || Options.HELP.toString().toLowerCase().equals(option);
    }

    private boolean containsHelpOption() {
        List<String> correctedArgs = this.getCorrectedCommandLineArgs();
        return correctedArgs.contains(Options.H.toString().toLowerCase())
                || correctedArgs.contains(Options.HELP.toString().toLowerCase());
    }

    private boolean containsInconsistentHelpRequest() {
        return (!this.userNeedHelp()) && this.containsHelpOption();
    }

    public AnalysisRunSetting getRunSetting() throws Exception {
        if(this.containsInconsistentHelpRequest()) {
            throw new Exception("Inconsistent arguments! Use -h/-help option for help.");
        } else if(this.userNeedHelp()) {
            System.out.println(helpMsg());
            return null;
        }
        return createRunSetting();
    }

    private static String optionCorrection(String option) {
        if((option == null) || option.isEmpty()) return null;
        option = option.trim();
        return option.startsWith("-") ? option.trim().substring(1) : option;
    }

    private List<String> getCorrectedCommandLineArgs() {
        return this.commandLineArgs.stream().map(OptionHandler::optionCorrection).collect(Collectors.toList());
    }

    private String getArg(int argIndex) {
        return this.commandLineArgs.size() > argIndex ? this.commandLineArgs.get(argIndex) : null;
    }

    private AnalysisRunSetting createRunSetting() throws Exception {
        ManagementTool toolInfo = this.extractManagementToolInfo();
        String configFilePath   = this.extractConfigurationFilePath(toolInfo);
        String exportFilePath   = this.extractInputFilePath();
        String outputFilePath   = this.extractOutputFilePath();
        AnalysisRunSetting result = new AnalysisRunSetting(configFilePath, exportFilePath, outputFilePath, toolInfo);
        if(result.isValid()) return result;
        throw new Exception("Inconsistent arguments! Use -h/-help option for help.");
    }

    private ManagementTool extractManagementToolInfo() {
        List<String> allArgs = this.getCorrectedCommandLineArgs();
        int           index = allArgs.indexOf(Options.ALM.toString().toLowerCase());
        if(index < 0) index = allArgs.indexOf(Options.XRAY.toString().toLowerCase());
        return index < 0 ? null : stringToManagementTool(allArgs.get(index));
    }

    private ManagementTool stringToManagementTool(String option) {
        switch (option) {
            case "alm":  return ManagementTool.HP_ALM;
            case "xray": return ManagementTool.JIRA_XRAY;
            default:     return null;
        }
    }

    private String extractConfigurationFilePath(ManagementTool tool) {
        List<String> allArgs = this.getCorrectedCommandLineArgs();
        int           index = allArgs.indexOf(Options.CONFIG.toString().toLowerCase());
        if(index < 0) index = allArgs.indexOf(Options.C.toString().toLowerCase());
        return index < 0 ? getDefaultConfigFilePath(tool) : this.getArg(++index);
    }

    private String getDefaultConfigFilePath(ManagementTool tool) {
        if(null == tool) return null;
        String almDefaultConfig  = "./analysis/src/main/resources/hpAlmDefaultConfiguration.xml";
        String xrayDefaultConfig = "./analysis/src/main/resources/jiraXrayDefaultConfiguration.xml";
        switch (tool){
            case JIRA_XRAY: return xrayDefaultConfig;
            case HP_ALM:    return almDefaultConfig;
        }
        return null;
    }

    private String extractInputFilePath() {
        List<String> allArgs = this.getCorrectedCommandLineArgs();
        int           index = allArgs.indexOf(Options.INPUT.toString().toLowerCase());
        if(index < 0) index = allArgs.indexOf(Options.I.toString().toLowerCase());
        return index < 0 ? null : this.getArg(++index);
    }

    private String extractOutputFilePath() {
        List<String> allArgs = this.getCorrectedCommandLineArgs();
        int           index = allArgs.indexOf(Options.OUTPUT.toString().toLowerCase());
        if(index < 0) index = allArgs.indexOf(Options.O.toString().toLowerCase());
        return index < 0 ? null : this.getArg(++index);
    }
}