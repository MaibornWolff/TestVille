package de.maibornwolff.ste.testVille.common;

import de.maibornwolff.ste.testVille.application.CommandLineOption;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class CommandLineArg extends ValueObject<String> {

    public CommandLineArg(String content) {
        super(content);
    }

    @Override
    public boolean equals(Object o) {
        return (o instanceof CommandLineArg) && (this.compareTo(o) == 0);
    }

    @Override
    public int compareTo(Object o) {
        if(o instanceof CommandLineArg) {
            CommandLineArg arg = (CommandLineArg) o;
            return this.content.compareTo(arg.content);
        }
        return -1;
    }

    public boolean startsLikeOption() {
        return this.content.trim().startsWith("-");
    }

    public Option toOption() {
        String correctedOptionString = removeOptionCharacters(this.content.trim());
        List<CommandLineOption> toOptionStringMatchedOptions = getAllKnownCommandLineOptions()
                .stream()
                .filter(x -> x.toString().compareToIgnoreCase(correctedOptionString) == 0)
                .collect(Collectors.toList());
        if(!toOptionStringMatchedOptions.isEmpty()) return new Option(toOptionStringMatchedOptions.get(0));
        return new Option(CommandLineOption.INVALID);
    }

    private static String removeOptionCharacters(String str) {
        while((!Objects.isNull(str)) && str.startsWith("-")){ str = str.substring(1); }
        return str;
    }

    private static Set<CommandLineOption> getAllKnownCommandLineOptions() {
        return Set.of(CommandLineOption.values());
    }
}
