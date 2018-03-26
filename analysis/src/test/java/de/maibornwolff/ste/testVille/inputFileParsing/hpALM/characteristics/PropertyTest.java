package de.maibornwolff.ste.testVille.inputFileParsing.hpALM.characteristics;

import de.maibornwolff.ste.testVille.inputFileParsing.common.Pair;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.util.Collection;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PropertyTest {

    private Property property;

    @BeforeEach
    void setUp() { // global arrange
        this.property = new Property("name", "15", 2);
        this.property.setMappingList(List.of(new Pair<>("hallo", 2),
                new Pair<>("take", 34),
                new Pair<>("ha", 90),
                new Pair<>("monad", 23),
                new Pair<>("io", 223),
                new Pair<>("world", 111))
        );
    }


    @TestFactory
    Collection<DynamicTest> selectionCheck() {

        List<DynamicTest> testChain;

        DynamicTest dt1 = DynamicTest.dynamicTest("found the pair with first == take",
                () -> assertEquals(new Pair<>("take", 34),
                        property.selectPairWithProp(x -> x.getFirst().equals("take")))
                )
        ;

        DynamicTest dt2 = DynamicTest.dynamicTest("found the pair with second == 23",
                () -> assertEquals(new Pair<>("monad", 23),
                        property.selectPairWithProp(x -> x.getSecond() == 23))
                )
        ;

        DynamicTest dt3 = DynamicTest.dynamicTest("found the pair with second >= 50",
                () -> assertEquals(new Pair<>("ha", 90),
                        property.selectPairWithProp(x -> x.getSecond() >= 50))
                )
        ;

        DynamicTest dt4 = DynamicTest.dynamicTest("found the pair with first.lastChar = 'd'",
                () -> assertEquals(new Pair<>("monad", 23),
                        property.selectPairWithProp(x -> x.getFirst().endsWith("d")))
                )
        ;

        DynamicTest dt5 = DynamicTest.dynamicTest("found the pair with first == take",
                () -> assertEquals(new Pair<>("world", 111),
                        property.selectPairWithProp(x -> x.getFirst().endsWith("d") && x.getSecond() >= 60))
                )
        ;

        DynamicTest dt6 = DynamicTest.dynamicTest("found nothing",
                () -> assertEquals(null,
                        property.selectPairWithProp(x -> x.getFirst().endsWith("PP")))
                )
        ;

        testChain = List.of(dt1, dt2, dt3, dt4, dt5, dt6);
        return testChain;
    }

    @Test
    void propertyNameCheck(){
        // assert
        assertEquals("name",
                property.getCharacteristicName(),
                "The name of the property is incorrect")
        ;
    }

    @Test
    void PropertyColumnNumCheck() {
        // assert
        assertEquals(2,
                property.getColumn(),
                "The column num of the property is incorrect")
        ;
    }

    @Test
    void propertyDefaultValueCheck() {
        // assert
        assertEquals("15",
                property.getDefaultValue(),
                "The default value of the property is incorrect")
        ;
    }

    @Test
    void setDefaultValueCheck() {
        //act
        property.setDefaultValue("hey");

        // assert
        assertEquals("hey",
                property.getDefaultValue(),
                "The default value of the property is incorrect")
        ;
    }

    @TestFactory
    Collection<DynamicTest> equalsCheck() {

        DynamicTest dt1 = DynamicTest.dynamicTest("the properties must be equals",
                () -> assertEquals(true, property.equals(new Property("name", "15", 2))))

        ;

        DynamicTest dt2 = DynamicTest.dynamicTest("the properties must be different",
                () -> assertEquals(false, property.equals(new Property("", "2", 4))))

        ;

        Characteristic sett = new Setting("", "2", 4);
        DynamicTest dt3 = DynamicTest.dynamicTest("the settings must be different",
                () -> assertEquals(false, property.equals(sett)))
        ;

        return List.of(dt1, dt2, dt3);
    }

    @Test
    void toStringTest() {
        //arrange
        String expected = "Property(name, 2, [[hallo, 2], [take, 34], [ha, 90], [monad, 23], [io, 223], [world, 111]], 15)";

        //assert
        assertEquals(expected,
                property.toString(),
                "The string representation of the property is incorrect")
        ;
    }


}