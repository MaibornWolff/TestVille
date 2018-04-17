package de.maibornwolff.ste.testVille.application;

import de.maibornwolff.ste.testVille.common.CommandLineArg;
import de.maibornwolff.ste.testVille.common.Option;
import de.maibornwolff.ste.testVille.inputFileParsing.common.ManagementTool;

import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

public class OptionHandler {

    private final List<CommandLineArg>  commandLineArgs;
    private final List<Option>  fromUserChosenOptions;
    private final List<Integer> indexesOfChosenOptions;

    public OptionHandler(List<String> commandLineArgsAsStrings) throws Exception {
        this.commandLineArgs = transformToCommandLineArgs(commandLineArgsAsStrings);
        this.fromUserChosenOptions = this.extractAllAvailableOptions();
        this.indexesOfChosenOptions = extractIndexesOfAvailableOptions();
    }

    private static List<CommandLineArg> transformToCommandLineArgs(List<String> commandLineArgsAsStrings) {
        boolean noArgsAvailable = commandLineArgsAsStrings.stream().allMatch(String::isEmpty);
        if(noArgsAvailable) return List.of(new CommandLineArg("-help"));

        return commandLineArgsAsStrings
                .stream()
                .map(CommandLineArg:: new)
                .collect(Collectors.toList());
    }

    private List<Option> extractAllAvailableOptions() throws Exception {
        List<Option> availableOptions = this.commandLineArgs
                .stream()
                .filter(CommandLineArg:: startsLikeOption)
                .map(CommandLineArg::toOption)
                .collect(Collectors.toList());
        if(!areFromUserChosenOptionsValid(availableOptions)) throwExceptionWithWarningMsg("Some chosen options are invalid!");
        return availableOptions;
    }

    private static boolean areFromUserChosenOptionsValid(List<Option> options) {
        return options
                .stream()
                .allMatch(Option::isValidOption);
    }

    private List<Integer> extractIndexesOfAvailableOptions() {
        List<Integer> optionIndexes = new ArrayList<>();
        int index = 0;
        for (CommandLineArg arg: this.commandLineArgs) {
            if(arg.startsLikeOption()){
                optionIndexes.add(index);
            }
            index ++;
        }
        return optionIndexes;
    }

    private static String produceHelpMessage() {
        return "\nTestVille\n\t@version: 1.0.0\n\t@module:  analysis\n"+
        "\t@task:    extract necessary data from a export file and processes it to produce a visualization file.\n"+
        "\t@options:\n" +
                "\t\t-h/-help                                    print help msg.\n" +
                "\t\t-alm/-xray                                  precise the origin of the export file.\n" +
                "\t\t-i/-input  <export file path>               precise the export file.\n" +
                "\t\t-c/-config <custom configuration file path> precise the config file to use. When empty a default config is used.\n" +
                "\t\t-o/-output <visualization file target>      precise the target for visualization file.";
    }

    private boolean doesUserNeedHelp() {
        return (this.fromUserChosenOptions.size() == 1) && this.fromUserChosenOptions.get(0).isHelpOption();
    }

    private boolean doesUserChoseHelpOption() {
        return this.fromUserChosenOptions.stream().anyMatch(Option::isHelpOption);
    }

    private boolean containsInconsistentHelpRequest() {
        return (!this.doesUserNeedHelp()) && this.doesUserChoseHelpOption();
    }

    public AnalysisRunSetting getRunSetting() throws Exception {
        if(this.containsInconsistentHelpRequest()) {
            throwExceptionWithWarningMsg("Inconsistent arguments!");
        } else if(this.doesUserNeedHelp()) {
            System.out.println(produceHelpMessage());
            return null;
        }
        return createRunSetting();
    }

    private AnalysisRunSetting createRunSetting() throws Exception {
        ManagementTool toolInfo = this.extractOriginOfUserExport();
        String configFilePath   = this.extractConfigurationFilePath(toolInfo);
        String exportFilePath   = this.extractInputFilePath();
        String outputFilePath   = this.extractOutputFilePath();
        AnalysisRunSetting result = new AnalysisRunSetting(configFilePath, exportFilePath, outputFilePath, toolInfo);
        if(!result.isValid()) throwExceptionWithWarningMsg("Inconsistent arguments!");
        return result;
    }

    private ManagementTool extractOriginOfUserExport() throws Exception {
        List<Option> shouldContainsOneElement = this.fromUserChosenOptions
                .stream()
                .filter(Option::isExportOriginOption)
                .collect(Collectors.toList());

        if(shouldContainsOneElement.isEmpty()){
            throwExceptionWithWarningMsg("You must precise the origin of test data!");
        } else if (shouldContainsOneElement.size() > 1){
            throwExceptionWithWarningMsg("2 origins of test data is not allowed!");
        }

        return mapOptionToManagementTool(shouldContainsOneElement.get(0));
    }

    private ManagementTool mapOptionToManagementTool(Option option) {
        switch (option.content) {
            case ALM:  return ManagementTool.HP_ALM;
            case XRAY: return ManagementTool.JIRA_XRAY;
            default:   return null;
        }
    }

    private String getDefaultConfigurationFilePath(ManagementTool tool) {
        if(null == tool) return null;
        String almDefaultConfig  = "./src/main/resources/hpAlmDefaultConfiguration.xml";
        String xrayDefaultConfig = "./src/main/resources/jiraXrayDefaultConfiguration.xml";
        switch (tool){
            case JIRA_XRAY: return xrayDefaultConfig;
            case HP_ALM:    return almDefaultConfig;
        }
        return null;
    }

    private void throwExceptionWithWarningMsg(String warningMsg) throws Exception {
        throw new Exception(warningMsg + "\nUse [-h/-help] for help");
    }


    private int getIndexOfOption(Option option) {
        int localIndexOfOption = this.fromUserChosenOptions.indexOf(option);
        return (localIndexOfOption < 0) ? -1 : this.indexesOfChosenOptions.get(localIndexOfOption);
    }

    private String extractConfigurationFilePath(ManagementTool tool) throws Exception {
        Option configOption = this.extractConfigFileOption();
        if(configOption.isDefaultConfiguration()) {
            return this.getDefaultConfigurationFilePath(tool);
        }

        List<String> configFiles = this.getInputsOfOption(configOption);
        if(configFiles.isEmpty()) {
            throwExceptionWithWarningMsg("Missing configuration file path!");
        }else if(configFiles.size() > 1) {
            throwExceptionWithWarningMsg("More than one configuration files are not allowed!");
        }
       return configFiles.get(0);
    }

    private Option extractConfigFileOption() throws Exception {
        List<Option> availableConfigOptions = this.extractOptionsThatMatch(Option::isConfigFileOption);

        if(availableConfigOptions.size() == 1){
            return availableConfigOptions.get(0);
        }else if(availableConfigOptions.size() > 1) {
            throwExceptionWithWarningMsg("You may use the [-c/-config]-option only one time!");
        }
        return new Option(CommandLineOption.DEFAULTCONFIG);
    }

    private String extractInputFilePath() throws Exception {
        Option inputOption = this.extractInputFileOption();

        List<String> inputFiles = this.getInputsOfOption(inputOption);
        if(inputFiles.isEmpty()) {
            throwExceptionWithWarningMsg("Missing input(export) file path!");
        }else if(inputFiles.size() > 1) {
            throwExceptionWithWarningMsg("More than one input files are not allowed!");
        }
        return inputFiles.get(0);
    }

    private Option extractInputFileOption() throws Exception {
        List<Option> availableInputOptions = this.extractOptionsThatMatch(Option::isInputFileOption);

        if(availableInputOptions.size() == 0){
            throwExceptionWithWarningMsg("Missing input(export) file path!");
        }else if (availableInputOptions.size() > 1) {
            throwExceptionWithWarningMsg("You may use the [-i/-input]-option only one time!");
        }
        return availableInputOptions.get(0);
    }

    private String extractOutputFilePath() throws Exception {
        Option outputFileOption = this.extractOutputFileOption();

        List<String> outputFilePaths = this.getInputsOfOption(outputFileOption);
        if(outputFilePaths.isEmpty()) {
            throwExceptionWithWarningMsg("Missing visualization file (target file) path!");
        }else if(outputFilePaths.size() > 1) {
            throwExceptionWithWarningMsg("More than one output files (target files) are not allowed!");
        }
        return outputFilePaths.get(0);
    }

    private Option extractOutputFileOption() throws Exception {
        List<Option> availableOutputOptions = this.extractOptionsThatMatch(Option::isOutputFileOption);

        if(availableOutputOptions.size() == 0){
            throwExceptionWithWarningMsg("Missing visualization file (target file) path!");
        }else if (availableOutputOptions.size() > 1) {
            throwExceptionWithWarningMsg("You may use the [-o/-output]-option only one time!");
        }
        return availableOutputOptions.get(0);
    }

    private List<Option> extractOptionsThatMatch(Predicate<Option> toMatch) {
        return this.fromUserChosenOptions
                .stream()
                .filter(toMatch)
                .collect(Collectors.toList());
    }

    private List<String> getInputsOfOption(Option option) {
        List<String> noInputsAvailable = new ArrayList<>();
        if(option.isExportOriginOption()){
            return noInputsAvailable;
        }

        int optionIndex = this.getIndexOfOption(option);
        if(optionIndex < 0) {
            return noInputsAvailable;
        }

        int indexOfCorrespondentInput = optionIndex + 1;
        if(indexOfCorrespondentInput >= this.commandLineArgs.size()) {
            return noInputsAvailable;
        }

        return this.getOptionInputsBetweenNextOptionAndIndex(indexOfCorrespondentInput);
    }

    private List<String> getOptionInputsBetweenNextOptionAndIndex(int index) {
        List<String> inputs = new ArrayList<>();
        while((index < this.commandLineArgs.size()) && (!this.commandLineArgs.get(index).startsLikeOption())) {
            inputs.add(this.commandLineArgs.get(index ++).content);
        }
        return inputs;
    }
}