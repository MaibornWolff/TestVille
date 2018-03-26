package de.maibornwolff.ste.testVille.managementTools.hpALM.characteristics;

/**
 *
 * @author jessyn
 * Param is an Info about a testCase we want to now.
 */
public interface Characteristic {

    /**
     * @return the name of the characteristic.
     */
    String getCharacteristicName();

    /**
     * @return the column num of a characteristic.
     */
    int getColumn();

    /**
     * @return return a string representation of a param.
     */
    @Override
    String toString();

}
