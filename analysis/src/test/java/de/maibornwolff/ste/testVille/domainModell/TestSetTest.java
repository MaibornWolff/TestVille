package de.maibornwolff.ste.testVille.domainModell;

import de.maibornwolff.ste.testVille.domainModell.jiraXray.TestSet;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;
import java.util.Collection;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class TestSetTest {

    @TestFactory
    Collection<DynamicTest> correctnessCheck() {
        //arrange
        TestSet set = new TestSet(-1);

        // act & assert
        set.addAssociatedElementKeys("haha", "hihi", "hoho");

        DynamicTest dt1 = DynamicTest.dynamicTest("Itemtyp check",
                () -> assertEquals(ItemTyp.TESTSET, set.getItemTyp()))
        ;

        DynamicTest dt2 = DynamicTest.dynamicTest("associated elements check",
                () -> assertEquals(true, set.getAssociatedElementKeys().contains("hihi")))
        ;

        DynamicTest dt3 = DynamicTest.dynamicTest("associated elements number check",
                () -> assertEquals(3, set.getAssociatedElementKeys().size()))
                ;

        return List.of(dt1, dt2, dt3);
    }

}