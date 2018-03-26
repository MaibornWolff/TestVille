package de.maibornwolff.ste.testVille.managementTools.hpALM.characteristics;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


class SettingTest {

    private Setting setting;

    @BeforeEach
    void setUp() {
        setting = new Setting("test", "23", 1);
    }

    @Test
    void toStringTest() {
        //assert
        assertEquals("Setting(test, 23, 1)",
                setting.toString(),
                "The string representation of the Setting-object is incorrect")
        ;
    }

    @Test
    void defaultValueCheck() {
        //assert
        assertEquals("23",
                setting.getDefaultValue(),
                "The setting has an incorrect default value");
    }

    @Test
    void settingNameCheck() {
        //assert
        assertEquals("test",
                setting.getCharacteristicName(),
                "The Name of the setting is incorrect")
        ;
    }

    @Test
    void columnNumCheck() {
        //assert
        assertEquals(1,
                setting.getColumn(),
                "The column num of the setting is incorrect")
        ;
    }

    @TestFactory
    Collection<DynamicTest> equalsCheck() {

        DynamicTest dt1 = DynamicTest.dynamicTest("the settings must be equals",
                () -> assertEquals(true, setting.equals(new Setting("test", "23", 1))))
        ;

        DynamicTest dt2 = DynamicTest.dynamicTest("the settings must be different",
                () -> assertEquals(false, setting.equals(new Setting("", "2", 4))))
        ;

        Characteristic prop = new Property("", "2", 4);
        DynamicTest dt3 = DynamicTest.dynamicTest("the settings must be different",
                () -> assertEquals(false, setting.equals(prop)))
        ;
        return List.of(dt1, dt2, dt3);
    }
}