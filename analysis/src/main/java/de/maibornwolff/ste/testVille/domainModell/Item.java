package de.maibornwolff.ste.testVille.domainModell;

import de.maibornwolff.ste.testVille.vizualisationFileWriting.Writable;

import java.util.*;

public abstract class Item implements Comparable<Item> {

    private String                     key;        // key from export
    private int                        localId;  // from this application generated key
    private String                     name;
    private String                     priority;
    private final Map<String, String>  untranslatableFields; // Example: reporter, projectName
    private final List<String>         associatedItemKeys;
    public static Map<String, Integer> priorityRanking = new HashMap<>();

    public Item (int localId) {
        this.localId              = localId;
        this.associatedItemKeys   = new ArrayList<>();
        this.untranslatableFields = new HashMap<>();
    }

    public int getLocalId() {
        return this.localId;
    }

    Map<String, String> getUntranslatableFields() {
        return this.untranslatableFields;
    }

    public void addNewUntranslatableField(String fieldName, String untranslatableValue) {
        this.untranslatableFields.putIfAbsent(fieldName, untranslatableValue);
    }

    private void addAssociatedElementKey(String element) {
        this.associatedItemKeys.add(element);
    }

    public void addAssociatedItemKeys(String ... xs) {
        Arrays.stream(xs)
            .filter(s -> (s != null) && !s.trim().equals(""))
            .map(String::trim)
            .forEach(this::addAssociatedElementKey);
    }

    public List<String> getAssociatedItemKeys() {
        return associatedItemKeys;
    }

    public String getKey() {
        return key;
    }

    public String getName() {
        return name;
    }

    public String getPriority() {
        return priority;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setName(String name) {
        if(name == null || name.trim().isEmpty()){
            this.name = "No_Name";
            return;
        }
        this.name = name.trim();
    }

    public void setPriority(String priority) {
        this.specifyItemPriorityRank(priority);
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

    public boolean isItemInvalid() {
        return (this.priority == null)  || (this.name == null) || (this.key == null);
    }

    public ItemTyp getItemTyp() {
        return ItemTyp.ITEM;
    }

    @Override
    public int compareTo(Item item) {
        return this.getKey().compareTo(item.getKey());
    }

    public String getUntranslatableFieldsAsString() {
        Set<Map.Entry<String, String>> entries = this.getUntranslatableFields().entrySet();
        return produceStringRepresentationOfFields(entries);
    }

    private static String produceStringRepresentationOfFields(Set<Map.Entry<String, String>> fields) {
        return fields.stream()
                .sorted(Comparator.comparing(Map.Entry::getKey))
                .map(x -> Writable.produceEntryString(x.getKey(), x.getValue(),false))
                .reduce("", (x, y) ->  x.concat(y).concat(", "));
    }

    private void specifyItemPriorityRank(String itemPriority) {

        if(itemPriority == null) {
            this.priority = "0::";
            return;
        }

        if(itemPriority.contains("::")) {
            this.priority = itemPriority;
            return;
        }

        Integer itemRank = priorityRanking.get(itemPriority);
        if (itemRank == null) {
            this.priority = "0:: " + itemPriority;
            return;
        }
        this.priority = itemRank + ":: " + itemPriority;
    }
}