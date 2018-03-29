package de.maibornwolff.ste.testVille.domainModell;

import de.maibornwolff.ste.testVille.domainModell.hpALM.Requirement;
import de.maibornwolff.ste.testVille.vizualisationFileWriting.Writable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComposedItem extends Item implements Writable{

    private Set<Item> associatedItems;

    public ComposedItem(int localKey) {
        super(localKey);
        this.associatedItems = new TreeSet<>();
    }

    public void setAssociatedItems(Set<Item> associatedItems) {
        this.associatedItems = associatedItems;
    }

    public void addAllAssociatedItems(Collection<Item> newAssociatedElements) {
        for (Item newAssociatedElement : newAssociatedElements) {
            this.addAssociatedItemIfAbsent(newAssociatedElement);
        }
    }

    public void addAllAssociatedItems(Item... newAssociatedElements) {
        Arrays.stream(newAssociatedElements).forEach(this::addAssociatedItemIfAbsent);
    }

    private void addAssociatedItemIfAbsent(Item newAssociatedElement) {
        this.associatedItems.add(newAssociatedElement);
    }

    private boolean doesRequirementContainsTestCase(Item maybeAlreadyContained) {
        return this.getAssociatedItems().stream().anyMatch(x -> x.equals(maybeAlreadyContained));
    }

    public Set<Item> getAssociatedItems() {
        return this.associatedItems;
    }

    @Override
    public String toString() {
        return "("+this.getKey() + "|" + this.getName()+")";
    }

    @Override
    public ItemTyp getItemTyp() {
        return ItemTyp.COMPOSEDITEM;
    }


    @Override
    public List<Writable> getWritableChildren() {
        Stream<Writable> writableChildren = this.getAssociatedItems().stream().map(x -> (Writable) x);
        return writableChildren.collect(Collectors.toList());
    }

    @Override
    public String getWritablePriority() {
        return this.getPriority();
    }

    @Override
    public String getWritableID() {
        return this.getKey();
    }

    @Override
    public String getWritableName() {
        return this.getName();
    }

    @Override
    public String getWritableType() {
        return this.getItemTyp().name();
    }

    @Override
    public StringBuilder getWritableMetricsAsString() {
        return new StringBuilder("\"attributes\": {}");
    }
}