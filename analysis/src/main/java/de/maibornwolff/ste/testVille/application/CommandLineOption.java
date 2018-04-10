package de.maibornwolff.ste.testVille.application;

/**
 * Enumeration of all option of the analysis module.
 *
 * @since 2.0.0
 *
 * (c) maibornwolff, TestVille, 2018
 */
public enum CommandLineOption {
    HELP, H,       // Help option.
    ALM, XRAY,    // Supported management tools options.
    INPUT, I,    // InputOption (export file).
    OUTPUT, O,  // OutputOption (visualization file target).
    CONFIG, C, DEFAULTCONFIG, // Configuration file option (config file path).
    INVALID
}
