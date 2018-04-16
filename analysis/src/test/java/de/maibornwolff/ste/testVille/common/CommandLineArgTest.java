package de.maibornwolff.ste.testVille.common;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class CommandLineArgTest {

    @Test
    @DisplayName("Should create an invalid Option")
    void createdOptionMustBeInvalid() {
        // arrange
        String commandLineArgAsString = "hallo";
        CommandLineArg arg = new CommandLineArg(commandLineArgAsString);

        // act
        Option createdOption = arg.toOption();

        // assert
        assertEquals(false, createdOption.isValidOption(), "But it's valid!");

    }

    @Test
    @DisplayName("Should create an invalid Option")
    void createdOptionMustBeInvalid2() {
        // arrange
        String commandLineArgAsString = "-hallo";
        CommandLineArg arg = new CommandLineArg(commandLineArgAsString);

        // act
        Option createdOption = arg.toOption();

        // assert
        assertEquals(false, createdOption.isValidOption(), "But it's valid!");

    }

    @Test
    @DisplayName("Should create an valid Option")
    void createdOptionMustBeValid() {
        // arrange
        String commandLineArgAsString = "-alm";
        CommandLineArg arg = new CommandLineArg(commandLineArgAsString);

        // act
        Option createdOption = arg.toOption();

        // assert
        assertEquals(true, createdOption.isValidOption(), "But it's invalid!");

    }

    @Test
    @DisplayName("Should create an valid output-option")
    void createdOptionMustValidOutputOption() {
        // arrange
        String commandLineArgAsString = "-o";
        CommandLineArg arg = new CommandLineArg(commandLineArgAsString);

        // act
        Option createdOption = arg.toOption();

        // assert
        assertEquals(true, createdOption.isValidOption(), "But it's invalid!");

    }

}