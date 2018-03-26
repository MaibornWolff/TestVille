package de.maibornwolff.ste.testVille.managementTools.hpALM.characteristics;

import de.maibornwolff.ste.testVille.managementTools.common.Pair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.function.Predicate;

public class Property implements Characteristic {

    private final String                propertyName;
    private final int                   column;
    private String                      defaultValue;
    private List<Pair<String, Integer>> mappingList;

    /**
     * To construct a new Property.
     * @param propertyName the name of the property.
     * @param defaultValue the default value of the property.
     * @param column column num of the property in the excel export.
     */
    public Property(String propertyName, String defaultValue,int column) {
        this.propertyName = propertyName;
        this.defaultValue = defaultValue;
        this.column       = column;
        this.mappingList  = new ArrayList<>();
    }

    public void completeMappingList(Pair<String, Integer>  newMappableValues) {
        this.mappingList.add(newMappableValues);
    }

    public List<Pair<String, Integer>> getMappingList() {
        return mappingList;
    }

    public void setMappingList(List<Pair<String, Integer>> newList) {
        this.mappingList = newList;
    }

    /**
     * This function return the first Pair in mappingList that satisfy predicate.
     * @param predicate to be satisfied.
     * @return the first Pair that satisfy predicate or null.
     */
    public Pair<String, Integer> selectPairWithProp(Predicate<Pair<String, Integer>> predicate) {

        for(Pair<String,  Integer> p: this.mappingList) {
            if(predicate.test(p))  return p;
        }

        return null;
    }

    /**
     * @return a string representation of a property
     */
    @Override
    public String toString() {
        return "Property("+this.propertyName+", "+this.column+", "+this.mappingList.toString()+", "+this.defaultValue+")";
    }

    /**
     * @return the name of a property
     */
    @Override
    public String getCharacteristicName() {
        return propertyName;
    }

    /**
     * @return the column num of a property
     */
    public int getColumn() {
        return column;
    }

    /**
     * @return the default value of the property.
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Setter for defaultValue.
     * @param defaultValue: new default value.
     */
    public void setDefaultValue(String defaultValue) {
        this.defaultValue = defaultValue;
    }

    @Override
    public boolean equals(Object obj){

        if (obj instanceof Property){
            Property prop = (Property) obj;
            return this.propertyName.equals(prop.propertyName) &&
                    this.defaultValue.equals(prop.defaultValue)&&
                    this.column == prop.column
            ;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(propertyName, column, defaultValue, mappingList);
    }
}
