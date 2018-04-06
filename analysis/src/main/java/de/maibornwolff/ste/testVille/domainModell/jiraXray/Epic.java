package de.maibornwolff.ste.testVille.domainModell.jiraXray;

import de.maibornwolff.ste.testVille.domainModell.ComposedItem;
import de.maibornwolff.ste.testVille.domainModell.ItemTyp;

import java.util.LinkedList;
import java.util.List;

public class Epic extends ComposedItem {

    public Epic(int localKey) {
        super(localKey);
    }

    @Override
    public ItemTyp getItemTyp() {
        return ItemTyp.EPIC;
    }

}