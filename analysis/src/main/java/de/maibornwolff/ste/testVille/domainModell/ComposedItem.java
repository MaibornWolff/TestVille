package de.maibornwolff.ste.testVille.domainModell;

import de.maibornwolff.ste.testVille.common.CollectionVisitor;
import de.maibornwolff.ste.testVille.vizualisationFileWriting.Writable;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ComposedItem extends Item implements Writable{

    public Set<Item> associatedItems;

    public ComposedItem(int localKey) {
        super(localKey);
        this.associatedItems = new TreeSet<>();
    }

    public void addAssociatedItems(Collection<? extends Item> newAssociatedElements) {
        for (Item newAssociatedElement : newAssociatedElements) {
            this.addAssociatedItemIfAbsent(newAssociatedElement);
        }
    }

    public void addAssociatedItems(Item... newAssociatedElements) {
        for (Item item: newAssociatedElements) {
            this.addAssociatedItemIfAbsent(item);
        }
    }

    private void addAssociatedItemIfAbsent(Item newAssociatedElement) {
        ComposedItem group = this.getItemGroupWithPriority(newAssociatedElement.getPriority());
        if(group == null) {
            group = buildGroupOfItem(newAssociatedElement);
            this.associatedItems.add(group);
            return;
        }
        group.associatedItems.add(newAssociatedElement);
    }

    private ComposedItem buildGroupOfItem(Item initialElement) {
        ComposedItem newGroup = new ComposedItem(-initialElement.getLocalId());
        newGroup.associatedItems.add(initialElement);
        newGroup.setName("groupedItem");
        newGroup.setPriority(initialElement.getPriority());
        newGroup.setKey(initialElement.getPriority());
        return newGroup;
    }

    private ComposedItem getItemGroupWithPriority(String priority) {
        Collection<Item> groups = CollectionVisitor.filterAndMap(this.associatedItems, x -> x.getPriority().equals(priority), Function.identity());
        if(groups.isEmpty()) {
            return null;
        }
        return (ComposedItem) groups.iterator().next();
    }

    public Set<Item> getAssociatedItems() {
        return this.associatedItems;
    }

    @Override
    public String toString() {
        return "("+this.getKey() + "|" + this.getName()+")";
    }

    @Override
    public ItemTyp getType() {
        return ItemTyp.COMPOSEDITEM;
    }


    @Override
    public List<Writable> getWritableChildren() {
        Stream<Writable> writableChildren = this.getAssociatedItems().stream().map(x -> (Writable) x).sorted();
        return writableChildren.collect(Collectors.toList());
    }

    @Override
    public String getWritablePriority() {
        return this.getPriority();
    }

    @Override
    public String getWritableID() {
        return ""+this.getLocalId();
    }

    @Override
    public String getWritableName() {
        return this.getName() + " (id: "+this.getKey()+")";
    }

    @Override
    public String getWritableType() {
        return this.getType().name();
    }

    @Override
    public StringBuilder getWritableMetricsAsString() {
        return new StringBuilder("\"attributes\": {}");
    }

    @Override
    public String getWritableUntranslatableFieldsAsString() {
        return super.getMaintenanceInfo();
    }
}