package de.maibornwolff.ste.tesla.managementTools.jiraXRAY.component;

import de.maibornwolff.ste.tesla.vizualisationFileWriter.Writable;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Epic extends Item implements Writable{

    private final Set<TestCase> associatedItems;

    public Epic() {
        super();
        this.associatedItems = new TreeSet<>();
    }

    public void addAllAssociatedElements(Collection<TestCase> newAssociatedElements) {
        for (TestCase item: newAssociatedElements) {
            this.addAssociatedElement(item);
        }
    }

    private void addAssociatedElement(TestCase newAssociatedElement) {
        this.associatedItems.add(newAssociatedElement);
    }

    public Set<TestCase> getAssociatedItems() {
        return this.associatedItems;
    }

    @Override
    public String toString() {
        return "("+this.getKey() + "|" + this.getName()+")";
    }

    @Override
    public ItemTyp getItemTyp() {
        return ItemTyp.EPIC;
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
    public Map<String, Integer> getMetricsToBeWrite(Map<String, Map<String, Integer>> translationMap) {
        return null;
    }

    @Override
    public Map<String, String> getUntranslatableProperties() {
        return this.getUntranslatableFields();
    }
}