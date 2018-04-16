package de.maibornwolff.ste.testVille.common;

import de.maibornwolff.ste.testVille.application.CommandLineOption;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class Option extends ValueObject<CommandLineOption> {

    public Option(CommandLineOption content) {
        super(content);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof Option) && (this.compareTo(o) == 0);
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof Option) {
            Option opt = (Option) o;
            return this.content.compareTo(opt.content);
        }
        return -1;
    }

    public boolean isExportOriginOption() {
        return (this.content == CommandLineOption.ALM) || (this.content == CommandLineOption.XRAY);
    }

    public boolean isDefaultConfiguration() {
        return this.content == CommandLineOption.DEFAULTCONFIG;
    }

    public boolean isValidOption() {
        return this.content != CommandLineOption.INVALID;
    }

    public boolean isOutputFileOption() {
        return (this.content == CommandLineOption.O) || (this.content == CommandLineOption.OUTPUT);
    }

    public boolean isConfigFileOption() {
        return (this.content == CommandLineOption.C) || (this.content == CommandLineOption.CONFIG);
    }

    public boolean isInputFileOption() {
        return (this.content == CommandLineOption.I) || (this.content == CommandLineOption.INPUT);
    }

    public boolean isHelpOption() {
        return (this.content == CommandLineOption.H) || (this.content == CommandLineOption.HELP);
    }

}