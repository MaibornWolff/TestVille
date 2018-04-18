package de.maibornwolff.ste.testVille.domainModell;

import java.util.*;

public abstract class Item implements Comparable<Item> {

    private String                     key;        // key from export
    private int                        localId;  // from this application generated key
    private String                     name;
    private String                     priority;
    private final List<String>         associatedItemKeys;
    public Maintenance                 maintenance;
    public static Map<String, Integer> priorityRanking = new HashMap<>();

    public Item (int localId) {
        this.localId              = localId;
        this.associatedItemKeys   = new ArrayList<>();
        this.maintenance          = Maintenance.getDefaultMaintenance();
    }

    public int getLocalId() {
        return this.localId;
    }

    String getMaintenanceInfo() {
        return this.maintenance.toString()+",";
    }

    public void addAssociatedItemKeys(String ... xs) {
        Arrays.stream(xs)
            .filter(s -> (s != null) && !s.trim().equals(""))
            .map(String::trim)
            .forEach(this::addAssociatedElementKey);
    }

    private void addAssociatedElementKey(String element) {
        this.associatedItemKeys.add(element);
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
        return Objects.hash(key, name, priority);
    }

    public boolean isInvalid() {
        return (this.priority == null)  || (this.name == null) || (this.key == null);
    }

    public ItemTyp getType() {
        return ItemTyp.ITEM;
    }

    @Override
    public int compareTo(Item item) {
        return this.getKey().compareTo(item.getKey());
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

        Integer itemRank = priorityRanking.get(itemPriority.toLowerCase().trim());
        if (itemRank == null) {
            this.priority = "0:: " + itemPriority;
            return;
        }
        this.priority = itemRank + ":: " + itemPriority;
    }
}