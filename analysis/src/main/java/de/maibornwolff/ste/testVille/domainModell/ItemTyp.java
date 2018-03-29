package de.maibornwolff.ste.testVille.domainModell;

public enum ItemTyp {
    TESTCASE, ITEM, COMPOSEDITEM,  // this type of item concern hpAlm & jiraXray
    TESTSET, EPIC, TESTEXECUTION, // this types of item concern only jiraXray
    REQUIREMENT                  // this type concern only hpAlm
}