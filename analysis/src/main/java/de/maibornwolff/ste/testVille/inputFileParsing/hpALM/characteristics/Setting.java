package de.maibornwolff.ste.testVille.inputFileParsing.hpALM.characteristics;

import java.util.Objects;

public class Setting implements Characteristic{

    private final String settingName;
    private final String defaultValue;
    private final int    column;

    /**
     * To create a new setting.
     * @param settingName name of the setting.
     * @param defaultValue the default value of the setting.
     * @param column column num of the setting (in the excel export).
     */
    public Setting(String settingName, String defaultValue, int column) {
        this.settingName  = settingName;
        this.defaultValue = defaultValue;
        this.column       = column;
    }


    /**
     * @return the string representation of a setting.
     */
    @Override
    public String toString() {
        return "Setting("+this.settingName+", "+this.defaultValue +", "+this.column+")";
    }


    /**
     * Getter for defaultValue.
     * @return the default value of the setting
     */
    public String getDefaultValue() {
        return defaultValue;
    }

    /**
     * Getter for settingName.
     * @return the name of a setting.
     */
    @Override
    public String getCharacteristicName() {
        return settingName;
    }


    /**
     * Getter for column.
     * @return the column num, where we can found the value of setting in the excel-file.
     */
    @Override
    public int getColumn() { return this.column;}


    @Override
    public boolean equals(Object obj) {

        if(obj instanceof Setting){
            Setting s = (Setting)obj;
            return (this.column == s.column) &&
                    (this.getDefaultValue().equals(s.getDefaultValue())) &&
                    (this.getCharacteristicName().equals(s.getCharacteristicName()))
            ;
        }

        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hash(settingName, defaultValue, column);
    }
}