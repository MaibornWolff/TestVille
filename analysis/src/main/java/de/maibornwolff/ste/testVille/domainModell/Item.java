package de.maibornwolff.ste.testVille.domainModell;

import de.maibornwolff.ste.testVille.vizualisationFileWriting.Writable;

import java.util.*;

public abstract class Item implements Comparable<Item> {

    private String                    key;        // key from export
    private int                       localId;  // from this application generated key
    private String                    name;
    private String                    priority;
    private final Map<String, String> untranslatableFields; // Example: reporter, projectName
    private final List<String>        associatedItemKeys;

    public Item (int localId) {
        this.localId              = localId;
        this.associatedItemKeys   = new ArrayList<>();
        this.untranslatableFields = new HashMap<>();
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
        this.associatedItemKeys.add(element);
    }

    /**
     * This function adds new associated elements to this LinkedItem.
     * @param xs Keys of elements that have a link to this LinkedItem.
     */
    public void addAssociatedItemKeys(String ... xs) {
        Arrays.stream(xs)
            .filter(s -> (s != null) && !s.trim().equals(""))
            .map(String::trim)
            .forEach(this::addAssociatedElementKey);
    }

    /**
     * Getter for the attribute {@link #associatedItemKeys}.
     * @return this.associatedItemKeys
     */
    public List<String> getAssociatedItemKeys() {
        return associatedItemKeys;
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
        String n = name.trim();
        if(n.isEmpty()) this.name = "No_Name";
        this.name = n;
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
        return Objects.hash(key, name, priority, untranslatableFields, associatedItemKeys);
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
    public ItemTyp getItemTyp() {
        return ItemTyp.ITEM;
    }

    @Override
    public int compareTo(Item item) {
        return this.getKey().compareTo(item.getKey());
    }

    public String getWritableUntranslatableFieldsAsString() {
        Set<Map.Entry<String, String>> entries = this.getUntranslatableFields().entrySet();
        return produceStringRepresentationOfFields(entries);
    }

    public static  <A, B> String produceStringRepresentationOfFields(Set<Map.Entry<A, B>> fields) {
        return fields.stream()
                .map(x -> Writable.produceEntryString(x.getKey().toString(), x.getValue().toString(),false))
                .reduce("", (x, y) -> x.isEmpty() ? x.concat(y) : x.concat(", ").concat(y));
    }
}