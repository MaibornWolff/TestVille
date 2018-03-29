package de.maibornwolff.ste.tesla.managementTools.jiraXRAY.component;

import java.util.*;

public abstract class Item implements Comparable<Item>{

    private String              key;
    private String              name;
    private String              priority;
    private final Map<String, String> untranslatableFields;
    private final List<String>  associatedElementKeys;

    Item () {
        this.associatedElementKeys = new ArrayList<>();
        this.untranslatableFields  = new HashMap<>();
    }

    /**
     * Getter for the attribute {@link #untranslatableFields}.
     * @return this.untranslatableFields
     */
    Map<String, String> getUntranslatableFields() {
        return this.untranslatableFields;
    }

    /**
     * This method add a new untranslatable field to the current item.
     * @param fieldName          : Name of the field.
     * @param untranslatableValue: untranslatable value of the field.
     */
    public void addNewUntranslatableField(String fieldName, String untranslatableValue) {
        this.untranslatableFields.putIfAbsent(fieldName, untranslatableValue);
    }

    /**
     * This function a new associated elements to this LinkedItem.
     * @param element Keys of a element that have a link to this LinkedItem.
     */
    private void addAssociatedElementKey(String element) {
        this.associatedElementKeys.add(element);
    }

    /**
     * This function adds new associated elements to this LinkedItem.
     * @param xs Keys of elements that have a link to this LinkedItem.
     */
    public void addAssociatedElementKeys(String ... xs) {
        for(String x: xs) {
            if((x != null) && (!x.trim().isEmpty())) this.addAssociatedElementKey(x.trim());
        }
    }

    /**
     * Getter for the attribute {@link #associatedElementKeys}.
     * @return this.associatedElementKeys
     */
    public List<String> getAssociatedElementKeys() {
        return associatedElementKeys;
    }

    /**
     * Getter for the attribute {@link #key}.
     * @return this.key
     */
    public String getKey() {
        return key;
    }

    /**
     * Getter for the attribute {@link #name}.
     * @return this.name
     */
    public String getName() {
        return name;
    }

    /**
     * Getter for the attribute {@link #priority}.
     * @return this.priority
     */
    public String getPriority() {
        return priority;
    }

    /**
     * Setter for the attribute {@link #key}.
     * @param key New key of this Item.
     */
    public void setKey(String key) {
        this.key = key;
    }

    /**
     * Setter for the attribute {@link #name}.
     * @param name New name of this Item.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for the attribute {@link #priority}.
     * @param priority New priority of this Item.
     */
    public void setPriority(String priority) {
        this.priority = priority;
    }

    @Override
    public boolean equals(Object object) {
        if(! (object instanceof Item)) return false;
        Item item = (Item)object;
        return this.key.equals(item.key) && this.name.equals(item.name) && this.priority.equals(item.priority);
    }

    @Override
    public int hashCode() {
        return Objects.hash(key, name, priority, untranslatableFields, associatedElementKeys);
    }

    /**
     * This function checks if an the item is invalid.
     * @return True if the item contains is consistent else false.
     */
    public boolean isItemInvalid() {
        return (this.priority == null)  || (this.name == null) || (this.key == null);
    }

    /**
     * This function return the typ of this item.
     * available itemTypes: TESTCASE, TESTSET, STORY, TESTEXECUTION.
     * @return ItemTyp.
     */
    public abstract ItemTyp getItemTyp();

    @Override
    public int compareTo(Item item) {
        return this.getKey().compareTo(item.getKey());
    }
}