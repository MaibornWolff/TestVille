package de.maibornwolff.ste.testVille.inputFileParsing.hpALM.component;

import de.maibornwolff.ste.testVille.inputFileParsing.hpALM.configFile.ConfigurationHandler;

public abstract class Component {

    private int    id;
    private int    localID;    // the id we give the Component.
    private String name;      // name of the Component.
    private String priority; // the priority of the Component.

    /**
     * To build a new Component.
     * @param id       the id of the Component.
     */
    Component(int id) {
        this.id       = id;
        this.name     = "";
        this.priority = "";
        this.localID  = 0;
    }

    /**
     *  this method invoke the correspondent setter.
     * @param attrName the name of the attribute.
     * @param value     the new value to be set.
     */
    public void setComponentAttributes(String attrName, String value) {

        if(attrName.contains("Id")) {

            if(value.isEmpty()) value = "0";
            this.setId(Integer.parseInt(value));

        }else if(attrName.contains("Name")) {

            if(value.isEmpty()) value = "No_Name";
            this.setName(ConfigurationHandler.toConformStr(value));

        }else if(attrName.contains("Priority")) {
            this.setPriority(ConfigurationHandler.toConformStr(value));
        }

    }

    /**
     * Getter for id.
     * @return return the id of the Component.
     */
    int getId() {
        return id;
    }

    /**
     * Setter for id.
     * @param id the new id of this Component.
     */
    public void setId(int id) {
        this.id = id;
    }

    /**
     * Setter for name.
     * @param name the new name to be set.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Setter for priority.
     * @param priority the new priority to be set.
     */
    void setPriority(String priority) {
        this.priority = priority;
    }

    /**
     * Getter for name.
     * @return the name of the Component as String.
     */
    public String getName() {
        return this.name;
    }

    /**
     * Getter for priority.
     * @return return the priority of the Component as String.
     */
    public String getPriority() {
        return this.priority;
    }

    /**
     * Setter for localID.
     */
    public void setLocalID(int lid) {
        this.localID = lid;
    }

    /**
     * Getter for localID
     * @return localID of this Component.
     */
    int getLocalID() {
        return this.localID;
    }

    static String correctPriorityName(String priorityName) {
        String c = ""+priorityName.charAt(0);
        return c+"::"+priorityName;
    }

    static String placeInQuote(String str) {
        return "\"" + str + "\"";
    }

}