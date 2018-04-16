package de.maibornwolff.ste.testVille.inputFileParsing;

import de.maibornwolff.ste.testVille.domainModell.Item;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public interface Parser {
    Collection<Item> parse(String fileToParse, String configurationFile) throws Exception;

    default  <A> List<Item> castToItems(Collection<A> listOfAs) {
        return listOfAs.stream().map(x -> (Item) x).collect(Collectors.toList());
    }
}
